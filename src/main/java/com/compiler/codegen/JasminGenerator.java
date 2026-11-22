package com.compiler.codegen;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.Node;
import com.compiler.ast.declaration.BlockRoutineBody;
import com.compiler.ast.declaration.Declaration;
import com.compiler.ast.declaration.ExpressionRoutineBody;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.TypeDeclaration;
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
import com.compiler.ast.statement.Assignment;
import com.compiler.ast.statement.ExpressionSource;
import com.compiler.ast.statement.ForLoop;
import com.compiler.ast.statement.IfStatement;
import com.compiler.ast.statement.PrintStatement;
import com.compiler.ast.statement.RangeSource;
import com.compiler.ast.statement.ReturnStatement;
import com.compiler.ast.statement.RoutineCallStatement;
import com.compiler.ast.statement.ScopedBlockStatement;
import com.compiler.ast.statement.WhileLoop;
import com.compiler.ast.types.ArrayType;
import com.compiler.ast.types.RecordType;
import com.compiler.ast.types.TypeNode;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.SemanticType;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.compiler.semantic.SemanticType.Scalar.*;

/** Emits Jasmin from a validated AST and its semantic bindings. */
public final class JasminGenerator {
    private static final int STACK_LIMIT = 1024;

    private final Set<VariableDeclaration> globalVariables =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<String, RoutineDeclaration> routineDeclarations = new LinkedHashMap<>();
    private final Map<SemanticType, TypeNode> typeSyntax = new IdentityHashMap<>();
    private final Map<SemanticType.Record, String> recordClassNames = new IdentityHashMap<>();
    private final Map<VariableDeclaration, SemanticType.Record> fieldOwners = new IdentityHashMap<>();
    private final Map<VariableDeclaration, Integer> fieldReceivers = new IdentityHashMap<>();
    private final Map<Node, Integer> localSlots = new IdentityHashMap<>();
    private final Map<String, String> generatedClasses = new LinkedHashMap<>();

    private AnalysisResult analysis;
    private String programClassName;
    private StringBuilder methodInstructions;
    private SemanticType currentReturnType;
    private int nextLocalSlot;
    private int nextLabelNumber;

