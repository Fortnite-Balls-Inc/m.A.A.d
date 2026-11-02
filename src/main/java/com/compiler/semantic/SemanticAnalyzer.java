package com.compiler.semantic;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.Node;
import com.compiler.ast.Program;
import com.compiler.ast.declaration.BlockRoutineBody;
import com.compiler.ast.declaration.Declaration;
import com.compiler.ast.declaration.ExpressionRoutineBody;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.SimpleDeclaration;
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
import com.compiler.ast.statement.Statement;
import com.compiler.ast.statement.WhileLoop;
import com.compiler.ast.types.ArrayType;
import com.compiler.ast.types.NamedType;
import com.compiler.ast.types.PrimitiveType;
import com.compiler.ast.types.RecordType;
import com.compiler.ast.types.TypeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.compiler.semantic.DiagnosticCode.*;
import static com.compiler.semantic.SemanticType.Scalar.*;

/** Checks the original AST without modifying it */
public final class SemanticAnalyzer {

    public AnalysisResult analyze(Program program) {
        return new AnalysisPass().analyzeProgram(program);
    }

    private enum SymbolKind {
        VARIABLE,
        TYPE,
        ROUTINE
    }

    private static class Symbol {
        private final SymbolKind kind;
        private final SemanticType type; // actual type, for type definitions also
        private final Node declaration; // link to the declaration in AST

        private Symbol(SymbolKind kind, SemanticType type, Node declaration) {
            this.kind = kind;
            this.type = type;
            this.declaration = declaration;
        }
    }

    private static final class RoutineSymbol extends Symbol {
        private final List<SemanticType> parameterTypes; // routine parameter list
        private boolean hasDefinition; // does the routine have definition

        private RoutineSymbol(SemanticType returnType, RoutineDeclaration declaration,
                              List<SemanticType> parameterTypes, boolean hasDefinition) {
            super(SymbolKind.ROUTINE, returnType, declaration);
            this.parameterTypes = List.copyOf(parameterTypes);
            this.hasDefinition = hasDefinition;
        }
    }

    private static final class Scope {
        private final Scope parent;
        private final Map<String, Symbol> symbols = new LinkedHashMap<>();

        private Scope(Scope parent) {
            this.parent = parent;
        }

        private Symbol findSymbol(String name) {
            Symbol symbol = symbols.get(name);
            if (symbol != null || parent == null) {
                return symbol;
            }
            return parent.findSymbol(name);
        }
    }

    private static final class AnalysisPass {
        private final List<Diagnostic> diagnostics = new ArrayList<>();
        private final Map<Expression, SemanticType> expressionTypes = new IdentityHashMap<>();
        private final Map<VariableDeclaration, SemanticType> variableTypes = new IdentityHashMap<>();
        private final Set<VariableDeclaration> usedVariables =
                Collections.newSetFromMap(new IdentityHashMap<>());

        private AnalysisResult analyzeProgram(Program program) {
            // Create the global scope and analyze top-level declarations in source order.
            Scope globalScope = new Scope(null);
            for (Declaration declaration : program.declarations()) {
                switch (declaration) {
                    case RoutineDeclaration routine -> analyzeRoutineDeclaration(routine, globalScope);
                    case SimpleDeclaration simple -> analyzeSimpleDeclaration(simple, globalScope);
                    default -> throw createUnsupportedNodeException(declaration);
                }
            }

            // Check that all routines have definitions
            for (var entry : globalScope.symbols.entrySet()) {
                Symbol symbol = entry.getValue();
                if (symbol instanceof RoutineSymbol routine && !routine.hasDefinition) {
                    reportError(E_FORWARD, symbol.declaration,
                            "Routine '" + entry.getKey() + "' has no definition");
                }
            }
            return new AnalysisResult(program, diagnostics, expressionTypes, usedVariables, variableTypes);
        }

        private void reportError(DiagnosticCode code, Node node, String message) {
            diagnostics.add(new Diagnostic(code, message, node.position()));
        }

