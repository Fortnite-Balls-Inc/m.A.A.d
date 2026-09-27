package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.Block;

public record ForLoop(
    String variableName,
    IterationSource source,
    boolean reverse,
    Block body,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "ForLoop variableName=\"" + variableName + "\" reverse=" + reverse + " " + position.toString() + ":");
        System.out.println(indent + "  source:");
        source.prettyPrint(h + 2);
        System.out.println(indent + "  body:");
        body.prettyPrint(h + 2);
    }
}
