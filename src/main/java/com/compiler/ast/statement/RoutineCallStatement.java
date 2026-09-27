package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.RoutineCall;

public record RoutineCallStatement(
    RoutineCall call,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RoutineCallStatement " + position.toString() + ":");
        call.prettyPrint(h + 1);
    }
}