        private IllegalArgumentException createUnsupportedNodeException(Node node) {
            return new IllegalArgumentException("Unsupported AST node: " + node.getClass());
        }

        private void declareSymbol(String name, Symbol symbol, Scope scope) {
            if (scope.symbols.putIfAbsent(name, symbol) != null) {
                reportError(E_DUPLICATE, symbol.declaration,
                        "Duplicate declaration of '" + name + "' in this scope");
            }
        }

        /** Reports missing names and wrong entity kinds at the use site. */
        private Symbol resolveSymbol(String name, SymbolKind expectedKind, Node useSite, Scope scope) {
            Symbol symbol = scope.findSymbol(name);
            if (symbol == null) {
                reportError(E_UNDECLARED, useSite, "'" + name + "' must be declared before use");
            } else if (symbol.kind != expectedKind) {
                reportError(E_KIND, useSite,
                        "'" + name + "' is not a " + expectedKind.name().toLowerCase(Locale.ROOT));
                return null;
            }
            return symbol;
        }

        private void analyzeSimpleDeclaration(SimpleDeclaration declaration, Scope scope) {
            switch (declaration) {
                case VariableDeclaration variable -> analyzeVariableDeclaration(variable, scope);
                case TypeDeclaration type -> analyzeTypeDeclaration(type, scope);
                default -> throw createUnsupportedNodeException(declaration);
            }
        }

        private void analyzeTypeDeclaration(TypeDeclaration declaration, Scope scope) {
            SemanticType resolvedType = resolveType(declaration.type(), scope);

            // For arrays and records a new type is constructed,
            // named aliases of existing types preserve the types identity.
            if (declaration.type() instanceof ArrayType
                    && resolvedType instanceof SemanticType.Array array) {
                resolvedType = new SemanticType.Array(array.element(), array.size(), declaration);
            }
            if (declaration.type() instanceof RecordType
                    && resolvedType instanceof SemanticType.Record record) {
                resolvedType = new SemanticType.Record(declaration.name(), record.fields());
            }
            declareSymbol(declaration.name(), new Symbol(SymbolKind.TYPE, resolvedType, declaration), scope);
        }

        private SemanticType analyzeVariableDeclaration(VariableDeclaration declaration, Scope scope) {
            SemanticType declaredType = declaration.type().map(type -> resolveType(type, scope)).orElse(null);
            SemanticType initializerType = declaration.initializer()
                    .map(initializer -> analyzeExpression(initializer, scope)).orElse(null);
            SemanticType variableType = declaredType != null ? declaredType : initializerType;

            if (variableType == null || (declaredType == null && (variableType == NULL || variableType == VOID))) {
                reportError(E_INFER, declaration, "Cannot infer a variable type; provide an explicit value type");
                variableType = ERROR;
            }
            if (declaredType != null && initializerType != null) {
                checkAssignmentCompatibility(declaredType, initializerType,
                        declaration.initializer().orElseThrow(), "Initializer");
            }

            // An initializer sees the surrounding scope before its own variable is declared.
            variableTypes.put(declaration, variableType);
            declareSymbol(declaration.name(), new Symbol(SymbolKind.VARIABLE, variableType, declaration), scope);
            return variableType;
        }

        private SemanticType resolveType(TypeNode type, Scope scope) {
            return switch (type) {
                case PrimitiveType primitive -> switch (primitive.kind()) {
                    case INTEGER -> INTEGER;
                    case REAL -> REAL;
                    case BOOLEAN -> BOOLEAN;
                };
                case NamedType named -> {
                    Symbol symbol = resolveSymbol(named.name(), SymbolKind.TYPE, named, scope);
                    yield symbol == null ? ERROR : symbol.type;
                }
                case ArrayType array -> resolveArrayType(array, scope);
                case RecordType record -> resolveRecordType(record, scope);
                default -> throw createUnsupportedNodeException(type);
            };
        }

