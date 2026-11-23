package com.compiler.codegen;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.Node;
import com.compiler.ast.declaration.BlockRoutineBody;
import com.compiler.ast.declaration.ExpressionRoutineBody;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.TypeDeclaration;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.FieldAccess;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.expression.IndexAccess;
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
import com.compiler.semantic.SemanticType;

import static com.compiler.codegen.MethodAssembly.arrayInstruction;
import static com.compiler.codegen.MethodAssembly.instructionPrefix;
import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.REAL;
import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Emits the parameters, statements and control flow of one source routine. */
final class RoutineGenerator {
    private final JasminProgram program;
    private final RoutineDeclaration routine;
    private final SemanticType returnType;
    private final MethodAssembly method = new MethodAssembly();
    private final ExpressionGenerator expressions;

    RoutineGenerator(JasminProgram program, RoutineDeclaration routine) {
        this.program = program;
        this.routine = routine;
        returnType = program.returnType(routine);
        expressions = new ExpressionGenerator(program, method);
    }

    String generate() {
        for (Parameter parameter : routine.parameters()) {
            method.declareLocal(parameter, program.parameterType(parameter));
        }
        switch (routine.body().orElseThrow()) {
            case BlockRoutineBody body -> emitBlock(body.block());
            case ExpressionRoutineBody body -> {
                expressions.emitConverted(body.expression(), returnType);
                method.emit(instructionPrefix(returnType) + "return");
            }
        }
        if (returnType == VOID) {
            method.emit("return");
        } else {
            // End labels need a valid target even after an infinite loop.
            method.emit("aconst_null");
            method.emit("athrow");
        }
        return method.finish(routine.name(), program.descriptor(routine));
    }

    private void emitBlock(Block block) {
        for (BlockItem item : block.items()) {
            switch (item) {
                case TypeDeclaration ignored -> {
                    // Type declarations have no runtime instructions.
                }
                case VariableDeclaration variable -> emitLocal(variable);
                case Assignment assignment -> emitAssignment(assignment);
                case PrintStatement statement -> emitPrint(statement);
                case RoutineCallStatement statement -> {
                    expressions.emitCall(statement.call());
                    method.discardResult(
                            program.expressionType(statement.call()));
                }
                case ReturnStatement statement -> {
                    statement.value().ifPresent(value ->
                            expressions.emitConverted(value, returnType));
                    method.emit(instructionPrefix(returnType) + "return");
                }
                case IfStatement statement -> emitIf(statement);
                case WhileLoop loop -> emitWhile(loop);
                case ForLoop loop -> emitFor(loop);
                case ScopedBlockStatement nested -> emitBlock(nested.block());
                default -> throw new IllegalStateException(
                        "Unsupported block item");
            }
        }
    }

    private void emitLocal(VariableDeclaration variable) {
        expressions.emitInitializer(variable);
        SemanticType type = program.variableType(variable);
        int slot = method.declareLocal(variable, type);
        method.emit(instructionPrefix(type) + "store " + slot);
    }

    private void emitAssignment(Assignment assignment) {
        Expression value = assignment.value();
        SemanticType type = program.expressionType(assignment.target());
        switch (assignment.target()) {
            case Identifier identifier -> {
                expressions.emitConverted(value, type);
                Node declaration = program.declarationOf(identifier);
                if (program.isGlobal(declaration)) {
                    String name = ((VariableDeclaration) declaration).name();
                    method.emit("putstatic "
                            + program.globalReference(name, type));
                } else {
                    method.emit(instructionPrefix(type) + "store "
                            + method.localSlot(declaration));
                }
            }
            case IndexAccess access -> {
                expressions.emitArrayAddress(access);
                expressions.emitConverted(value, type);
                method.emit(arrayInstruction(type, "store"));
            }
            case FieldAccess access -> {
                expressions.emit(access.target());
                expressions.emitConverted(value, type);
                SemanticType record = program.expressionType(access.target());
                String owner = program.recordName((SemanticType.Record) record);
                method.emit("putfield " + program.fieldReference(owner,
                        access.fieldName(), type));
            }
            default -> throw new IllegalStateException(
                    "Unvalidated assignment target");
        }
    }

