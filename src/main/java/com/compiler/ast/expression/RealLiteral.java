package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record RealLiteral(
    double value,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "[RealLiteral value=" + value + " " + position.toString() + "]");
    }
}
