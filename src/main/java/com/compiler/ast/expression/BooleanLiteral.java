package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record BooleanLiteral(
    boolean value,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "[BooleanLiteral value=" + value + " " + position.toString() + "]");
    }
}
