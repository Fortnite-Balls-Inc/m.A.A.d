package com.compiler.semantic;

import java.util.Optional;

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

/** Evaluates expressions composed entirely of literal operands. */
public final class ConstantEvaluator {

    private ConstantEvaluator() {
    }

    /** Returns an empty result when no finite literal represents the value. */
    public static Optional<Expression> evaluateConstant(Expression expression) {
        return switch (expression) {
            case IntegerLiteral literal -> Optional.of(literal);
            case BooleanLiteral literal -> Optional.of(literal);
            case NullLiteral literal -> Optional.of(literal);
            case RealLiteral literal -> Double.isFinite(literal.value())
                    ? Optional.of(literal) : Optional.empty();
            case UnaryExpression unary -> evaluateConstant(unary.operand())
                    .flatMap(value -> evaluateUnary(unary, value));
            case BinaryExpression binary -> evaluateBinary(binary);
            default -> Optional.empty();
        };
    }

    public static Optional<Boolean> evaluateBoolean(Expression expression) {
        return evaluateConstant(expression)
                .flatMap(ConstantEvaluator::asBoolean);
    }

    /** Rounds real values to integers with ties toward positive infinity. */
    public static Optional<Integer> evaluateInteger(Expression expression) {
        return evaluateConstant(expression)
                .flatMap(ConstantEvaluator::asInteger);
    }

    private static Optional<Boolean> asBoolean(Expression literal) {
        if (literal instanceof BooleanLiteral bool) {
            return Optional.of(bool.value());
        }
        if (literal instanceof IntegerLiteral integer
                && (integer.value() == 0 || integer.value() == 1)) {
            return Optional.of(integer.value() == 1);
        }
        return Optional.empty();
    }

    static Optional<Integer> asInteger(Expression literal) {
        return switch (literal) {
            case IntegerLiteral integer -> Optional.of(integer.value());
            case BooleanLiteral bool -> Optional.of(bool.value() ? 1 : 0);
            case RealLiteral real -> {
                long rounded = Math.round(real.value());
                if (Double.isFinite(real.value())
                        && rounded >= Integer.MIN_VALUE
                        && rounded <= Integer.MAX_VALUE) {
                    yield Optional.of((int) rounded);
                }
                yield Optional.empty();
            }
            default -> Optional.empty();
        };
    }

    private static boolean isNumeric(Expression literal) {
        return literal instanceof IntegerLiteral
                || literal instanceof RealLiteral
                || literal instanceof BooleanLiteral;
    }

    private static double numericValue(Expression literal) {
        return switch (literal) {
            case IntegerLiteral integer -> integer.value();
            case RealLiteral real -> real.value();
            case BooleanLiteral bool -> bool.value() ? 1 : 0;
            default -> throw new IllegalArgumentException(
                    "Expected a numeric literal: " + literal);
        };
    }

    private static Optional<Expression> evaluateUnary(UnaryExpression unary,
                                                      Expression operand) {
        SourcePosition position = unary.position();
        if (unary.operator() == UnaryOperator.NOT) {
            return asBoolean(operand)
                    .map(value -> new BooleanLiteral(!value, position));
        }
        if (!isNumeric(operand)) {
            return Optional.empty();
        }

        boolean negate = unary.operator() == UnaryOperator.MINUS;
        if (operand instanceof RealLiteral real) {
            double value = negate ? -real.value() : real.value();
            return Optional.of(new RealLiteral(value, position));
        }
        int value = (int) numericValue(operand);
        return Optional.of(new IntegerLiteral(negate ? -value : value,
                position));
    }

    private static Optional<Expression> evaluateBinary(
            BinaryExpression binary) {
        Optional<Expression> left = evaluateConstant(binary.left());
        Optional<Expression> right = evaluateConstant(binary.right());
        if (left.isEmpty() || right.isEmpty()) {
            return Optional.empty();
        }
        return applyBinary(binary.operator(), left.get(), right.get(),
                binary.position());
    }

    private static Optional<Expression> applyBinary(
            BinaryOperator operator, Expression left, Expression right,
            SourcePosition position) {
        if (operator == BinaryOperator.AND || operator == BinaryOperator.OR
                || operator == BinaryOperator.XOR) {
            return evaluateLogical(operator, left, right, position);
        }
        if (left instanceof NullLiteral && right instanceof NullLiteral
                && (operator == BinaryOperator.EQUAL
                        || operator == BinaryOperator.NOT_EQUAL)) {
            boolean equal = operator == BinaryOperator.EQUAL;
            return Optional.of(new BooleanLiteral(equal, position));
        }
        if (!isNumeric(left) || !isNumeric(right)) {
            return Optional.empty();
        }

        double leftValue = numericValue(left);
        double rightValue = numericValue(right);
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

        // Leave division by zero to the diagnostic or runtime trap.
        if (rightValue == 0 && (operator == BinaryOperator.DIVIDE
                || operator == BinaryOperator.REMAINDER)) {
            return Optional.empty();
        }
        if (left instanceof RealLiteral || right instanceof RealLiteral) {
            double value = realArithmetic(operator, leftValue, rightValue);
            return Double.isFinite(value)
                    ? Optional.of(new RealLiteral(value, position))
                    : Optional.empty();
        }
        int value = integerArithmetic(operator, (int) leftValue,
                (int) rightValue);
        return Optional.of(new IntegerLiteral(value, position));
    }

    private static Optional<Expression> evaluateLogical(
            BinaryOperator operator, Expression left, Expression right,
            SourcePosition position) {
        Optional<Boolean> leftValue = asBoolean(left);
        Optional<Boolean> rightValue = asBoolean(right);
        if (leftValue.isEmpty() || rightValue.isEmpty()) {
            return Optional.empty();
        }
        boolean value = switch (operator) {
            case AND -> leftValue.get() & rightValue.get();
            case OR -> leftValue.get() | rightValue.get();
            case XOR -> leftValue.get() ^ rightValue.get();
            default -> throw new IllegalArgumentException(
                    "Unexpected logical operator: " + operator);
        };
        return Optional.of(new BooleanLiteral(value, position));
    }

    private static double realArithmetic(BinaryOperator operator, double left,
                                         double right) {
        return switch (operator) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> left / right;
            case REMAINDER -> left % right;
            default -> throw new IllegalArgumentException(
                    "Unexpected arithmetic operator: " + operator);
        };
    }

    private static int integerArithmetic(BinaryOperator operator, int left,
                                         int right) {
        // JVM int arithmetic wraps at 32 bits and divides toward zero.
        return switch (operator) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> left / right;
            case REMAINDER -> left % right;
            default -> throw new IllegalArgumentException(
                    "Unexpected arithmetic operator: " + operator);
        };
    }
}
