package com.compiler.ast.types;

import com.compiler.ast.SourcePosition;

public record NamedType(
    String name,
    SourcePosition position
) implements TypeNode {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "[NamedType name=\"" + name + "\" " + position.toString() + "]");
    }
}
