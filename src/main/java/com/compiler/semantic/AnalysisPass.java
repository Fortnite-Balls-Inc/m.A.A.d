package com.compiler.semantic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import com.compiler.ast.expression.Expression;
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

import static com.compiler.semantic.DiagnosticCode.E_ARRAY_SIZE;
import static com.compiler.semantic.DiagnosticCode.E_INFER;
import static com.compiler.semantic.DiagnosticCode.E_ITERATION;
import static com.compiler.semantic.DiagnosticCode.E_MISSING_RETURN;
import static com.compiler.semantic.DiagnosticCode.E_RETURN;
import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.ERROR;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.NULL;
import static com.compiler.semantic.SemanticType.Scalar.REAL;
import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Walks declarations and statements with fresh state for one program. */
final class AnalysisPass {
    private final AnalysisResult.Builder result = new AnalysisResult.Builder();
    private final ExpressionAnalyzer expressions =
            new ExpressionAnalyzer(result);

    AnalysisResult analyzeProgram(Program program) {
        Scope globals = new Scope(result);
        for (Declaration declaration : program.declarations()) {
            switch (declaration) {
                case RoutineDeclaration routine ->
                        analyzeRoutine(routine, globals);
                case SimpleDeclaration simple ->
                        analyzeDeclaration(simple, globals);
            }
        }
        globals.reportUndefinedRoutines();
        return result.build(program);
    }

    private void analyzeDeclaration(SimpleDeclaration declaration,
                                    Scope scope) {
        switch (declaration) {
            case VariableDeclaration variable ->
                    analyzeVariable(variable, scope);
            case TypeDeclaration type -> analyzeType(type, scope);
        }
    }

    private void analyzeType(TypeDeclaration declaration, Scope scope) {
        TypeNode syntax = declaration.type();
        SemanticType type = resolveType(syntax, scope);

        // Array and record declarations introduce identities. An alias
        // of an existing type keeps that type's original identity.
        if (syntax instanceof ArrayType
                && type instanceof SemanticType.Array array) {
            type = new SemanticType.Array(array.element(), array.size(),
                    declaration);
        } else if (syntax instanceof RecordType
                && type instanceof SemanticType.Record record) {
            type = new SemanticType.Record(declaration.name(), record.fields());
        }
        result.setType(syntax, type);
        scope.declareType(declaration, type);
    }

    private SemanticType analyzeVariable(VariableDeclaration variable,
                                         Scope scope) {
        SemanticType declaredType = variable.type()
                .map(type -> resolveType(type, scope)).orElse(null);
        SemanticType initialType = variable.initializer()
                .map(value -> expressions.analyze(value, scope))
                .orElse(null);
        SemanticType type = declaredType != null ? declaredType : initialType;
        if (declaredType == null
                && (type == null || type == NULL || type == VOID)) {
            result.report(E_INFER, variable, "Cannot infer a variable "
                    + "type; provide an explicit value type");
            type = ERROR;
        }
        if (declaredType != null && initialType != null) {
            expressions.checkConversion(declaredType, initialType,
                    variable.initializer().orElseThrow(), "Initializer");
        }

        // The initializer sees only the preceding declarations.
        result.setType(variable, type);
        scope.declareVariable(variable.name(), type, variable);
        return type;
    }

    private SemanticType resolveType(TypeNode syntax, Scope scope) {
        SemanticType type = switch (syntax) {
            case PrimitiveType primitive -> switch (primitive.kind()) {
                case INTEGER -> INTEGER;
                case REAL -> REAL;
                case BOOLEAN -> BOOLEAN;
            };
            case NamedType named -> scope.resolveType(named.name(), named);
            case ArrayType array -> resolveArray(array, scope);
            case RecordType record -> resolveRecord(record, scope);
        };
        result.setType(syntax, type);
        return type;
    }

    private SemanticType resolveArray(ArrayType array, Scope scope) {
        Integer size = null;
        if (array.size().isPresent()) {
            Expression length = array.size().get();
            expressions.check(length, scope, INTEGER, "Array size");
            size = ConstantEvaluator.evaluateInteger(length).orElse(null);
            if (size != null && size < 0) {
                result.report(E_ARRAY_SIZE, length,
                        "Array size must be non-negative");
            }
        }
        return new SemanticType.Array(resolveType(array.type(), scope),
                size, null);
    }

    private SemanticType resolveRecord(RecordType record, Scope scope) {
        Scope fieldScope = scope.child();
        Map<String, SemanticType> fields = new LinkedHashMap<>();
        for (VariableDeclaration field : record.fields()) {
            fields.put(field.name(), analyzeVariable(field, fieldScope));
        }
        return new SemanticType.Record(fields);
    }