    /** Returns assembly by class name; analysis must describe this exact AST. */
    public Map<String, String> generateClasses(AnalysisResult analysis, String className) {
        if (!analysis.isValid()) {
            throw new IllegalArgumentException("Cannot generate code with semantic errors");
        }
        this.analysis = analysis;
        programClassName = className;
        globalVariables.clear();
        routineDeclarations.clear();
        typeSyntax.clear();
        recordClassNames.clear();
        fieldOwners.clear();
        generatedClasses.clear();
        nextLabelNumber = 0;

        // Keep syntax only for array sizes and record field initializers.
        for (var entry : analysis.resolvedTypes().entrySet()) {
            switch (entry.getKey()) {
                case ArrayType array -> typeSyntax.put(entry.getValue(), array);
                case RecordType record -> {
                    typeSyntax.put(entry.getValue(), record);
                    for (VariableDeclaration field : record.fields()) {
                        fieldOwners.put(field, (SemanticType.Record) entry.getValue());
                    }
                }
                default -> { }
            }
        }

        StringBuilder assembly = new StringBuilder(buildClassHeader(className));
        for (Declaration declaration : analysis.program().declarations()) {
            switch (declaration) {
                case VariableDeclaration variable -> {
                    globalVariables.add(variable);
                    assembly.append(".field public static \"").append(variable.name())
                            .append("\" ").append(getTypeDescriptor(getVariableType(variable)))
                            .append('\n');
                }
                case RoutineDeclaration routine -> {
                    if (routine.body().isPresent()) {
                        routineDeclarations.put(routine.name(), routine);
                    }
                }
                case TypeDeclaration ignored -> { }
            }
        }

        // The JVM executes global initializers once, in source order.
        beginMethod(VOID);
        for (Declaration declaration : analysis.program().declarations()) {
            if (declaration instanceof VariableDeclaration variable) {
                emitVariableInitializer(variable);
                emitVariableStore(variable, getVariableType(variable));
            }
        }
        emitInstruction("return");
        assembly.append(buildMethodAssembly("<clinit>", "()V"));

        for (RoutineDeclaration routine : routineDeclarations.values()) {
            beginMethod(routine.returnType().map(analysis.resolvedTypes()::get).orElse(VOID));
            for (Parameter parameter : routine.parameters()) {
                localSlots.put(parameter, allocateLocalSlot(analysis.resolvedTypes().get(parameter.type())));
            }
            switch (routine.body().orElseThrow()) {
                case BlockRoutineBody body -> generateBlock(body.block());
                case ExpressionRoutineBody body -> {
                    emitConvertedExpression(body.expression(), currentReturnType);
                    emitInstruction(getInstructionPrefix(currentReturnType) + "return");
                }
            }
            if (currentReturnType == VOID) {
                emitInstruction("return");
            } else {
                // Anchor end labels even when the verifier cannot prove a loop is infinite.
                emitInstruction("aconst_null");
                emitInstruction("athrow");
            }
            assembly.append(buildMethodAssembly(routine.name(), getRoutineDescriptor(routine)));
        }

        RoutineDeclaration entry = routineDeclarations.get("main");
        if (entry == null) {
            entry = routineDeclarations.get("Main");
        }
        if (entry != null) {
            beginMethod(VOID);
            nextLocalSlot = 1; // JVM String[] args occupies slot 0.
            for (int index = 0; index < entry.parameters().size(); index++) {
                SemanticType type = analysis.resolvedTypes().get(entry.parameters().get(index).type());
                String parser = switch (type) {
                    case SemanticType.Scalar scalar -> switch (scalar) {
                        case REAL -> "java/lang/Double/parseDouble(Ljava/lang/String;)D";
                        case BOOLEAN -> "java/lang/Boolean/parseBoolean(Ljava/lang/String;)Z";
                        case INTEGER -> "java/lang/Integer/parseInt(Ljava/lang/String;)I";
                        default -> throw new IllegalArgumentException("Entry parameters must be primitive");
                    };
                    default -> throw new IllegalArgumentException("Entry parameters must be primitive");
                };
                emitInstruction("aload_0");
                emitInstruction("ldc " + index);
                emitInstruction("aaload");
                emitInstruction("invokestatic " + parser);
            }
            emitInstruction("invokestatic " + programClassName + "/" + entry.name()
                    + getRoutineDescriptor(entry));
            discardReturnValue(entry.returnType().map(analysis.resolvedTypes()::get).orElse(VOID));
            emitInstruction("return");
            assembly.append(buildMethodAssembly("main", "([Ljava/lang/String;)V"));
        }

        generatedClasses.put(className, assembly.toString());
        return new LinkedHashMap<>(generatedClasses);
    }

    private String buildClassHeader(String className) {
        return ".class public " + className + "\n.super java/lang/Object\n";
    }

    /** Opens a method buffer; variable bindings use declaration identity, not names. */
    private void beginMethod(SemanticType returnType) {
        methodInstructions = new StringBuilder();
        nextLocalSlot = 0;
        currentReturnType = returnType;
        localSlots.clear();
        fieldReceivers.clear();
    }

    private String buildMethodAssembly(String methodName, String descriptor) {
        return "\n.method public static " + methodName + descriptor
                + "\n.limit stack " + STACK_LIMIT
                + "\n.limit locals " + Math.max(1, nextLocalSlot)
                + "\n" + methodInstructions + ".end method\n";
    }

    private void emitInstruction(String instruction) {
        methodInstructions.append("  ").append(instruction).append('\n');
    }

    private String createLabel() {
        return "L" + nextLabelNumber++;
    }

