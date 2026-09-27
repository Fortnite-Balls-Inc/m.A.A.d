package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import java.util.Optional;

public record ReturnStatement(
    Optional<Expression> value,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "ReturnStatement " + position.toString() + ":");
        if (value.isPresent()) {
            System.out.println(indent + "  value:");
            value.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  value: none");
        }
    }
}