    private void emitPrint(PrintStatement statement) {
        for (Expression expression : statement.expressions()) {
            SemanticType type = program.expressionType(expression);
            method.emit("getstatic java/lang/System/out Ljava/io/PrintStream;");
            expressions.emit(expression);
            boolean primitive = type == INTEGER || type == REAL
                    || type == BOOLEAN;
            String descriptor = primitive
                    ? program.descriptor(type) : "Ljava/lang/Object;";
            method.emit("invokevirtual java/io/PrintStream/println("
                    + descriptor + ")V");
        }
    }

    private void emitIf(IfStatement statement) {
        String elseLabel = program.newLabel();
        String endLabel = program.newLabel();
        expressions.emitConverted(statement.condition(), BOOLEAN);
        method.emit("ifeq " + elseLabel);
        emitBlock(statement.thenBlock());
        method.emit("goto " + endLabel);
        method.label(elseLabel);
        statement.elseBlock().ifPresent(this::emitBlock);
        method.label(endLabel);
    }

    private void emitWhile(WhileLoop loop) {
        String startLabel = program.newLabel();
        String endLabel = program.newLabel();
        method.label(startLabel);
        expressions.emitConverted(loop.condition(), BOOLEAN);
        method.emit("ifeq " + endLabel);
        emitBlock(loop.body());
        method.emit("goto " + startLabel);
        method.label(endLabel);
    }

    private void emitFor(ForLoop loop) {
        if (loop.source() instanceof RangeSource range) {
            emitRangeLoop(loop, range);
        } else {
            ExpressionSource source = (ExpressionSource) loop.source();
            emitArrayLoop(loop, source.expression());
        }
    }

    private void emitRangeLoop(ForLoop loop, RangeSource range) {
        int cursor = method.allocateSlot(INTEGER);
        int bound = method.allocateSlot(INTEGER);
        expressions.emitConverted(range.start(), INTEGER);
        method.emit("istore " + cursor);
        expressions.emitConverted(range.end(), INTEGER);
        method.emit("istore " + bound);
        if (loop.reverse()) {
            // Evaluate the bounds in source order before swapping them.
            method.emit("iload " + cursor);
            method.emit("iload " + bound);
            method.emit("istore " + cursor);
            method.emit("istore " + bound);
        }

        int element = method.declareLocal(loop, INTEGER);
        String startLabel = program.newLabel();
        String endLabel = program.newLabel();
        method.label(startLabel);
        method.emit("iload " + cursor);
        method.emit("iload " + bound);
        method.emit((loop.reverse() ? "if_icmplt " : "if_icmpgt ") + endLabel);
        method.emit("iload " + cursor);
        method.emit("istore " + element);
        emitBlock(loop.body());

        // Stop at the inclusive bound before incrementing can overflow.
        method.emit("iload " + cursor);
        method.emit("iload " + bound);
        method.emit("if_icmpeq " + endLabel);
        emitLoopStep(loop, cursor, startLabel, endLabel);
    }

    private void emitArrayLoop(ForLoop loop, Expression source) {
        SemanticType.Array array = (SemanticType.Array)
                program.expressionType(source);
        SemanticType elementType = array.element();
        int cursor = method.allocateSlot(INTEGER);
        int length = method.allocateSlot(INTEGER);
        int arraySlot = method.allocateSlot(array);
        expressions.emit(source);
        method.emit("astore " + arraySlot);
        method.emit("aload " + arraySlot);
        method.emit("arraylength");
        method.emit("istore " + length);
        if (loop.reverse()) {
            method.emit("iload " + length);
            method.emit("iconst_1");
            method.emit("isub");
        } else {
            method.emit("iconst_0");
        }
        method.emit("istore " + cursor);

        int element = method.declareLocal(loop, elementType);
        String startLabel = program.newLabel();
        String endLabel = program.newLabel();
        method.label(startLabel);
        method.emit("iload " + cursor);
        if (loop.reverse()) {
            method.emit("iflt " + endLabel);
        } else {
            method.emit("iload " + length);
            method.emit("if_icmpge " + endLabel);
        }
        method.emit("aload " + arraySlot);
        method.emit("iload " + cursor);
        method.emit(arrayInstruction(elementType, "load"));
        method.emit(instructionPrefix(elementType) + "store " + element);
        emitBlock(loop.body());
        emitLoopStep(loop, cursor, startLabel, endLabel);
    }

    private void emitLoopStep(ForLoop loop, int cursor,
                              String startLabel, String endLabel) {
        method.emit("iinc " + cursor + (loop.reverse() ? " -1" : " 1"));
        method.emit("goto " + startLabel);
        method.label(endLabel);
    }
}