    private void emitLabel(String label) {
        methodInstructions.append(label).append(":\n");
    }

    private String getInstructionPrefix(SemanticType type) {
        if (type == VOID) {
            return "";
        }
        if (type == REAL) {
            return "d";
        }
        return type == INTEGER || type == BOOLEAN ? "i" : "a";
    }

    private String getTypeDescriptor(SemanticType type) {
        return switch (type) {
            case SemanticType.Scalar scalar -> switch (scalar) {
                case INTEGER -> "I";
                case REAL -> "D";
                case BOOLEAN -> "Z";
                case VOID -> "V";
                case NULL -> "Ljava/lang/Object;";
                case ERROR -> throw new IllegalStateException("Unvalidated semantic type");
            };
            case SemanticType.Array array -> "[" + getTypeDescriptor(array.element());
            case SemanticType.Record record -> "L" + getOrGenerateRecordClassName(record) + ";";
        };
    }

    /** Emits each nominal record class once; defaults are filled at the creation site. */
    private String getOrGenerateRecordClassName(SemanticType.Record record) {
        if (recordClassNames.containsKey(record)) {
            return recordClassNames.get(record);
        }
        String name = programClassName + "$Record" + recordClassNames.size();
        recordClassNames.put(record, name);
        StringBuilder assembly = new StringBuilder(buildClassHeader(name));
        for (VariableDeclaration field : ((RecordType) typeSyntax.get(record)).fields()) {
            assembly.append(".field public \"").append(field.name()).append("\" ")
                    .append(getTypeDescriptor(record.fields().get(field.name()))).append('\n');
        }
        assembly.append("\n.method public <init>()V\n.limit stack 1\n.limit locals 1\n")
                .append("  aload_0\n  invokespecial java/lang/Object/<init>()V\n")
                .append("  return\n.end method\n");
        generatedClasses.put(name, assembly.toString());
        return name;
    }

    private String getRoutineDescriptor(RoutineDeclaration routine) {
        String parameters = routine.parameters().stream()
                .map(parameter -> getTypeDescriptor(analysis.resolvedTypes().get(parameter.type())))
                .collect(Collectors.joining());
        String result = routine.returnType().map(analysis.resolvedTypes()::get)
                .map(this::getTypeDescriptor).orElse("V");
        return "(" + parameters + ")" + result;
    }

    private int allocateLocalSlot(SemanticType type) {
        int slot = nextLocalSlot;
        nextLocalSlot += type == REAL ? 2 : 1;
        return slot;
    }

    private void emitVariableStore(Node declaration, SemanticType type) {
        if (globalVariables.contains(declaration)) {
            VariableDeclaration variable = (VariableDeclaration) declaration;
            emitInstruction("putstatic " + programClassName + "/" + variable.name()
                    + " " + getTypeDescriptor(type));
        } else {
            emitInstruction(getInstructionPrefix(type) + "store " + localSlots.get(declaration));
        }
    }

    private SemanticType getVariableType(VariableDeclaration variable) {
        return analysis.variableTypes().get(variable);
    }

    private SemanticType getExpressionType(Expression expression) {
        return analysis.expressionTypes().get(expression);
    }

    /** Emits conversions only; assignment compatibility is checked by semantic analysis. */
    private void emitConvertedExpression(Expression expression, SemanticType targetType) {
        SemanticType sourceType = getExpressionType(expression);
        generateExpression(expression);
        if (sourceType == REAL && targetType == INTEGER) {
            emitInstruction("invokestatic java/lang/Math/round(D)J");
            emitInstruction("invokestatic java/lang/Math/toIntExact(J)I");
        } else if ((sourceType == INTEGER || sourceType == BOOLEAN) && targetType == REAL) {
            emitInstruction("i2d");
        } else if (sourceType == INTEGER && targetType == BOOLEAN) {
            emitBooleanResult("ifne");
        }
    }

