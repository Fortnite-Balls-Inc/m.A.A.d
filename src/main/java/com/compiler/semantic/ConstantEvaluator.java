package com.compiler.semantic;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.BinaryExpression;
import com.compiler.ast.expression.BinaryOperator;
import com.compiler.ast.expression.BooleanLiteral;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.IntegerLiteral;
import com.compiler.ast.expression.NullLiteral;
import com.compiler.ast.expression.RealLiteral;
import com.compiler.ast.expression.UnaryExpression;
import com.compiler.ast.expression.UnaryOperator;

import java.util.Optional;

/** Evaluates literal-only expressions without reading variables or executing calls. */
public final class ConstantEvaluator {

    private ConstantEvaluator() {
    }

    public static Optional<Expression> evaluateConstant(Expression expression) {
        return switch (expression) {
            case IntegerLiteral literal -> Optional.of(literal);
            case BooleanLiteral literal -> Optional.of(literal);
            case NullLiteral literal -> Optional.of(literal);
            case RealLiteral literal -> Double.isFinite(literal.value())
                    ? Optional.of(literal) : Optional.empty();
            case UnaryExpression unary -> evaluateConstant(unary.operand())
                    .flatMap(value -> evaluateUnaryOperator(unary.operator(), value, unary.position()));
            case BinaryExpression binary -> {
                Optional<Expression> left = evaluateConstant(binary.left());
                Optional<Expression> right = evaluateConstant(binary.right());
                if (left.isPresent() && right.isPresent()) {
                    yield evaluateBinaryOperator(binary.operator(), left.get(), right.get(), binary.position());
                }
                yield Optional.empty();
            }
            default -> Optional.empty();
        };
    }

    public static Optional<Boolean> evaluateBoolean(Expression expression) {
        return evaluateConstant(expression).flatMap(ConstantEvaluator::convertToBoolean);
    }

    /** Applies integer assignment conversion: Math.round, with ties toward positive infinity. */
    public static Optional<Integer> evaluateInteger(Expression expression) {
        return evaluateConstant(expression).flatMap(ConstantEvaluator::convertToInteger);
    }

    private static Optional<Boolean> convertToBoolean(Expression literal) {
        if (literal instanceof BooleanLiteral booleanLiteral) {
            return Optional.of(booleanLiteral.value());
        }
        if (literal instanceof IntegerLiteral integer && (integer.value() == 0 || integer.value() == 1)) {
            return Optional.of(integer.value() == 1);
        }
        return Optional.empty();
    }

    private static Optional<Integer> convertToInteger(Expression literal) {
        return switch (literal) {
            case IntegerLiteral integer -> Optional.of(integer.value());
            case BooleanLiteral booleanLiteral -> Optional.of(booleanLiteral.value() ? 1 : 0);
            case RealLiteral real -> {
                long rounded = Math.round(real.value());
                if (Double.isFinite(real.value()) && rounded >= Integer.MIN_VALUE && rounded <= Integer.MAX_VALUE) {
                    yield Optional.of((int) rounded);
                }
                yield Optional.empty();
            }
            default -> Optional.empty();
        };
    }

    private static boolean isNumericLiteral(Expression expression) {
        return expression instanceof IntegerLiteral
                || expression instanceof RealLiteral
                || expression instanceof BooleanLiteral;
    }

    private static double getNumericValue(Expression literal) {
        return switch (literal) {
            case IntegerLiteral integer -> integer.value();
            case RealLiteral real -> real.value();
            case BooleanLiteral booleanLiteral -> booleanLiteral.value() ? 1 : 0;
            default -> throw new IllegalArgumentException("Expected a numeric literal: " + literal);
        };
    }

