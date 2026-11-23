package com.compiler.optimization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.Node;
import com.compiler.ast.Program;
import com.compiler.ast.declaration.BlockRoutineBody;
import com.compiler.ast.declaration.Declaration;
import com.compiler.ast.declaration.ExpressionRoutineBody;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineBody;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.TypeDeclaration;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.BinaryExpression;
import com.compiler.ast.expression.BooleanLiteral;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.FieldAccess;
import com.compiler.ast.expression.IndexAccess;
import com.compiler.ast.expression.IntegerLiteral;
import com.compiler.ast.expression.MemberInitialization;
import com.compiler.ast.expression.RealLiteral;
import com.compiler.ast.expression.RecordConstructor;
import com.compiler.ast.expression.RoutineCall;
import com.compiler.ast.expression.UnaryExpression;
import com.compiler.ast.statement.Assignment;
import com.compiler.ast.statement.ExpressionSource;
import com.compiler.ast.statement.ForLoop;
import com.compiler.ast.statement.IfStatement;
import com.compiler.ast.statement.IterationSource;
import com.compiler.ast.statement.PrintStatement;
import com.compiler.ast.statement.RangeSource;
import com.compiler.ast.statement.ReturnStatement;
import com.compiler.ast.statement.RoutineCallStatement;
import com.compiler.ast.statement.ScopedBlockStatement;
import com.compiler.ast.statement.Statement;
import com.compiler.ast.statement.WhileLoop;
import com.compiler.ast.types.ArrayType;
import com.compiler.ast.types.RecordType;
import com.compiler.ast.types.TypeNode;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.ConstantEvaluator;
import com.compiler.semantic.ControlFlow;
import com.compiler.semantic.SemanticType;

/**
 * Rewrites one program. Semantic metadata belongs to the original nodes,
 * while constant folding and reachability use the rewritten children.
 */
final class OptimizationPass {
    private final AnalysisResult analysis;
    private final Map<String, Integer> changes = new LinkedHashMap<>();

    OptimizationPass(AnalysisResult analysis) {
        this.analysis = analysis;
    }

    OptimizationResult optimizeProgram() {
        Program original = analysis.program();
        List<Declaration> declarations = original.declarations().stream()
                .map(this::optimizeDeclaration)
                .toList();
        Program optimized = new Program(declarations, original.position());
        return new OptimizationResult(optimized, changes);
    }

    private Declaration optimizeDeclaration(Declaration declaration) {
        return switch (declaration) {
            case VariableDeclaration variable -> optimizeVariable(variable);
            case TypeDeclaration type -> optimizeTypeDeclaration(type);
            case RoutineDeclaration routine -> optimizeRoutine(routine);
        };
    }

    private TypeDeclaration optimizeTypeDeclaration(TypeDeclaration type) {
        return new TypeDeclaration(type.name(), optimizeType(type.type()),
                type.position());
    }

    private VariableDeclaration optimizeVariable(VariableDeclaration variable) {
        return new VariableDeclaration(variable.name(),
                variable.type().map(this::optimizeType),
                variable.initializer().map(this::optimizeExpression),
                variable.position());
    }

    private RoutineDeclaration optimizeRoutine(RoutineDeclaration routine) {
        List<Parameter> parameters = routine.parameters().stream()
                .map(this::optimizeParameter)
                .toList();
        return new RoutineDeclaration(routine.name(), parameters,
                routine.returnType().map(this::optimizeType),
                routine.body().map(this::optimizeRoutineBody),
                routine.position());
    }

    private Parameter optimizeParameter(Parameter parameter) {
        return new Parameter(parameter.name(), optimizeType(parameter.type()),
                parameter.position());
    }

    private RoutineBody optimizeRoutineBody(RoutineBody body) {
        return switch (body) {
            case BlockRoutineBody block -> new BlockRoutineBody(
                    optimizeBlock(block.block()), block.position());
            case ExpressionRoutineBody expression -> new ExpressionRoutineBody(
                    optimizeExpression(expression.expression()),
                    expression.position());
        };
    }