    /** Converts a conditional JVM branch to a 0 or 1 stack value. */
    private void emitBooleanResult(String branchInstruction) {
        String trueLabel = createLabel();
        String endLabel = createLabel();
        emitInstruction(branchInstruction + " " + trueLabel);
        emitInstruction("iconst_0");
        emitInstruction("goto " + endLabel);
        emitLabel(trueLabel);
        emitInstruction("iconst_1");
        emitLabel(endLabel);
    }

    private void emitVariableInitializer(VariableDeclaration variable) {
        if (variable.initializer().isPresent()) {
            emitConvertedExpression(variable.initializer().get(), getVariableType(variable));
        } else {
            SemanticType type = getVariableType(variable);
            if (type instanceof SemanticType.Array array
                    && typeSyntax.get(type) instanceof ArrayType syntax && syntax.size().isPresent()) {
                emitConvertedExpression(syntax.size().get(), INTEGER);
                String element = switch (array.element()) {
                    case SemanticType.Scalar scalar -> switch (scalar) {
                        case REAL -> "newarray double";
                        case BOOLEAN -> "newarray boolean";
                        case INTEGER -> "newarray int";
                        default -> throw new IllegalStateException("Invalid array element type");
                    };
                    case SemanticType.Array nested -> "anewarray " + getTypeDescriptor(nested);
                    case SemanticType.Record record -> "anewarray " + getOrGenerateRecordClassName(record);
                };
                emitInstruction(element);
            } else {
                emitInstruction(type == REAL ? "dconst_0"
                        : type == INTEGER || type == BOOLEAN ? "iconst_0" : "aconst_null");
            }
        }
    }

    private String getArrayInstruction(SemanticType elementType, String operation) {
        String prefix = elementType == BOOLEAN ? "b" : getInstructionPrefix(elementType);
        return prefix + "a" + operation;
    }

    private void emitArrayAddress(IndexAccess access) {
        generateExpression(access.target());
        emitConvertedExpression(access.index(), INTEGER);
        emitInstruction("iconst_1");
        emitInstruction("isub");
    }

