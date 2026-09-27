package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record Identifier(
        String name,
        SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "[Identifier name=\"" + name + "\" " + position.toString() + "]");
    }
}