        private SemanticType resolveArrayType(ArrayType array, Scope scope) {
            Integer size = null;
            if (array.size().isPresent()) {
                Expression sizeExpression = array.size().get();
                checkAssignmentCompatibility(INTEGER, analyzeExpression(sizeExpression, scope),
                        sizeExpression, "Array size");
                size = ConstantEvaluator.evaluateInteger(sizeExpression).orElse(null);
                if (size != null && size < 0) {
                    reportError(E_ARRAY_SIZE, sizeExpression, "Array size must be non-negative");
                }
            }
            return new SemanticType.Array(resolveType(array.type(), scope), size, null);
        }

        private SemanticType resolveRecordType(RecordType record, Scope scope) {
            Scope fieldScope = new Scope(scope);
            Map<String, SemanticType> fieldTypes = new LinkedHashMap<>();
            for (VariableDeclaration field : record.fields()) {
                fieldTypes.put(field.name(), analyzeVariableDeclaration(field, fieldScope));
            }
            return new SemanticType.Record(fieldTypes);
        }

        private void analyzeRoutineDeclaration(RoutineDeclaration routine, Scope globalScope) {
            Scope routineScope = new Scope(globalScope);
            List<SemanticType> parameterTypes = new ArrayList<>();
            for (Parameter parameter : routine.parameters()) {
                SemanticType parameterType = resolveType(parameter.type(), routineScope);
                parameterTypes.add(parameterType);
                declareSymbol(parameter.name(),
                        new Symbol(SymbolKind.VARIABLE, parameterType, parameter), routineScope);
            }
            SemanticType returnType = routine.returnType()
                    .map(type -> resolveType(type, routineScope)).orElse(VOID);
            registerRoutine(routine, parameterTypes, returnType, globalScope);

            if (routine.body().isEmpty()) {
                return;
            }
            switch (routine.body().get()) {
                case ExpressionRoutineBody expressionBody -> {
                    SemanticType actualType = analyzeExpression(expressionBody.expression(), routineScope);
                    if (returnType == VOID) {
                        reportError(E_RETURN, expressionBody, "An expression body requires a declared return type");
                    } else {
                        checkAssignmentCompatibility(returnType, actualType,
                                expressionBody.expression(), "Return value");
                    }
                }
                case BlockRoutineBody blockBody -> {
                    analyzeBlock(blockBody.block(), routineScope, returnType);
                    if (returnType != VOID && returnType != ERROR
                            && !ControlFlow.preventsFallThrough(blockBody.block())) {
                        reportError(E_MISSING_RETURN, routine,
                                "Routine '" + routine.name() + "' can reach its end without returning a value");
                    }
                }
                default -> throw createUnsupportedNodeException(routine.body().get());
            }
        }

        private void registerRoutine(RoutineDeclaration routine, List<SemanticType> parameterTypes,
                                     SemanticType returnType, Scope globalScope) {
            Symbol previous = globalScope.symbols.get(routine.name());
            if (previous == null) {
                RoutineSymbol symbol = new RoutineSymbol(returnType, routine, parameterTypes,
                        routine.body().isPresent());
                // Register before checking the body so recursive calls resolve normally.
                globalScope.symbols.put(routine.name(), symbol);
            } else if (previous instanceof RoutineSymbol previousRoutine && !previousRoutine.hasDefinition
                    && routine.body().isPresent()) {
                if (!haveSameType(previous.type, returnType)
                        || !haveSameParameterTypes(previousRoutine.parameterTypes, parameterTypes)) {
                    reportError(E_SIGNATURE, routine,
                            "Definition of '" + routine.name() + "' does not match its forward declaration");
                }
                previousRoutine.hasDefinition = true;
            } else {
                reportError(E_DUPLICATE, routine, "Duplicate declaration of '" + routine.name() + "'");
            }
        }

        private boolean haveSameParameterTypes(List<SemanticType> expected, List<SemanticType> actual) {
            if (expected.size() != actual.size()) {
                return false;
            }
            for (int index = 0; index < expected.size(); index++) {
                if (!haveSameType(expected.get(index), actual.get(index))) {
                    return false;
                }
            }
            return true;
        }

