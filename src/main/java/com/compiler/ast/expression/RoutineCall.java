package com.compiler.ast.expression;

import java.util.List;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record RoutineCall(String name, List<Expression> arguments,
        SourcePosition position) implements Expression {

    public RoutineCall {
        arguments = List.copyOf(arguments);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "RoutineCall name=\"" + name + "\"", position)
                .children(arguments);
    }
}
