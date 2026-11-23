package com.compiler.semantic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import com.compiler.ast.statement.ForLoop;

import static com.compiler.semantic.DiagnosticCode.E_ARITY;
import static com.compiler.semantic.DiagnosticCode.E_BOUNDS;
import static com.compiler.semantic.DiagnosticCode.E_CONSTRUCTOR;
import static com.compiler.semantic.DiagnosticCode.E_CONVERSION;
import static com.compiler.semantic.DiagnosticCode.E_DIV_ZERO;
import static com.compiler.semantic.DiagnosticCode.E_DUPLICATE_FIELD;
import static com.compiler.semantic.DiagnosticCode.E_FIELD;
import static com.compiler.semantic.DiagnosticCode.E_INDEX;
import static com.compiler.semantic.DiagnosticCode.E_LITERAL;
import static com.compiler.semantic.DiagnosticCode.E_LVALUE;
import static com.compiler.semantic.DiagnosticCode.E_READONLY;
import static com.compiler.semantic.DiagnosticCode.E_TYPE;
import static com.compiler.semantic.DiagnosticCode.E_VOID;
import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.ERROR;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.NULL;
import static com.compiler.semantic.SemanticType.Scalar.REAL;
import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Resolves operand types, variable references and calls at their use sites. */
final class ExpressionAnalyzer {
    private final AnalysisResult.Builder result;

    ExpressionAnalyzer(AnalysisResult.Builder result) {
        this.result = result;
    }

    SemanticType analyze(Expression expression, Scope scope) {
        SemanticType type = switch (expression) {
            case IntegerLiteral ignored -> INTEGER;
            case RealLiteral literal -> {
                if (!Double.isFinite(literal.value())) {
                    result.report(E_LITERAL, literal,
                            "Real literal must be finite");
                }
                yield REAL;
            }
            case BooleanLiteral ignored -> BOOLEAN;
            case NullLiteral ignored -> NULL;
            case Identifier identifier -> analyzeIdentifier(identifier, scope);
            case RoutineCall call -> analyzeCall(call, scope, true);
            case UnaryExpression unary -> analyzeUnary(unary, scope);
            case BinaryExpression binary -> analyzeBinary(binary, scope);
            case IndexAccess index -> analyzeIndex(index, scope);
            case FieldAccess field -> analyzeField(field, scope);
            case RecordConstructor record -> analyzeConstructor(record, scope);
        };
        result.setType(expression, type);
        return type;
    }

    /** Analyzes a value and checks its conversion to the expected type. */
    void check(Expression value, Scope scope, SemanticType expected,
               String context) {
        checkConversion(expected, analyze(value, scope), value, context);
    }

    void checkConversion(SemanticType expected, SemanticType actual,
                         Expression value, String context) {
        if (expected == ERROR || actual == ERROR) {
            return;
        }
        if (!TypeRules.canAssign(expected, actual)) {
            result.report(E_TYPE, value,
                    context + ": cannot convert " + actual + " to " + expected);
            return;
        }

        if (expected == BOOLEAN && actual == INTEGER) {
            Expression literal = ConstantEvaluator.evaluateConstant(value)
                    .orElse(null);
            if (literal instanceof IntegerLiteral integer
                    && integer.value() != 0 && integer.value() != 1) {
                result.report(E_CONVERSION, value, context
                        + ": only integer 0 or 1 can convert to boolean");
            }
        } else if (expected == INTEGER && actual == REAL) {
            Optional<Expression> literal =
                    ConstantEvaluator.evaluateConstant(value);
            if (literal.isPresent()
                    && ConstantEvaluator.asInteger(literal.get()).isEmpty()) {
                result.report(E_CONVERSION, value,
                        context + ": real value is outside the integer range");
            }
        }
    }

    void checkWritable(Expression target, Scope scope) {
        switch (target) {
            case Identifier identifier -> {
                Scope.Symbol symbol = scope.find(identifier.name());
                if (symbol != null && symbol.declaration instanceof ForLoop) {
                    result.report(E_READONLY, target,
                            "Loop variable '" + identifier.name()
                                    + "' is read-only");
                }
            }
            case FieldAccess field -> {
                if (result.typeOf(field.target()) instanceof SemanticType.Array
                        && field.fieldName().equals("length")) {
                    result.report(E_READONLY, target,
                            "Array length is read-only");
                }
            }
            case IndexAccess ignored -> {
                // Individual array elements are writable.
            }
            default -> result.report(E_LVALUE, target,
                    "Assignment requires a variable, field, or array element");
        }
    }

    SemanticType analyzeCall(RoutineCall call, Scope scope,
                             boolean requiresValue) {
        Scope.Routine routine = scope.resolveRoutine(call);
        List<SemanticType> arguments = new ArrayList<>();
        for (Expression argument : call.arguments()) {
            arguments.add(analyze(argument, scope));
        }
        if (routine == null) {
            return ERROR;
        }

        int expected = routine.parameters.size();
        int actual = arguments.size();
        if (actual != expected) {
            result.report(E_ARITY, call, "Routine '" + call.name()
                    + "' expects " + expected + " arguments, got " + actual);
        }
        int matched = Math.min(actual, expected);
        for (int index = 0; index < matched; index++) {
            checkConversion(routine.parameters.get(index), arguments.get(index),
                    call.arguments().get(index), "Argument " + (index + 1));
        }
        if (requiresValue && routine.type == VOID) {
            result.report(E_VOID, call, "Procedure '" + call.name()
                    + "' cannot be used as a value");
            return ERROR;
        }

        // Statement calls also need a result type during code generation.
        result.setType(call, routine.type);
        return routine.type;
    }

