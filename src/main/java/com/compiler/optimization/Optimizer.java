package com.compiler.optimization;

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
import com.compiler.ast.declaration.SimpleDeclaration;
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
import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Builds an optimized immutable AST after all checks on the original program succeed. */
public final class Optimizer {

    public OptimizationResult optimize(Program program) {
        return optimize(new SemanticAnalyzer().analyze(program));
    }

    public OptimizationResult optimize(AnalysisResult analysis) {
        if (!analysis.isValid()) {
            throw new IllegalArgumentException("Cannot optimize a program with semantic errors");
        }
        return new OptimizationPass(analysis).optimizeProgram();
    }

    private static final class OptimizationPass {
        private final AnalysisResult analysis;
        private final Map<String, Integer> changes = new LinkedHashMap<>();

        private OptimizationPass(AnalysisResult analysis) {
            this.analysis = analysis;
        }

        private void recordChange(String optimizationName) {
            changes.merge(optimizationName, 1, Integer::sum);
        }

        private OptimizationResult optimizeProgram() {
            Program original = analysis.program();
            List<Declaration> declarations = original.declarations().stream()
                    .map(this::optimizeDeclaration).toList();
            Program optimized = new Program(declarations, original.position());
            return new OptimizationResult(optimized, changes);
        }

        private Declaration optimizeDeclaration(Declaration declaration) {
            return switch (declaration) {
                case VariableDeclaration variable -> optimizeVariableDeclaration(variable);
                case TypeDeclaration type -> new TypeDeclaration(
                        type.name(), optimizeType(type.type()), type.position());
                case RoutineDeclaration routine -> optimizeRoutineDeclaration(routine);
                default -> throw createUnsupportedNodeException(declaration);
            };
        }

        private RoutineDeclaration optimizeRoutineDeclaration(RoutineDeclaration routine) {
            List<Parameter> parameters = routine.parameters().stream()
                    .map(parameter -> new Parameter(parameter.name(),
                            optimizeType(parameter.type()), parameter.position())).toList();
            return new RoutineDeclaration(routine.name(), parameters,
                    routine.returnType().map(this::optimizeType),
                    routine.body().map(this::optimizeRoutineBody), routine.position());
        }

        private RoutineBody optimizeRoutineBody(RoutineBody body) {
            return switch (body) {
                case BlockRoutineBody blockBody -> {
                    Block optimizedBlock = optimizeBlock(blockBody.block());
                    yield new BlockRoutineBody(optimizedBlock, body.position());
                }
                case ExpressionRoutineBody expressionBody -> {
                    Expression optimizedExpression = optimizeExpression(expressionBody.expression());
                    yield new ExpressionRoutineBody(optimizedExpression, body.position());
                }
                default -> throw createUnsupportedNodeException(body);
            };
        }

        private VariableDeclaration optimizeVariableDeclaration(VariableDeclaration variable) {
            return new VariableDeclaration(variable.name(), variable.type().map(this::optimizeType),
                    variable.initializer().map(this::optimizeExpression), variable.position());
        }

        private TypeNode optimizeType(TypeNode type) {
            return switch (type) {
                case ArrayType array -> new ArrayType(array.size().map(this::optimizeExpression),
                        optimizeType(array.type()), array.position());
                case RecordType record -> new RecordType(
                        record.fields().stream().map(this::optimizeVariableDeclaration).toList(), record.position());
                default -> type;
            };
        }

        private Block optimizeBlock(Block block) {
            List<BlockItem> optimizedItems = new ArrayList<>();
            boolean isUnreachable = false;
            for (BlockItem original : block.items()) {
                if (isUnreachable) {
                    recordChange("unreachable statements");
                    continue;
                }

                BlockItem optimized = optimizeBlockItem(original);
                if (optimized != null) {
                    optimizedItems.add(optimized);
                    isUnreachable = ControlFlow.preventsFallThrough(optimized);
                }
            }
            return new Block(optimizedItems, block.position());
        }

        /** A null result means this item can be removed without evaluating anything. */
        private BlockItem optimizeBlockItem(BlockItem item) {
            return switch (item) {
                case VariableDeclaration variable -> {
                    VariableDeclaration optimized = optimizeVariableDeclaration(variable);
                    if (canRemoveUnusedLocal(variable, optimized)) {
                        recordChange("unused locals");
                        yield null;
                    }
                    yield optimized;
                }
                case SimpleDeclaration declaration -> (BlockItem) optimizeDeclaration(declaration);
                case Statement statement -> optimizeStatement(statement);
                default -> throw createUnsupportedNodeException(item);
            };
        }

        private boolean canRemoveUnusedLocal(VariableDeclaration original, VariableDeclaration optimized) {
            if (analysis.usedVariables().contains(original)) {
                return false;
            }
            SemanticType type = analysis.variableTypes().get(original);
            if (type != SemanticType.Scalar.INTEGER && type != SemanticType.Scalar.REAL
                    && type != SemanticType.Scalar.BOOLEAN) {
                return false;
            }

            // Preserve calls, allocation, reads that may trap, and runtime conversions.
            return optimized.initializer().isEmpty() || isScalarLiteral(optimized.initializer().get());
        }