        private void analyzeBlock(Block block, Scope scope, SemanticType returnType) {
            // Unreachable code is still checked; only the later optimizer may remove it.
            for (BlockItem item : block.items()) {
                switch (item) {
                    case SimpleDeclaration declaration -> analyzeSimpleDeclaration(declaration, scope);
                    case Statement statement -> analyzeStatement(statement, scope, returnType);
                    default -> throw createUnsupportedNodeException(item);
                }
            }
        }

        private void analyzeStatement(Statement statement, Scope scope, SemanticType returnType) {
            switch (statement) {
                case Assignment assignment -> {
                    SemanticType targetType = analyzeExpression(assignment.target(), scope);
                    checkWritableTarget(assignment.target(), scope);
                    checkAssignmentCompatibility(targetType, analyzeExpression(assignment.value(), scope),
                            assignment.value(), "Assignment");
                }
                case PrintStatement print -> {
                    for (Expression expression : print.expressions()) {
                        analyzeExpression(expression, scope);
                    }
                }
                case RoutineCallStatement callStatement -> analyzeRoutineCall(callStatement.call(), scope, false);
                case ReturnStatement returnStatement -> analyzeReturnStatement(returnStatement, scope, returnType);
                case IfStatement conditional -> {
                    checkAssignmentCompatibility(BOOLEAN, analyzeExpression(conditional.condition(), scope),
                            conditional.condition(), "If condition");
                    analyzeBlock(conditional.thenBlock(), new Scope(scope), returnType);
                    conditional.elseBlock().ifPresent(block -> analyzeBlock(block, new Scope(scope), returnType));
                }
                case WhileLoop loop -> {
                    checkAssignmentCompatibility(BOOLEAN, analyzeExpression(loop.condition(), scope),
                            loop.condition(), "While condition");
                    analyzeBlock(loop.body(), new Scope(scope), returnType);
                }
                case ForLoop loop -> analyzeForLoop(loop, scope, returnType);
                case ScopedBlockStatement scopedBlock ->
                        analyzeBlock(scopedBlock.block(), new Scope(scope), returnType);
                default -> throw createUnsupportedNodeException(statement);
            }
        }

        private void analyzeReturnStatement(ReturnStatement statement, Scope scope, SemanticType returnType) {
            if (statement.value().isPresent()) {
                Expression value = statement.value().get();
                SemanticType actualType = analyzeExpression(value, scope);
                if (returnType == VOID) {
                    reportError(E_RETURN, statement, "A procedure cannot return a value");
                } else {
                    checkAssignmentCompatibility(returnType, actualType, value, "Return value");
                }
            } else if (returnType != VOID && returnType != ERROR) {
                reportError(E_RETURN, statement, "A function must return a value of type " + returnType);
            }
        }

        private void analyzeForLoop(ForLoop loop, Scope scope, SemanticType returnType) {
            SemanticType elementType = switch (loop.source()) {
                case RangeSource range -> {
                    checkAssignmentCompatibility(INTEGER, analyzeExpression(range.start(), scope),
                            range.start(), "Range start");
                    checkAssignmentCompatibility(INTEGER, analyzeExpression(range.end(), scope),
                            range.end(), "Range end");
                    yield INTEGER;
                }
                case ExpressionSource source -> {
                    SemanticType sourceType = analyzeExpression(source.expression(), scope);
                    if (sourceType instanceof SemanticType.Array array) {
                        yield array.element();
                    }
                    if (sourceType != ERROR) {
                        reportError(E_ITERATION, source, "A single-expression for loop requires an array");
                    }
                    yield ERROR;
                }
                default -> throw createUnsupportedNodeException(loop.source());
            };

            Scope loopScope = new Scope(scope);
            declareSymbol(loop.variableName(), new Symbol(SymbolKind.VARIABLE, elementType, loop), loopScope);
            analyzeBlock(loop.body(), loopScope, returnType);
        }