    private void generateExpression(Expression expression) {
        switch (expression) {
            case IntegerLiteral literal -> emitInstruction("ldc " + literal.value());
            case RealLiteral literal -> emitInstruction("ldc2_w " + literal.value());
            case BooleanLiteral literal -> emitInstruction(literal.value() ? "iconst_1" : "iconst_0");
            case NullLiteral ignored -> emitInstruction("aconst_null");
            case Identifier identifier -> {
                Node declaration = analysis.variableReferences().get(identifier);
                if (globalVariables.contains(declaration)) {
                    emitInstruction("getstatic " + programClassName + "/" + identifier.name()
                            + " " + getTypeDescriptor(getExpressionType(identifier)));
                } else if (declaration instanceof VariableDeclaration field
                        && fieldOwners.containsKey(field)) {
                    String owner = getOrGenerateRecordClassName(fieldOwners.get(field));
                    emitInstruction("aload " + fieldReceivers.get(field));
                    emitInstruction("getfield " + owner + "/" + field.name()
                            + " " + getTypeDescriptor(getVariableType(field)));
                } else {
                    emitInstruction(getInstructionPrefix(getExpressionType(identifier))
                            + "load " + localSlots.get(declaration));
                }
            }
            case IndexAccess access -> {
                emitArrayAddress(access);
                emitInstruction(getArrayInstruction(getExpressionType(access), "load"));
            }
            case FieldAccess access -> {
                generateExpression(access.target());
                if (getExpressionType(access.target()) instanceof SemanticType.Array) {
                    emitInstruction("arraylength");
                } else {
                    String owner = getOrGenerateRecordClassName(
                            (SemanticType.Record) getExpressionType(access.target()));
                    emitInstruction("getfield " + owner + "/" + access.fieldName()
                            + " " + getTypeDescriptor(getExpressionType(access)));
                }
            }
            case RoutineCall call -> generateRoutineCall(call);
            case RecordConstructor constructor -> {
                SemanticType.Record record = (SemanticType.Record) getExpressionType(constructor);
                String owner = getOrGenerateRecordClassName(record);
                int receiverSlot = allocateLocalSlot(record);
                emitInstruction("new " + owner);
                emitInstruction("dup");
                emitInstruction("invokespecial " + owner + "/<init>()V");
                emitInstruction("astore " + receiverSlot);

                Map<String, Expression> initializers = new LinkedHashMap<>();
                for (MemberInitialization member : constructor.members()) {
                    initializers.put(member.name(), member.value());
                }
                RecordType syntax = (RecordType) typeSyntax.get(record);
                Map<VariableDeclaration, Integer> enclosingReceivers = new IdentityHashMap<>(fieldReceivers);
                for (VariableDeclaration field : syntax.fields()) {
                    fieldReceivers.put(field, receiverSlot);
                }
                for (VariableDeclaration field : syntax.fields()) {
                    emitInstruction("aload " + receiverSlot);
                    if (initializers.containsKey(field.name())) {
                        emitConvertedExpression(initializers.get(field.name()), getVariableType(field));
                    } else {
                        emitVariableInitializer(field);
                    }
                    emitInstruction("putfield " + owner + "/" + field.name()
                            + " " + getTypeDescriptor(getVariableType(field)));
                }
                fieldReceivers.clear();
                fieldReceivers.putAll(enclosingReceivers);
                emitInstruction("aload " + receiverSlot);
            }
            case UnaryExpression unary -> {
                SemanticType operandType = unary.operator() == UnaryOperator.NOT
                        ? BOOLEAN : getExpressionType(unary);
                emitConvertedExpression(unary.operand(), operandType);
                switch (unary.operator()) {
                    case MINUS -> emitInstruction(getInstructionPrefix(operandType) + "neg");
                    case NOT -> {
                        emitInstruction("iconst_1");
                        emitInstruction("ixor");
                    }
                    case PLUS -> { }
                }
            }
            case BinaryExpression binary -> {
                switch (binary.operator()) {
                    case AND, OR, XOR -> {
                        emitConvertedExpression(binary.left(), BOOLEAN);
                        emitConvertedExpression(binary.right(), BOOLEAN);
                        emitInstruction(switch (binary.operator()) {
                            case AND -> "iand";
                            case OR -> "ior";
                            case XOR -> "ixor";
                            default -> throw new IllegalStateException("Not a logical operator");
                        });
                    }
                    case ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER -> {
                        SemanticType resultType = getExpressionType(binary);
                        emitConvertedExpression(binary.left(), resultType);
                        emitConvertedExpression(binary.right(), resultType);
                        String operation = switch (binary.operator()) {
                            case ADD -> "add";
                            case SUBTRACT -> "sub";
                            case MULTIPLY -> "mul";
                            case DIVIDE -> "div";
                            case REMAINDER -> "rem";
                            default -> throw new IllegalStateException("Not an arithmetic operator");
                        };
                        emitInstruction(getInstructionPrefix(resultType) + operation);
                    }
                    case LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL,
                            EQUAL, NOT_EQUAL -> generateComparison(binary);
                }
            }
        }
    }

    private void generateRoutineCall(RoutineCall call) {
        RoutineDeclaration routine = routineDeclarations.get(call.name());
        for (int index = 0; index < call.arguments().size(); index++) {
            SemanticType parameterType = analysis.resolvedTypes().get(routine.parameters().get(index).type());
            emitConvertedExpression(call.arguments().get(index), parameterType);
        }
        emitInstruction("invokestatic " + programClassName + "/" + call.name()
                + getRoutineDescriptor(routine));
    }