        private boolean isScalarLiteral(Expression expression) {
            return expression instanceof IntegerLiteral
                    || expression instanceof RealLiteral
                    || expression instanceof BooleanLiteral;
        }

        private Statement optimizeStatement(Statement statement) {
            return switch (statement) {
                case Assignment assignment -> new Assignment(optimizeExpression(assignment.target()),
                        optimizeExpression(assignment.value()), assignment.position());
                case PrintStatement print -> new PrintStatement(
                        print.expressions().stream().map(this::optimizeExpression).toList(), print.position());
                case RoutineCallStatement callStatement -> new RoutineCallStatement(
                        optimizeRoutineCall(callStatement.call()), callStatement.position());
                case ReturnStatement returnStatement -> new ReturnStatement(
                        returnStatement.value().map(this::optimizeExpression), returnStatement.position());
                case ScopedBlockStatement scopedBlock -> new ScopedBlockStatement(
                        optimizeBlock(scopedBlock.block()), scopedBlock.position());
                case IfStatement conditional -> optimizeIfStatement(conditional);
                case WhileLoop loop -> optimizeWhileLoop(loop);
                case ForLoop loop -> optimizeForLoop(loop);
                default -> throw createUnsupportedNodeException(statement);
            };
        }

        private Statement optimizeIfStatement(IfStatement conditional) {
            Expression condition = optimizeExpression(conditional.condition());
            Optional<Boolean> constantCondition = ConstantEvaluator.evaluateBoolean(condition);
            if (constantCondition.isPresent()) {
                recordChange("constant branches");
                Block selectedBranch = constantCondition.get()
                        ? conditional.thenBlock() : conditional.elseBlock().orElse(null);
                if (selectedBranch == null) {
                    return null;
                }
                // Flattening this block would leak its declarations into the enclosing scope.
                return new ScopedBlockStatement(optimizeBlock(selectedBranch), conditional.position());
            }
            return new IfStatement(condition, optimizeBlock(conditional.thenBlock()),
                    conditional.elseBlock().map(this::optimizeBlock), conditional.position());
        }

        private Statement optimizeWhileLoop(WhileLoop loop) {
            Expression condition = optimizeExpression(loop.condition());
            if (ConstantEvaluator.evaluateBoolean(condition).equals(Optional.of(false))) {
                recordChange("empty loops");
                return null;
            }
            return new WhileLoop(condition, optimizeBlock(loop.body()), loop.position());
        }

        private Statement optimizeForLoop(ForLoop loop) {
            IterationSource source = switch (loop.source()) {
                case RangeSource range -> new RangeSource(optimizeExpression(range.start()),
                        optimizeExpression(range.end()), range.position());
                case ExpressionSource expressionSource -> new ExpressionSource(
                        optimizeExpression(expressionSource.expression()), expressionSource.position());
                default -> throw createUnsupportedNodeException(loop.source());
            };
            if (source instanceof RangeSource range && isEmptyConstantRange(range)) {
                recordChange("empty loops");
                return null;
            }
            return new ForLoop(loop.variableName(), source, loop.reverse(),
                    optimizeBlock(loop.body()), loop.position());
        }

        private boolean isEmptyConstantRange(RangeSource range) {
            Optional<Integer> start = ConstantEvaluator.evaluateInteger(range.start());
            Optional<Integer> end = ConstantEvaluator.evaluateInteger(range.end());
            return start.isPresent() && end.isPresent() && start.get() > end.get();
        }

        private RoutineCall optimizeRoutineCall(RoutineCall call) {
            return new RoutineCall(call.name(),
                    call.arguments().stream().map(this::optimizeExpression).toList(), call.position());
        }

        private Expression optimizeExpression(Expression expression) {
            Expression optimized = switch (expression) {
                case BinaryExpression binary -> new BinaryExpression(binary.operator(),
                        optimizeExpression(binary.left()), optimizeExpression(binary.right()), binary.position());
                case UnaryExpression unary -> new UnaryExpression(unary.operator(),
                        optimizeExpression(unary.operand()), unary.position());
                case RoutineCall call -> optimizeRoutineCall(call);
                case RecordConstructor constructor -> new RecordConstructor(constructor.typeName(),
                        constructor.members().stream().map(member -> new MemberInitialization(
                                member.name(), optimizeExpression(member.value()), member.position())).toList(),
                        constructor.position());
                case FieldAccess field -> new FieldAccess(
                        optimizeExpression(field.target()), field.fieldName(), field.position());
                case IndexAccess index -> new IndexAccess(
                        optimizeExpression(index.target()), optimizeExpression(index.index()), index.position());
                default -> expression;
            };

            if (optimized instanceof BinaryExpression || optimized instanceof UnaryExpression) {
                Optional<Expression> constant = ConstantEvaluator.evaluateConstant(optimized);
                if (constant.isPresent()) {
                    recordChange("constant expressions");
                    return constant.get();
                }
            }
            return optimized;
        }

        private IllegalArgumentException createUnsupportedNodeException(Node node) {
            return new IllegalArgumentException("Unsupported AST node: " + node.getClass());
        }
    }
}
