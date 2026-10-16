package com.compiler.ast.declaration;

import com.compiler.ast.Node;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.types.TypeNode;

public record Parameter(
    String name,
    TypeNode type,
    SourcePosition position
) implements Node {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "Parameter name=\"" + name + "\" " + position.toString() + ":");
        type.prettyPrint(h + 1);
    }
}