    private static Optional<Expression> evaluateUnaryOperator(UnaryOperator operator, Expression operand,
                                                               SourcePosition position) {
        if (operator == UnaryOperator.NOT) {
            return convertToBoolean(operand).map(value -> new BooleanLiteral(!value, position));
        }
        if (!isNumericLiteral(operand)) {
            return Optional.empty();
        }
        if (operand instanceof RealLiteral real) {
            double result = operator == UnaryOperator.MINUS ? -real.value() : real.value();
            return Optional.of(new RealLiteral(result, position));
        }
        int value = (int) getNumericValue(operand);
        int result = operator == UnaryOperator.MINUS ? -value : value;
        return Optional.of(new IntegerLiteral(result, position));
    }

    private static Optional<Expression> evaluateBinaryOperator(BinaryOperator operator,
                                                                Expression left, Expression right,
                                                                SourcePosition position) {
        if (operator == BinaryOperator.AND || operator == BinaryOperator.OR || operator == BinaryOperator.XOR) {
            return evaluateLogicalOperator(operator, left, right, position);
        }
        if (left instanceof NullLiteral && right instanceof NullLiteral
                && (operator == BinaryOperator.EQUAL || operator == BinaryOperator.NOT_EQUAL)) {
            return Optional.of(new BooleanLiteral(operator == BinaryOperator.EQUAL, position));
        }
        if (!isNumericLiteral(left) || !isNumericLiteral(right)) {
            return Optional.empty();
        }

        double leftValue = getNumericValue(left);
        double rightValue = getNumericValue(right);
        Boolean comparison = switch (operator) {
            case LESS_THAN -> leftValue < rightValue;
            case LESS_THAN_OR_EQUAL -> leftValue <= rightValue;
            case GREATER_THAN -> leftValue > rightValue;
            case GREATER_THAN_OR_EQUAL -> leftValue >= rightValue;
            case EQUAL -> leftValue == rightValue;
            case NOT_EQUAL -> leftValue != rightValue;
            default -> null;
        };
        if (comparison != null) {
            return Optional.of(new BooleanLiteral(comparison, position));
        }

        // A failed evaluation must not introduce a compiler exception or erase a runtime trap.
        if ((operator == BinaryOperator.DIVIDE || operator == BinaryOperator.REMAINDER) && rightValue == 0) {
            return Optional.empty();
        }
        if (left instanceof RealLiteral || right instanceof RealLiteral) {
            double result = evaluateRealArithmetic(operator, leftValue, rightValue);
            return Double.isFinite(result) ? Optional.of(new RealLiteral(result, position)) : Optional.empty();
        }
        int result = evaluateIntegerArithmetic(operator, (int) leftValue, (int) rightValue);
        return Optional.of(new IntegerLiteral(result, position));
    }

    private static Optional<Expression> evaluateLogicalOperator(BinaryOperator operator,
                                                                 Expression left, Expression right,
                                                                 SourcePosition position) {
        Optional<Boolean> leftValue = convertToBoolean(left);
        Optional<Boolean> rightValue = convertToBoolean(right);
        if (leftValue.isEmpty() || rightValue.isEmpty()) {
            return Optional.empty();
        }
        boolean result = switch (operator) {
            case AND -> leftValue.get() & rightValue.get();
            case OR -> leftValue.get() | rightValue.get();
            case XOR -> leftValue.get() ^ rightValue.get();
            default -> throw new IllegalArgumentException("Unexpected logical operator: " + operator);
        };
        return Optional.of(new BooleanLiteral(result, position));
    }

    private static double evaluateRealArithmetic(BinaryOperator operator, double left, double right) {
        return switch (operator) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> left / right;
            case REMAINDER -> left % right;
            default -> throw new IllegalArgumentException("Unexpected arithmetic operator: " + operator);
        };
    }

    private static int evaluateIntegerArithmetic(BinaryOperator operator, int left, int right) {
        // JVM int semantics: 32-bit wraparound and division toward zero.
        return switch (operator) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> left / right;
            case REMAINDER -> left % right;
            default -> throw new IllegalArgumentException("Unexpected arithmetic operator: " + operator);
        };
    }
}