        private void checkWritableTarget(Expression target, Scope scope) {
            switch (target) {
                case Identifier identifier -> {
                    Symbol symbol = scope.findSymbol(identifier.name());
                    // Loop declarations are the only source of immutable variable bindings.
                    if (symbol != null && symbol.declaration instanceof ForLoop) {
                        reportError(E_READONLY, target,
                                "Loop variable '" + identifier.name() + "' is read-only");
                    }
                }
                case FieldAccess field -> {
                    if (expressionTypes.get(field.target()) instanceof SemanticType.Array
                            && field.fieldName().equals("length")) {
                        reportError(E_READONLY, target, "Array length is read-only");
                    }
                }
                case IndexAccess _ -> {
                    // Array elements are writable; analyzeExpression already checked the index.
                }
                default -> reportError(E_LVALUE, target, "Assignment requires a variable, field, or array element");
            }
        }

        private SemanticType analyzeExpression(Expression expression, Scope scope) {
            SemanticType type = inferExpressionType(expression, scope);
            expressionTypes.put(expression, type);
            return type;
        }

        private SemanticType inferExpressionType(Expression expression, Scope scope) {
            return switch (expression) {
                case IntegerLiteral _ -> INTEGER;
                case RealLiteral literal -> {
                    if (!Double.isFinite(literal.value())) {
                        reportError(E_LITERAL, literal, "Real literal must be finite");
                    }
                    yield REAL;
                }
                case BooleanLiteral _ -> BOOLEAN;
                case NullLiteral _ -> NULL;
                case Identifier identifier -> analyzeIdentifier(identifier, scope);
                case RoutineCall call -> analyzeRoutineCall(call, scope, true);
                case UnaryExpression unary -> analyzeUnaryExpression(unary, scope);
                case BinaryExpression binary -> analyzeBinaryExpression(binary, scope);
                case IndexAccess index -> analyzeIndexAccess(index, scope);
                case FieldAccess field -> analyzeFieldAccess(field, scope);
                case RecordConstructor constructor -> analyzeRecordConstructor(constructor, scope);
                default -> throw createUnsupportedNodeException(expression);
            };
        }

        private SemanticType analyzeIdentifier(Identifier identifier, Scope scope) {
            Symbol symbol = resolveSymbol(identifier.name(), SymbolKind.VARIABLE, identifier, scope);
            if (symbol == null) {
                return ERROR;
            }
            if (symbol.declaration instanceof VariableDeclaration variable) {
                usedVariables.add(variable);
            }
            return symbol.type;
        }

        private SemanticType analyzeUnaryExpression(UnaryExpression unary, Scope scope) {
            SemanticType operandType = analyzeExpression(unary.operand(), scope);
            if (unary.operator() == UnaryOperator.NOT) {
                checkAssignmentCompatibility(BOOLEAN, operandType, unary.operand(), "Logical operand");
                return BOOLEAN;
            }
            checkNumericOperand(operandType, unary.operand());
            return promoteNumericTypes(operandType, INTEGER);
        }

        private SemanticType analyzeIndexAccess(IndexAccess access, Scope scope) {
            SemanticType targetType = analyzeExpression(access.target(), scope);
            checkAssignmentCompatibility(INTEGER, analyzeExpression(access.index(), scope),
                    access.index(), "Array index");
            Integer index = ConstantEvaluator.evaluateInteger(access.index()).orElse(null);

            if (targetType instanceof SemanticType.Array array) {
                if (index != null && (index < 1 || (array.size() != null && index > array.size()))) {
                    reportError(E_BOUNDS, access.index(), "Index " + index + " is outside 1.."
                            + (array.size() == null ? "length" : array.size()));
                }
                return array.element();
            }
            if (targetType != ERROR) {
                reportError(E_INDEX, access, "Only arrays can be indexed");
            }
            return ERROR;
        }

