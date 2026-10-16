package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record Assignment(
    Expression target,
    Expression value,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "Assignment " + position.toString() + ":");
        System.out.println(indent + "  target:");
        target.prettyPrint(h + 2);
        System.out.println(indent + "  value:");
        value.prettyPrint(h + 2);
    }
}
