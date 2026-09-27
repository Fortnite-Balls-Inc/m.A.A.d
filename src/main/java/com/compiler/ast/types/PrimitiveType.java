package com.compiler.ast.types;

import com.compiler.ast.SourcePosition;

public record PrimitiveType(
    PrimitiveKind kind,
    SourcePosition position
) implements TypeNode{

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "[PrimitiveType kind=" + kind + " " + position.toString() + "]");
    }
}
