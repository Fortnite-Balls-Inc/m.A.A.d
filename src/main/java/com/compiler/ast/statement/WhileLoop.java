package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.Block;

public record WhileLoop(
    Expression condition,
    Block body,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "WhileLoop " + position.toString() + ":");
        System.out.println(indent + "  condition:");
        condition.prettyPrint(h + 2);
        System.out.println(indent + "  body:");
        body.prettyPrint(h + 2);
    }
}