    private void generateComparison(BinaryExpression expression) {
        BinaryOperator operator = expression.operator();
        String condition = switch (operator) {
            case EQUAL -> "eq";
            case NOT_EQUAL -> "ne";
            case LESS_THAN -> "lt";
            case LESS_THAN_OR_EQUAL -> "le";
            case GREATER_THAN -> "gt";
            case GREATER_THAN_OR_EQUAL -> "ge";
            default -> throw new IllegalStateException("Not a comparison operator");
        };
        SemanticType left = getExpressionType(expression.left());
        SemanticType right = getExpressionType(expression.right());
        boolean numeric = left == INTEGER || left == REAL || left == BOOLEAN;
        if (numeric) {
            SemanticType operandType = left == REAL || right == REAL ? REAL : INTEGER;
            emitConvertedExpression(expression.left(), operandType);
            emitConvertedExpression(expression.right(), operandType);
            if (operandType == REAL) {
                boolean compareNaNAsGreater = operator == BinaryOperator.LESS_THAN
                        || operator == BinaryOperator.LESS_THAN_OR_EQUAL;
                emitInstruction(compareNaNAsGreater ? "dcmpg" : "dcmpl");
                emitBooleanResult("if" + condition);
            } else {
                emitBooleanResult("if_icmp" + condition);
            }
        } else {
            generateExpression(expression.left());
            generateExpression(expression.right());
            emitBooleanResult("if_acmp" + condition);
        }
    }

    /** Semantic bindings retain lexical scopes, including optimizer-created blocks. */
    private void generateBlock(Block block) {
        for (BlockItem item : block.items()) {
            switch (item) {
                case TypeDeclaration ignored -> { }
                case VariableDeclaration variable -> {
                    emitVariableInitializer(variable);
                    localSlots.put(variable, allocateLocalSlot(getVariableType(variable)));
                    emitVariableStore(variable, getVariableType(variable));
                }
                case Assignment assignment -> {
                    SemanticType targetType = getExpressionType(assignment.target());
                    switch (assignment.target()) {
                        case Identifier identifier -> {
                            emitConvertedExpression(assignment.value(), targetType);
                            emitVariableStore(analysis.variableReferences().get(identifier), targetType);
                        }
                        case IndexAccess access -> {
                            emitArrayAddress(access);
                            emitConvertedExpression(assignment.value(), targetType);
                            emitInstruction(getArrayInstruction(targetType, "store"));
                        }
                        case FieldAccess access -> {
                            generateExpression(access.target());
                            emitConvertedExpression(assignment.value(), targetType);
                            String owner = getOrGenerateRecordClassName(
                                    (SemanticType.Record) getExpressionType(access.target()));
                            emitInstruction("putfield " + owner + "/" + access.fieldName()
                                    + " " + getTypeDescriptor(targetType));
                        }
                        default -> throw new IllegalStateException("Unvalidated assignment target");
                    }
                }
                case PrintStatement statement -> {
                    for (Expression expression : statement.expressions()) {
                        SemanticType type = getExpressionType(expression);
                        emitInstruction("getstatic java/lang/System/out Ljava/io/PrintStream;");
                        generateExpression(expression);
                        String descriptor = type == INTEGER || type == REAL || type == BOOLEAN
                                ? getTypeDescriptor(type) : "Ljava/lang/Object;";
                        emitInstruction("invokevirtual java/io/PrintStream/println(" + descriptor + ")V");
                    }
                }
                case RoutineCallStatement statement -> {
                    generateRoutineCall(statement.call());
                    discardReturnValue(getExpressionType(statement.call()));
                }
                case ReturnStatement statement -> {
                    statement.value().ifPresent(value -> emitConvertedExpression(value, currentReturnType));
                    emitInstruction(getInstructionPrefix(currentReturnType) + "return");
                }
                case IfStatement statement -> {
                    String elseLabel = createLabel();
                    String endLabel = createLabel();
                    emitConvertedExpression(statement.condition(), BOOLEAN);
                    emitInstruction("ifeq " + elseLabel);
                    generateBlock(statement.thenBlock());
                    emitInstruction("goto " + endLabel);
                    emitLabel(elseLabel);
                    statement.elseBlock().ifPresent(this::generateBlock);
                    emitLabel(endLabel);
                }
                case WhileLoop loop -> {
                    String startLabel = createLabel();
                    String endLabel = createLabel();
                    emitLabel(startLabel);
                    emitConvertedExpression(loop.condition(), BOOLEAN);
                    emitInstruction("ifeq " + endLabel);
                    generateBlock(loop.body());
                    emitInstruction("goto " + startLabel);
                    emitLabel(endLabel);
                }
                case ForLoop loop -> generateForLoop(loop);
                case ScopedBlockStatement statement -> generateBlock(statement.block());
                default -> throw new IllegalStateException("Unsupported block item");
            }
        }
    }

