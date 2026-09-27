package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record RangeSource(
    Expression start,
    Expression end,
    SourcePosition position
) implements IterationSource {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RangeSource " + position.toString() + ":");
        System.out.println(indent + "  start:");
        start.prettyPrint(h + 2);
        System.out.println(indent + "  end:");
        end.prettyPrint(h + 2);
    }
}
