package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record FieldAccess(
    Expression target,
    String fieldName,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "FieldAccess fieldName=\"" + fieldName + "\" " + position.toString() + ":");
        target.prettyPrint(h + 1);
    }
}
