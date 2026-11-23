package com.compiler.codegen;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.compiler.ast.Node;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.BinaryExpression;
import com.compiler.ast.expression.BinaryOperator;
import com.compiler.ast.expression.BooleanLiteral;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.FieldAccess;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.expression.IndexAccess;
import com.compiler.ast.expression.IntegerLiteral;
import com.compiler.ast.expression.MemberInitialization;
import com.compiler.ast.expression.NullLiteral;
import com.compiler.ast.expression.RealLiteral;
import com.compiler.ast.expression.RecordConstructor;
import com.compiler.ast.expression.RoutineCall;
import com.compiler.ast.expression.UnaryExpression;
import com.compiler.ast.expression.UnaryOperator;
import com.compiler.ast.types.ArrayType;
import com.compiler.semantic.SemanticType;

import static com.compiler.codegen.MethodAssembly.arrayInstruction;
import static com.compiler.codegen.MethodAssembly.instructionPrefix;
import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.REAL;

/** Emits expressions and leaves their values on the JVM operand stack. */
final class ExpressionGenerator {
    private final JasminProgram program;
    private final MethodAssembly method;
    private Map<VariableDeclaration, Integer> fieldReceivers =
            new IdentityHashMap<>();

    ExpressionGenerator(JasminProgram program, MethodAssembly method) {
        this.program = program;
        this.method = method;
    }

    void emit(Expression expression) {
        switch (expression) {
            case IntegerLiteral literal ->
                method.emit("ldc " + literal.value());
            case RealLiteral literal ->
                method.emit("ldc2_w " + literal.value());
            case BooleanLiteral literal ->
                method.emit(literal.value() ? "iconst_1" : "iconst_0");
            case NullLiteral ignored -> method.emit("aconst_null");
            case Identifier identifier -> emitIdentifier(identifier);
            case IndexAccess access -> {
                emitArrayAddress(access);
                SemanticType type = program.expressionType(access);
                method.emit(arrayInstruction(type, "load"));
            }
            case FieldAccess access -> emitFieldAccess(access);
            case RoutineCall call -> emitCall(call);
            case RecordConstructor constructor -> emitRecord(constructor);
            case UnaryExpression unary -> emitUnary(unary);
            case BinaryExpression binary -> emitBinary(binary);
        }
    }

    void emitConverted(Expression expression, SemanticType target) {
        SemanticType source = program.expressionType(expression);
        emit(expression);
        if (source == REAL && target == INTEGER) {
            method.emit("invokestatic java/lang/Math/round(D)J");
            method.emit("invokestatic java/lang/Math/toIntExact(J)I");
        } else if ((source == INTEGER || source == BOOLEAN) && target == REAL) {
            method.emit("i2d");
        } else if (source == INTEGER && target == BOOLEAN) {
            emitBooleanResult("ifne");
        }
    }

    void emitInitializer(VariableDeclaration variable) {
        SemanticType type = program.variableType(variable);
        if (variable.initializer().isPresent()) {
            emitConverted(variable.initializer().get(), type);
            return;
        }

        if (type instanceof SemanticType.Array array) {
            ArrayType syntax = program.arraySyntax(array);
            if (syntax != null && syntax.size().isPresent()) {
                emitConverted(syntax.size().get(), INTEGER);
                method.emit(newArrayInstruction(array.element()));
                return;
            }
        }

        if (type == INTEGER || type == BOOLEAN) {
            method.emit("iconst_0");
        } else if (type == REAL) {
            method.emit("dconst_0");
        } else {
            method.emit("aconst_null");
        }
    }

    private String newArrayInstruction(SemanticType elementType) {
        return switch (elementType) {
            case SemanticType.Scalar scalar -> switch (scalar) {
                case REAL -> "newarray double";
                case BOOLEAN -> "newarray boolean";
                case INTEGER -> "newarray int";
                default -> throw new IllegalStateException(
                        "Invalid array element type");
            };
            case SemanticType.Array nested ->
                "anewarray " + program.descriptor(nested);
            case SemanticType.Record record ->
                "anewarray " + program.recordName(record);
        };
    }

    /** Leaves the array and its zero-based JVM index on the operand stack. */
    void emitArrayAddress(IndexAccess access) {
        emit(access.target());
        emitConverted(access.index(), INTEGER);
        method.emit("iconst_1");
        method.emit("isub");
    }

    private void emitIdentifier(Identifier identifier) {
        Node declaration = program.declarationOf(identifier);
        SemanticType type = program.expressionType(identifier);
        if (program.isGlobal(declaration)) {
            String field = program.globalReference(identifier.name(), type);
            method.emit("getstatic " + field);
        } else if (declaration instanceof VariableDeclaration field
                && program.fieldOwner(field) != null) {
            String owner = program.recordName(program.fieldOwner(field));
            method.emit("aload " + fieldReceivers.get(field));
            method.emit("getfield " + program.fieldReference(owner,
                    field.name(), program.variableType(field)));
        } else {
            method.emit(instructionPrefix(type) + "load "
                    + method.localSlot(declaration));
        }
    }

