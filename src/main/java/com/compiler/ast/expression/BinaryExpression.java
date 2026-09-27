package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record BinaryExpression(
    BinaryOperator operator,
    Expression left,
    Expression right,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "BinaryExpression operator=" + operator + " " + position.toString() + ":");
        System.out.println(indent + "  left:");
        left.prettyPrint(h + 2);
        System.out.println(indent + "  right:");
        right.prettyPrint(h + 2);
    }
}