    private TypeNode optimizeType(TypeNode type) {
        return switch (type) {
            case ArrayType array -> new ArrayType(
                    array.size().map(this::optimizeExpression),
                    optimizeType(array.type()), array.position());
            case RecordType record -> {
                List<VariableDeclaration> fields = record.fields().stream()
                        .map(this::optimizeVariable)
                        .toList();
                yield new RecordType(fields, record.position());
            }
            default -> type;
        };
    }

    private Block optimizeBlock(Block block) {
        List<BlockItem> items = new ArrayList<>();
        boolean unreachable = false;
        for (BlockItem original : block.items()) {
            if (unreachable) {
                recordChange("unreachable statements");
                continue;
            }

            BlockItem item = optimizeBlockItem(original);
            if (item != null) {
                items.add(item);
                unreachable = ControlFlow.preventsFallThrough(item);
            }
        }
        return new Block(items, block.position());
    }

    /** A null result removes the item from the enclosing block. */
    private BlockItem optimizeBlockItem(BlockItem item) {
        return switch (item) {
            case VariableDeclaration variable -> optimizeLocal(variable);
            case TypeDeclaration type -> optimizeTypeDeclaration(type);
            case Statement statement -> optimizeStatement(statement);
            default -> throw unsupportedNode(item);
        };
    }

    private VariableDeclaration optimizeLocal(VariableDeclaration original) {
        VariableDeclaration optimized = optimizeVariable(original);
        if (canRemoveUnusedLocal(original, optimized)) {
            recordChange("unused locals");
            return null;
        }
        return optimized;
    }

    private boolean canRemoveUnusedLocal(VariableDeclaration original,
            VariableDeclaration optimized) {
        if (analysis.usedVariables().contains(original)) {
            return false;
        }
        SemanticType type = analysis.variableTypes().get(original);
        if (type != SemanticType.Scalar.INTEGER
                && type != SemanticType.Scalar.REAL
                && type != SemanticType.Scalar.BOOLEAN) {
            return false;
        }

        // Keep initializers that may call a routine, allocate or throw.
        return optimized.initializer()
                .map(OptimizationPass::isScalarLiteral)
                .orElse(true);
    }

    private static boolean isScalarLiteral(Expression expression) {
        return expression instanceof IntegerLiteral
                || expression instanceof RealLiteral
                || expression instanceof BooleanLiteral;
    }

    private Statement optimizeStatement(Statement statement) {
        return switch (statement) {
            case Assignment assignment -> new Assignment(
                    optimizeExpression(assignment.target()),
                    optimizeExpression(assignment.value()),
                    assignment.position());
            case PrintStatement print -> new PrintStatement(
                    optimizeExpressions(print.expressions()), print.position());
            case RoutineCallStatement call -> new RoutineCallStatement(
                    optimizeRoutineCall(call.call()), call.position());
            case ReturnStatement result -> new ReturnStatement(
                    result.value().map(this::optimizeExpression),
                    result.position());
            case ScopedBlockStatement scoped -> new ScopedBlockStatement(
                    optimizeBlock(scoped.block()), scoped.position());
            case IfStatement conditional -> optimizeIf(conditional);
            case WhileLoop loop -> optimizeWhile(loop);
            case ForLoop loop -> optimizeFor(loop);
        };
    }

    private Statement optimizeIf(IfStatement conditional) {
        Expression condition = optimizeExpression(conditional.condition());
        Optional<Boolean> constant = ConstantEvaluator.evaluateBoolean(
                condition);
        if (constant.isEmpty()) {
            return new IfStatement(condition,
                    optimizeBlock(conditional.thenBlock()),
                    conditional.elseBlock().map(this::optimizeBlock),
                    conditional.position());
        }

        recordChange("constant branches");
        Block selected = constant.get()
                ? conditional.thenBlock()
                : conditional.elseBlock().orElse(null);
        if (selected == null) {
            return null;
        }

        // Keep declarations in the lexical scope of the selected branch.
        return new ScopedBlockStatement(optimizeBlock(selected),
                conditional.position());
    }