    private void emitFieldAccess(FieldAccess access) {
        emit(access.target());
        SemanticType target = program.expressionType(access.target());
        if (target instanceof SemanticType.Array) {
            method.emit("arraylength");
        } else {
            String owner = program.recordName((SemanticType.Record) target);
            method.emit("getfield " + program.fieldReference(owner,
                    access.fieldName(), program.expressionType(access)));
        }
    }

    private void emitRecord(RecordConstructor constructor) {
        SemanticType.Record record =
                (SemanticType.Record) program.expressionType(constructor);
        String owner = program.recordName(record);
        int receiver = method.allocateSlot(record);
        method.emit("new " + owner);
        method.emit("dup");
        method.emit("invokespecial " + owner + "/<init>()V");
        method.emit("astore " + receiver);

        Map<String, Expression> overrides = new LinkedHashMap<>();
        for (MemberInitialization member : constructor.members()) {
            overrides.put(member.name(), member.value());
        }

        // A field default may refer to an earlier field of this instance.
        // Nested constructors must restore the receiver of the outer one.
        Map<VariableDeclaration, Integer> outerReceivers = fieldReceivers;
        fieldReceivers = new IdentityHashMap<>(outerReceivers);
        for (VariableDeclaration field : program.fields(record)) {
            fieldReceivers.put(field, receiver);
        }
        for (VariableDeclaration field : program.fields(record)) {
            method.emit("aload " + receiver);
            SemanticType type = program.variableType(field);
            Expression override = overrides.get(field.name());
            if (override == null) {
                emitInitializer(field);
            } else {
                emitConverted(override, type);
            }
            String reference =
                    program.fieldReference(owner, field.name(), type);
            method.emit("putfield " + reference);
        }
        fieldReceivers = outerReceivers;
        method.emit("aload " + receiver);
    }

    void emitCall(RoutineCall call) {
        RoutineDeclaration routine = program.routine(call.name());
        for (int index = 0; index < call.arguments().size(); index++) {
            SemanticType type =
                    program.parameterType(routine.parameters().get(index));
            emitConverted(call.arguments().get(index), type);
        }
        method.emit("invokestatic " + program.routineReference(routine));
    }

    private void emitUnary(UnaryExpression expression) {
        SemanticType type = expression.operator() == UnaryOperator.NOT
                ? BOOLEAN : program.expressionType(expression);
        emitConverted(expression.operand(), type);
        switch (expression.operator()) {
            case MINUS -> method.emit(instructionPrefix(type) + "neg");
            case NOT -> {
                method.emit("iconst_1");
                method.emit("ixor");
            }
            case PLUS -> {
                // The operand is already on the stack.
            }
        }
    }

    private void emitBinary(BinaryExpression expression) {
        SemanticType result = program.expressionType(expression);
        switch (expression.operator()) {
            case AND -> emitOperation(expression, BOOLEAN, "and");
            case OR -> emitOperation(expression, BOOLEAN, "or");
            case XOR -> emitOperation(expression, BOOLEAN, "xor");
            case ADD -> emitOperation(expression, result, "add");
            case SUBTRACT -> emitOperation(expression, result, "sub");
            case MULTIPLY -> emitOperation(expression, result, "mul");
            case DIVIDE -> emitOperation(expression, result, "div");
            case REMAINDER -> emitOperation(expression, result, "rem");
            case LESS_THAN -> emitComparison(expression, "lt");
            case LESS_THAN_OR_EQUAL -> emitComparison(expression, "le");
            case GREATER_THAN -> emitComparison(expression, "gt");
            case GREATER_THAN_OR_EQUAL -> emitComparison(expression, "ge");
            case EQUAL -> emitComparison(expression, "eq");
            case NOT_EQUAL -> emitComparison(expression, "ne");
        }
    }

    private void emitOperation(BinaryExpression expression, SemanticType type,
                               String operation) {
        emitConverted(expression.left(), type);
        emitConverted(expression.right(), type);
        method.emit(instructionPrefix(type) + operation);
    }

    private void emitComparison(BinaryExpression expression, String condition) {
        SemanticType left = program.expressionType(expression.left());
        SemanticType right = program.expressionType(expression.right());
        if (left != INTEGER && left != REAL && left != BOOLEAN) {
            emit(expression.left());
            emit(expression.right());
            emitBooleanResult("if_acmp" + condition);
            return;
        }

        SemanticType type = left == REAL || right == REAL ? REAL : INTEGER;
        emitConverted(expression.left(), type);
        emitConverted(expression.right(), type);
        if (type == REAL) {
            // Choose the NaN ordering that makes ordered comparisons false.
            BinaryOperator operator = expression.operator();
            boolean nanIsGreater = operator == BinaryOperator.LESS_THAN
                    || operator == BinaryOperator.LESS_THAN_OR_EQUAL;
            method.emit(nanIsGreater ? "dcmpg" : "dcmpl");
            emitBooleanResult("if" + condition);
        } else {
            emitBooleanResult("if_icmp" + condition);
        }
    }

    private void emitBooleanResult(String branch) {
        String trueLabel = program.newLabel();
        String endLabel = program.newLabel();
        method.emit(branch + " " + trueLabel);
        method.emit("iconst_0");
        method.emit("goto " + endLabel);
        method.label(trueLabel);
        method.emit("iconst_1");
        method.label(endLabel);
    }
}
