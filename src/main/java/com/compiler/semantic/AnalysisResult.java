package com.compiler.semantic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.compiler.ast.Node;
import com.compiler.ast.Program;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.types.TypeNode;

/** Types and bindings indexed by the identity of nodes in the input program. */
public record AnalysisResult(
        Program program, List<Diagnostic> diagnostics,
        Map<Expression, SemanticType> expressionTypes,
        Set<VariableDeclaration> usedVariables,
        Map<VariableDeclaration, SemanticType> variableTypes,
        Map<TypeNode, SemanticType> resolvedTypes,
        Map<Identifier, Node> variableReferences) {

    public AnalysisResult {
        diagnostics = List.copyOf(diagnostics);
        expressionTypes = copyIdentityMap(expressionTypes);
        variableTypes = copyIdentityMap(variableTypes);
        resolvedTypes = copyIdentityMap(resolvedTypes);
        variableReferences = copyIdentityMap(variableReferences);

        Set<VariableDeclaration> referenced =
                Collections.newSetFromMap(new IdentityHashMap<>());
        referenced.addAll(usedVariables);
        usedVariables = Collections.unmodifiableSet(referenced);
    }

    public boolean isValid() {
        return diagnostics.isEmpty();
    }

    private static <K, V> Map<K, V> copyIdentityMap(Map<K, V> entries) {
        return Collections.unmodifiableMap(new IdentityHashMap<>(entries));
    }

    /** Collects facts for one analysis before publishing an immutable copy. */
    static final class Builder {
        private final List<Diagnostic> diagnostics = new ArrayList<>();
        private final Map<Expression, SemanticType> expressionTypes =
                new IdentityHashMap<>();
        private final Set<VariableDeclaration> usedVariables =
                Collections.newSetFromMap(new IdentityHashMap<>());
        private final Map<VariableDeclaration, SemanticType> variableTypes =
                new IdentityHashMap<>();
        private final Map<TypeNode, SemanticType> resolvedTypes =
                new IdentityHashMap<>();
        private final Map<Identifier, Node> variableReferences =
                new IdentityHashMap<>();

        void report(DiagnosticCode code, Node node, String message) {
            diagnostics.add(new Diagnostic(code, message, node.position()));
        }

        void bind(Identifier identifier, Node declaration) {
            variableReferences.put(identifier, declaration);
            if (declaration instanceof VariableDeclaration variable) {
                usedVariables.add(variable);
            }
        }

        void setType(Expression expression, SemanticType type) {
            expressionTypes.put(expression, type);
        }

        void setType(TypeNode syntax, SemanticType type) {
            resolvedTypes.put(syntax, type);
        }

        void setType(VariableDeclaration variable, SemanticType type) {
            variableTypes.put(variable, type);
        }

        SemanticType typeOf(Expression expression) {
            return expressionTypes.get(expression);
        }

        AnalysisResult build(Program program) {
            return new AnalysisResult(program, diagnostics, expressionTypes,
                    usedVariables, variableTypes, resolvedTypes,
                    variableReferences);
        }
    }
}