    private Statement optimizeWhile(WhileLoop loop) {
        Expression condition = optimizeExpression(loop.condition());
        Optional<Boolean> constant = ConstantEvaluator.evaluateBoolean(
                condition);
        if (constant.isPresent() && !constant.get()) {
            recordChange("empty loops");
            return null;
        }
        return new WhileLoop(condition, optimizeBlock(loop.body()),
                loop.position());
    }

    private Statement optimizeFor(ForLoop loop) {
        IterationSource source = optimizeIterationSource(loop.source());
        if (source instanceof RangeSource range && isEmptyRange(range)) {
            recordChange("empty loops");
            return null;
        }
        return new ForLoop(loop.variableName(), source, loop.reverse(),
                optimizeBlock(loop.body()), loop.position());
    }

    private IterationSource optimizeIterationSource(IterationSource source) {
        return switch (source) {
            case RangeSource range -> new RangeSource(
                    optimizeExpression(range.start()),
                    optimizeExpression(range.end()), range.position());
            case ExpressionSource iterable -> new ExpressionSource(
                    optimizeExpression(iterable.expression()),
                    iterable.position());
            default -> throw unsupportedNode(source);
        };
    }

    private static boolean isEmptyRange(RangeSource range) {
        Optional<Integer> start = ConstantEvaluator.evaluateInteger(
                range.start());
        Optional<Integer> end = ConstantEvaluator.evaluateInteger(range.end());
        return start.isPresent() && end.isPresent() && start.get() > end.get();
    }

    private Expression optimizeExpression(Expression expression) {
        return switch (expression) {
            case BinaryExpression binary -> foldConstant(new BinaryExpression(
                    binary.operator(), optimizeExpression(binary.left()),
                    optimizeExpression(binary.right()), binary.position()));
            case UnaryExpression unary -> foldConstant(new UnaryExpression(
                    unary.operator(), optimizeExpression(unary.operand()),
                    unary.position()));
            case RoutineCall call -> optimizeRoutineCall(call);
            case RecordConstructor record -> {
                List<MemberInitialization> members = record.members().stream()
                        .map(this::optimizeMember)
                        .toList();
                yield new RecordConstructor(record.typeName(), members,
                        record.position());
            }
            case FieldAccess field -> new FieldAccess(
                    optimizeExpression(field.target()), field.fieldName(),
                    field.position());
            case IndexAccess index -> new IndexAccess(
                    optimizeExpression(index.target()),
                    optimizeExpression(index.index()), index.position());
            default -> expression;
        };
    }

    private Expression foldConstant(Expression expression) {
        Optional<Expression> literal = ConstantEvaluator.evaluateConstant(
                expression);
        if (literal.isPresent()) {
            recordChange("constant expressions");
            return literal.get();
        }
        return expression;
    }

    private RoutineCall optimizeRoutineCall(RoutineCall call) {
        return new RoutineCall(call.name(),
                optimizeExpressions(call.arguments()), call.position());
    }

    private List<Expression> optimizeExpressions(List<Expression> expressions) {
        return expressions.stream().map(this::optimizeExpression).toList();
    }

    private MemberInitialization optimizeMember(MemberInitialization member) {
        return new MemberInitialization(member.name(),
                optimizeExpression(member.value()), member.position());
    }

    private void recordChange(String name) {
        changes.merge(name, 1, Integer::sum);
    }

    private static IllegalArgumentException unsupportedNode(Node node) {
        return new IllegalArgumentException(
                "Unsupported AST node: " + node.getClass());
    }
}
