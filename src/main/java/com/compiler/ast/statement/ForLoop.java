package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;

public record ForLoop(String variableName, IterationSource source,
        boolean reverse, Block body, SourcePosition position)
        implements Statement {

    @Override
    public void prettyPrint(int depth) {
        String description = "ForLoop variableName=\"" + variableName
                + "\" reverse=" + reverse;
        AstPrinter branch = AstPrinter.branch(depth, description, position);
        branch.child("source", source);
        branch.child("body", body);
    }
}