    private void analyzeRoutine(RoutineDeclaration routine, Scope globals) {
        Scope scope = globals.child();
        List<SemanticType> parameters = new ArrayList<>();
        for (Parameter parameter : routine.parameters()) {
            SemanticType type = resolveType(parameter.type(), scope);
            parameters.add(type);
            scope.declareVariable(parameter.name(), type, parameter);
        }
        SemanticType returnType = routine.returnType()
                .map(type -> resolveType(type, scope)).orElse(VOID);
        // A recursive call must see the routine before its body is checked.
        globals.declareRoutine(routine, parameters, returnType);

        if (routine.body().isEmpty()) {
            return;
        }
        switch (routine.body().get()) {
            case ExpressionRoutineBody body -> {
                SemanticType type = expressions.analyze(body.expression(),
                        scope);
                if (returnType == VOID) {
                    result.report(E_RETURN, body, "An expression body "
                            + "requires a declared return type");
                } else {
                    expressions.checkConversion(returnType, type,
                            body.expression(), "Return value");
                }
            }
            case BlockRoutineBody body -> {
                analyzeBlock(body.block(), scope, returnType);
                if (returnType != VOID && returnType != ERROR
                        && !ControlFlow.preventsFallThrough(body.block())) {
                    result.report(E_MISSING_RETURN, routine, "Routine '"
                            + routine.name() + "' can reach its end "
                            + "without returning a value");
                }
            }
        }
    }

    private void analyzeBlock(Block block, Scope scope,
                              SemanticType returnType) {
        // Unreachable statements still participate in semantic analysis.
        for (BlockItem item : block.items()) {
            switch (item) {
                case SimpleDeclaration declaration ->
                        analyzeDeclaration(declaration, scope);
                case Statement statement ->
                        analyzeStatement(statement, scope, returnType);
                default -> throw unsupportedNode(item);
            }
        }
    }

    private void analyzeStatement(Statement statement, Scope scope,
                                  SemanticType returnType) {
        switch (statement) {
            case Assignment assignment -> {
                Expression target = assignment.target();
                SemanticType type = expressions.analyze(target, scope);
                expressions.checkWritable(target, scope);
                expressions.check(assignment.value(), scope, type,
                        "Assignment");
            }
            case PrintStatement print -> {
                for (Expression value : print.expressions()) {
                    expressions.analyze(value, scope);
                }
            }
            case RoutineCallStatement call ->
                    expressions.analyzeCall(call.call(), scope, false);
            case ReturnStatement value ->
                    analyzeReturn(value, scope, returnType);
            case IfStatement conditional -> {
                expressions.check(conditional.condition(), scope, BOOLEAN,
                        "If condition");
                analyzeBlock(conditional.thenBlock(), scope.child(),
                        returnType);
                conditional.elseBlock().ifPresent(block ->
                        analyzeBlock(block, scope.child(), returnType));
            }
            case WhileLoop loop -> {
                expressions.check(loop.condition(), scope, BOOLEAN,
                        "While condition");
                analyzeBlock(loop.body(), scope.child(), returnType);
            }
            case ForLoop loop -> analyzeForLoop(loop, scope, returnType);
            case ScopedBlockStatement block ->
                    analyzeBlock(block.block(), scope.child(), returnType);
        }
    }

    private void analyzeReturn(ReturnStatement statement, Scope scope,
                               SemanticType returnType) {
        if (statement.value().isEmpty()) {
            if (returnType != VOID && returnType != ERROR) {
                result.report(E_RETURN, statement,
                        "A function must return a value of type "
                                + returnType);
            }
            return;
        }

        Expression value = statement.value().get();
        SemanticType type = expressions.analyze(value, scope);
        if (returnType == VOID) {
            result.report(E_RETURN, statement,
                    "A procedure cannot return a value");
        } else {
            expressions.checkConversion(returnType, type, value,
                    "Return value");
        }
    }

    private void analyzeForLoop(ForLoop loop, Scope scope,
                                SemanticType returnType) {
        SemanticType element = switch (loop.source()) {
            case RangeSource range -> {
                expressions.check(range.start(), scope, INTEGER, "Range start");
                expressions.check(range.end(), scope, INTEGER, "Range end");
                yield INTEGER;
            }
            case ExpressionSource source -> {
                SemanticType type = expressions.analyze(source.expression(),
                        scope);
                if (type instanceof SemanticType.Array array) {
                    yield array.element();
                }
                if (type != ERROR) {
                    result.report(E_ITERATION, source,
                            "A single-expression for loop requires an array");
                }
                yield ERROR;
            }
            default -> throw unsupportedNode(loop.source());
        };
        Scope loopScope = scope.child();
        loopScope.declareVariable(loop.variableName(), element, loop);
        analyzeBlock(loop.body(), loopScope, returnType);
    }

    private static IllegalArgumentException unsupportedNode(Node node) {
        return new IllegalArgumentException(
                "Unsupported AST node: " + node.getClass());
    }
}