        private SemanticType analyzeFieldAccess(FieldAccess access, Scope scope) {
            SemanticType targetType = analyzeExpression(access.target(), scope);
            if (targetType instanceof SemanticType.Array && access.fieldName().equals("length")) {
                return INTEGER;
            }
            if (targetType instanceof SemanticType.Record record) {
                SemanticType fieldType = record.fields().get(access.fieldName());
                if (fieldType != null) {
                    return fieldType;
                }
            }
            if (targetType != ERROR) {
                reportError(E_FIELD, access, "No field '" + access.fieldName() + "' on " + targetType);
            }
            return ERROR;
        }

        private SemanticType analyzeRecordConstructor(RecordConstructor constructor, Scope scope) {
            Symbol symbol = resolveSymbol(constructor.typeName(), SymbolKind.TYPE, constructor, scope);
            SemanticType recordType = symbol == null ? ERROR : symbol.type;
            if (recordType != ERROR && !(recordType instanceof SemanticType.Record)) {
                reportError(E_CONSTRUCTOR, constructor, "'new' requires a record type");
            }

            Set<String> initializedFields = new HashSet<>();
            for (MemberInitialization member : constructor.members()) {
                SemanticType actualType = analyzeExpression(member.value(), scope);
                if (!initializedFields.add(member.name())) {
                    reportError(E_DUPLICATE_FIELD, member, "Repeated initializer for '" + member.name() + "'");
                }
                if (recordType instanceof SemanticType.Record record) {
                    SemanticType expectedType = record.fields().get(member.name());
                    if (expectedType == null) {
                        reportError(E_FIELD, member, "Unknown record field '" + member.name() + "'");
                    } else {
                        checkAssignmentCompatibility(expectedType, actualType,
                                member.value(), "Field '" + member.name() + "'");
                    }
                }
            }
            return recordType instanceof SemanticType.Record ? recordType : ERROR;
        }

        private SemanticType analyzeRoutineCall(RoutineCall call, Scope scope, boolean requiresValue) {
            Symbol symbol = resolveSymbol(call.name(), SymbolKind.ROUTINE, call, scope);
            List<SemanticType> argumentTypes = new ArrayList<>();
            for (Expression argument : call.arguments()) {
                argumentTypes.add(analyzeExpression(argument, scope));
            }
            if (!(symbol instanceof RoutineSymbol routine)) {
                return ERROR;
            }

            if (argumentTypes.size() != routine.parameterTypes.size()) {
                reportError(E_ARITY, call, "Routine '" + call.name() + "' expects "
                        + routine.parameterTypes.size() + " arguments, got " + argumentTypes.size());
            }
            int matchedArguments = Math.min(argumentTypes.size(), routine.parameterTypes.size());
            for (int index = 0; index < matchedArguments; index++) {
                checkAssignmentCompatibility(routine.parameterTypes.get(index), argumentTypes.get(index),
                        call.arguments().get(index), "Argument " + (index + 1));
            }
            if (requiresValue && symbol.type == VOID) {
                reportError(E_VOID, call, "Procedure '" + call.name() + "' cannot be used as a value");
                return ERROR;
            }
            expressionTypes.put(call, symbol.type);
            return symbol.type;
        }

        private SemanticType analyzeBinaryExpression(BinaryExpression binary, Scope scope) {
            SemanticType leftType = analyzeExpression(binary.left(), scope);
            SemanticType rightType = analyzeExpression(binary.right(), scope);

            return switch (binary.operator()) {
                case AND, OR, XOR -> {
                    checkAssignmentCompatibility(BOOLEAN, leftType, binary.left(), "Logical operand");
                    checkAssignmentCompatibility(BOOLEAN, rightType, binary.right(), "Logical operand");
                    yield BOOLEAN;
                }
                case EQUAL, NOT_EQUAL -> {
                    if (leftType != ERROR && rightType != ERROR
                            && !(isNumericType(leftType) && isNumericType(rightType))
                            && !isAssignmentCompatible(leftType, rightType)
                            && !isAssignmentCompatible(rightType, leftType)) {
                        reportError(E_TYPE, binary, "Cannot compare " + leftType + " and " + rightType);
                    }
                    yield BOOLEAN;
                }
                default -> {
                    checkNumericOperand(leftType, binary.left());
                    checkNumericOperand(rightType, binary.right());
                    checkConstantDivisor(binary);
                    yield switch (binary.operator()) {
                        case LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL -> BOOLEAN;
                        default -> promoteNumericTypes(leftType, rightType);
                    };
                }
            };
        }

