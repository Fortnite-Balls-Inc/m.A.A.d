package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import java.util.List;

public record PrintStatement(
    List<Expression> expressions,
    SourcePosition position
) implements Statement {
    public PrintStatement {
        expressions = List.copyOf(expressions);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "PrintStatement " + position.toString() + ":");
        if (expressions.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (Expression item : expressions) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
