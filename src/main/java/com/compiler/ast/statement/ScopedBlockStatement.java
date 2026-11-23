package com.compiler.ast.statement;

import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;

/** Retains the lexical scope of a branch selected by the optimizer. */
public record ScopedBlockStatement(Block block, SourcePosition position)
        implements Statement {

    @Override
    public void prettyPrint(int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "ScopedBlockStatement " + position + ":");
        block.prettyPrint(depth + 1);
    }
}