    private SemanticType analyzeIdentifier(Identifier identifier, Scope scope) {
        Scope.Symbol symbol = scope.resolveVariable(identifier);
        if (symbol == null) {
            return ERROR;
        }
        result.bind(identifier, symbol.declaration);
        return symbol.type;
    }

    private SemanticType analyzeIndex(IndexAccess access, Scope scope) {
        SemanticType target = analyze(access.target(), scope);
        check(access.index(), scope, INTEGER, "Array index");
        Integer index = ConstantEvaluator.evaluateInteger(access.index())
                .orElse(null);
        if (target instanceof SemanticType.Array array) {
            Integer size = array.size();
            if (index != null
                    && (index < 1 || (size != null && index > size))) {
                result.report(E_BOUNDS, access.index(),
                        "Index " + index + " is outside 1.."
                                + (size == null ? "length" : size));
            }
            return array.element();
        }
        if (target != ERROR) {
            result.report(E_INDEX, access, "Only arrays can be indexed");
        }
        return ERROR;
    }

    private SemanticType analyzeField(FieldAccess access, Scope scope) {
        SemanticType target = analyze(access.target(), scope);
        String name = access.fieldName();
        if (target instanceof SemanticType.Array && name.equals("length")) {
            return INTEGER;
        }
        if (target instanceof SemanticType.Record record) {
            SemanticType field = record.fields().get(name);
            if (field != null) {
                return field;
            }
        }
        if (target != ERROR) {
            result.report(E_FIELD, access,
                    "No field '" + name + "' on " + target);
        }
        return ERROR;
    }

    private SemanticType analyzeConstructor(RecordConstructor constructor,
                                            Scope scope) {
        SemanticType type = scope.resolveType(constructor.typeName(),
                constructor);
        if (type != ERROR && !(type instanceof SemanticType.Record)) {
            result.report(E_CONSTRUCTOR, constructor,
                    "'new' requires a record type");
        }

        Set<String> initialized = new HashSet<>();
        for (MemberInitialization member : constructor.members()) {
            String name = member.name();
            SemanticType actual = analyze(member.value(), scope);
            if (!initialized.add(name)) {
                result.report(E_DUPLICATE_FIELD, member,
                        "Repeated initializer for '" + name + "'");
            }
            if (type instanceof SemanticType.Record record) {
                SemanticType expected = record.fields().get(name);
                if (expected == null) {
                    result.report(E_FIELD, member,
                            "Unknown record field '" + name + "'");
                } else {
                    checkConversion(expected, actual, member.value(),
                            "Field '" + name + "'");
                }
            }
        }
        return type instanceof SemanticType.Record ? type : ERROR;
    }

    private SemanticType analyzeUnary(UnaryExpression unary, Scope scope) {
        SemanticType operand = analyze(unary.operand(), scope);
        if (unary.operator() == UnaryOperator.NOT) {
            checkConversion(BOOLEAN, operand, unary.operand(),
                    "Logical operand");
            return BOOLEAN;
        }
        checkNumericOperand(operand, unary.operand());
        return TypeRules.promote(operand, INTEGER);
    }

    private SemanticType analyzeBinary(BinaryExpression binary, Scope scope) {
        SemanticType left = analyze(binary.left(), scope);
        SemanticType right = analyze(binary.right(), scope);
        return switch (binary.operator()) {
            case AND, OR, XOR -> {
                checkConversion(BOOLEAN, left, binary.left(),
                        "Logical operand");
                checkConversion(BOOLEAN, right, binary.right(),
                        "Logical operand");
                yield BOOLEAN;
            }
            case EQUAL, NOT_EQUAL -> {
                if (!TypeRules.canAssign(left, right)
                        && !TypeRules.canAssign(right, left)) {
                    result.report(E_TYPE, binary,
                            "Cannot compare " + left + " and " + right);
                }
                yield BOOLEAN;
            }
            default -> {
                checkNumericOperand(left, binary.left());
                checkNumericOperand(right, binary.right());
                checkDivisor(binary);
                yield switch (binary.operator()) {
                    case LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN,
                            GREATER_THAN_OR_EQUAL -> BOOLEAN;
                    default -> TypeRules.promote(left, right);
                };
            }
        };
    }

    private void checkNumericOperand(SemanticType type, Expression operand) {
        if (type != ERROR && !TypeRules.isNumeric(type)) {
            result.report(E_TYPE, operand,
                    "Numeric operand required, got " + type);
        }
    }

    private void checkDivisor(BinaryExpression binary) {
        if (binary.operator() != BinaryOperator.DIVIDE
                && binary.operator() != BinaryOperator.REMAINDER) {
            return;
        }
        if (ConstantEvaluator.evaluateConstant(binary.right())
                .filter(ExpressionAnalyzer::isZeroLiteral).isPresent()) {
            result.report(E_DIV_ZERO, binary.right(),
                    "Constant divisor is zero");
        }
    }

    private static boolean isZeroLiteral(Expression expression) {
        return switch (expression) {
            case IntegerLiteral literal -> literal.value() == 0;
            case RealLiteral literal -> literal.value() == 0;
            case BooleanLiteral literal -> !literal.value();
            default -> false;
        };
    }
}
