package com.compiler.semantic;

import com.compiler.ast.Node;
import com.compiler.ast.Program;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.types.TypeNode;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Identity maps refer to nodes of this input program, never to rewritten nodes. */
public record AnalysisResult(Program program, List<Diagnostic> diagnostics,
                             Map<Expression, SemanticType> expressionTypes,
                             Set<VariableDeclaration> usedVariables,
                             Map<VariableDeclaration, SemanticType> variableTypes,
                             Map<TypeNode, SemanticType> resolvedTypes,
                             Map<Identifier, Node> variableReferences) {

    public AnalysisResult {
        diagnostics = List.copyOf(diagnostics);
        expressionTypes = Collections.unmodifiableMap(new IdentityHashMap<>(expressionTypes));
        variableTypes = Collections.unmodifiableMap(new IdentityHashMap<>(variableTypes));
        resolvedTypes = Collections.unmodifiableMap(new IdentityHashMap<>(resolvedTypes));
        variableReferences = Collections.unmodifiableMap(new IdentityHashMap<>(variableReferences));

        Set<VariableDeclaration> referencedVariables = Collections.newSetFromMap(new IdentityHashMap<>());
        referencedVariables.addAll(usedVariables);
        usedVariables = Collections.unmodifiableSet(referencedVariables);
    }

    public boolean isValid() {
        return diagnostics.isEmpty();
    }
}
