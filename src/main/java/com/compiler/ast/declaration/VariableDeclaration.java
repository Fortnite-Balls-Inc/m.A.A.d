package com.compiler.ast.declaration;

import java.util.Optional;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.types.TypeNode;

public record VariableDeclaration(
    String name,
    Optional<TypeNode> type,
    Optional<Expression> initializer,
    SourcePosition position
) implements SimpleDeclaration {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "VariableDeclaration name=\"" + name + "\" " + position.toString() + ":");
        if (type.isPresent()) {
            System.out.println(indent + "  type:");
            type.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  type: none");
        }
        if (initializer.isPresent()) {
            System.out.println(indent + "  initializer:");
            initializer.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  initializer: none");
        }
    }
}
