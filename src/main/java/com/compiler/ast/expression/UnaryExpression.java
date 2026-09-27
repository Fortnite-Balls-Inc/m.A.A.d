package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

public record UnaryExpression(
    UnaryOperator operator,
    Expression operand,
    SourcePosition position
) implements Expression {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "UnaryExpression operator=" + operator + " " + position.toString() + ":");
        operand.prettyPrint(h + 1);
    }
}
