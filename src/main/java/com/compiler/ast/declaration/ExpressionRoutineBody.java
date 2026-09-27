package com.compiler.ast.declaration;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ExpressionRoutineBody(
    Expression expression,
    SourcePosition position
) implements RoutineBody {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "ExpressionRoutineBody " + position.toString() + ":");
        expression.prettyPrint(h + 1);
    }
}