        private void checkConstantDivisor(BinaryExpression binary) {
            if (binary.operator() != BinaryOperator.DIVIDE && binary.operator() != BinaryOperator.REMAINDER) {
                return;
            }
            if (ConstantEvaluator.evaluateConstant(binary.right()).filter(this::isZeroLiteral).isPresent()) {
                reportError(E_DIV_ZERO, binary.right(), "Constant divisor is zero");
            }
        }

        private boolean isZeroLiteral(Expression expression) {
            return switch (expression) {
                case IntegerLiteral literal -> literal.value() == 0;
                case RealLiteral literal -> literal.value() == 0;
                case BooleanLiteral literal -> !literal.value();
                default -> false;
            };
        }

        private boolean isNumericType(SemanticType type) {
            return type == INTEGER || type == REAL || type == BOOLEAN;
        }

        private SemanticType promoteNumericTypes(SemanticType left, SemanticType right) {
            if (left == ERROR || right == ERROR) {
                return ERROR;
            }
            return left == REAL || right == REAL ? REAL : INTEGER;
        }

        private void checkNumericOperand(SemanticType type, Expression operand) {
            if (type != ERROR && !isNumericType(type)) {
                reportError(E_TYPE, operand, "Numeric operand required, got " + type);
            }
        }

        /** Reports incompatible types and invalid constant conversions; does not cast the AST. */
        private void checkAssignmentCompatibility(SemanticType expected, SemanticType actual,
                                                  Expression value, String context) {
            if (expected == ERROR || actual == ERROR) {
                return;
            }
            if (!isAssignmentCompatible(expected, actual)) {
                reportError(E_TYPE, value, context + ": cannot convert " + actual + " to " + expected);
                return;
            }

            Optional<Expression> constant = ConstantEvaluator.evaluateConstant(value);
            if (expected == BOOLEAN && actual == INTEGER
                    && constant.orElse(null) instanceof IntegerLiteral integer
                    && integer.value() != 0 && integer.value() != 1) {
                reportError(E_CONVERSION, value, context + ": only integer 0 or 1 can convert to boolean");
            }
            if (expected == INTEGER && actual == REAL && constant.isPresent()
                    && ConstantEvaluator.evaluateInteger(value).isEmpty()) {
                reportError(E_CONVERSION, value, context + ": real value is outside the integer range");
            }
        }

        private boolean isAssignmentCompatible(SemanticType target, SemanticType source) {
            if (target == ERROR || source == ERROR) {
                return true;
            }
            if (target == source) {
                return target != VOID;
            }
            if (isNumericType(target) && isNumericType(source)) {
                return !(target == BOOLEAN && source == REAL);
            }
            if (target instanceof SemanticType.Record && source == NULL) {
                return true;
            }
            if (target instanceof SemanticType.Array targetArray && source instanceof SemanticType.Array sourceArray) {
                // Reference assignment cannot convert elements stored in the aliased array.
                return haveSameType(targetArray.element(), sourceArray.element())
                        && (targetArray.identity() == null || sourceArray.identity() == null
                            || targetArray.identity() == sourceArray.identity())
                        && (targetArray.size() == null || sourceArray.size() == null
                            || targetArray.size().equals(sourceArray.size()));
            }
            return false;
        }

        private boolean haveSameType(SemanticType first, SemanticType second) {
            if (first == second || first == ERROR || second == ERROR) {
                return true;
            }
            if (first instanceof SemanticType.Array firstArray && second instanceof SemanticType.Array secondArray) {
                return firstArray.identity() == secondArray.identity()
                        && Objects.equals(firstArray.size(), secondArray.size())
                        && haveSameType(firstArray.element(), secondArray.element());
            }
            return false;
        }
    }
}
