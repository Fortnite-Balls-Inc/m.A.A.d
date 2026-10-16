package com.compiler.ast.declaration;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.types.TypeNode;

public record TypeDeclaration(
    String name,
    TypeNode type,
    SourcePosition position
) implements SimpleDeclaration {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "TypeDeclaration name=\"" + name + "\" " + position.toString() + ":");
        type.prettyPrint(h + 1);
    }
}