    private void generateForLoop(ForLoop loop) {
        int cursorSlot = allocateLocalSlot(INTEGER);
        int boundSlot = allocateLocalSlot(INTEGER);
        int arraySlot = -1;
        SemanticType elementType = INTEGER;
        if (loop.source() instanceof RangeSource range) {
            emitConvertedExpression(range.start(), INTEGER);
            emitInstruction("istore " + cursorSlot);
            emitConvertedExpression(range.end(), INTEGER);
            emitInstruction("istore " + boundSlot);
            if (loop.reverse()) {
                emitInstruction("iload " + cursorSlot);
                emitInstruction("iload " + boundSlot);
                emitInstruction("istore " + cursorSlot);
                emitInstruction("istore " + boundSlot);
            }
        } else {
            Expression source = ((ExpressionSource) loop.source()).expression();
            SemanticType.Array type = (SemanticType.Array) getExpressionType(source);
            elementType = type.element();
            arraySlot = allocateLocalSlot(type);
            generateExpression(source);
            emitInstruction("astore " + arraySlot);
            emitInstruction("aload " + arraySlot);
            emitInstruction("arraylength");
            emitInstruction("istore " + boundSlot);
            if (loop.reverse()) {
                emitInstruction("iload " + boundSlot);
                emitInstruction("iconst_1");
                emitInstruction("isub");
            } else {
                emitInstruction("iconst_0");
            }
            emitInstruction("istore " + cursorSlot);
        }

        int elementSlot = allocateLocalSlot(elementType);
        localSlots.put(loop, elementSlot);
        String startLabel = createLabel();
        String endLabel = createLabel();
        emitLabel(startLabel);
        emitInstruction("iload " + cursorSlot);
        if (arraySlot >= 0 && loop.reverse()) {
            emitInstruction("iflt " + endLabel);
        } else {
            emitInstruction("iload " + boundSlot);
            String exitInstruction = loop.reverse() ? "if_icmplt"
                    : arraySlot < 0 ? "if_icmpgt" : "if_icmpge";
            emitInstruction(exitInstruction + " " + endLabel);
        }
        if (arraySlot >= 0) {
            emitInstruction("aload " + arraySlot);
        }
        emitInstruction("iload " + cursorSlot);
        if (arraySlot >= 0) {
            emitInstruction(getArrayInstruction(elementType, "load"));
        }
        emitInstruction(getInstructionPrefix(elementType) + "store " + elementSlot);
        generateBlock(loop.body());
        if (arraySlot < 0) {
            // Avoid overflowing the inclusive final range value.
            emitInstruction("iload " + cursorSlot);
            emitInstruction("iload " + boundSlot);
            emitInstruction("if_icmpeq " + endLabel);
        }
        emitInstruction("iinc " + cursorSlot + (loop.reverse() ? " -1" : " 1"));
        emitInstruction("goto " + startLabel);
        emitLabel(endLabel);
    }

    private void discardReturnValue(SemanticType returnType) {
        if (returnType != VOID) {
            emitInstruction(returnType == REAL ? "pop2" : "pop");
        }
    }
}
