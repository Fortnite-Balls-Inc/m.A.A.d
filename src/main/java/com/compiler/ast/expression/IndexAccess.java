package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record IndexAccess(
    Expression target,
    Expression index,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "IndexAccess " + position.toString() + ":");
        System.out.println(indent + "  target:");
        target.prettyPrint(h + 2);
        System.out.println(indent + "  index:");
        index.prettyPrint(h + 2);
    }
}
