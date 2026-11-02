package com.compiler.ast.statement;

import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;

/** Optimizer-only node: a selected constant branch must retain its lexical scope. */
public record ScopedBlockStatement(Block block, SourcePosition position) implements Statement {

    @Override
    public void prettyPrint(int depth) {
        System.out.println("  ".repeat(depth) + "ScopedBlockStatement " + position + ":");
        block.prettyPrint(depth + 1);
    }
}
