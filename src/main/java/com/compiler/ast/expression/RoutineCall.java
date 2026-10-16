package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

import java.util.List;

public record RoutineCall(
    String name,
    List<Expression> arguments,
    SourcePosition position
) implements Expression {
    public RoutineCall {
        arguments = List.copyOf(arguments);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RoutineCall name=\"" + name + "\" " + position.toString() + ":");
        if (arguments.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (Expression item : arguments) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
