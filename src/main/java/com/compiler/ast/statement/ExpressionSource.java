package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ExpressionSource(
    Expression expression,
    SourcePosition position
) implements IterationSource {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "ExpressionSource " + position.toString() + ":");
        expression.prettyPrint(h + 1);
    }
}
