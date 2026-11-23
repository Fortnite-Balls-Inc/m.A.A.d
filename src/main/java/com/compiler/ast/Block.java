package com.compiler.ast;

import java.util.List;

public record Block(List<BlockItem> items, SourcePosition position)
        implements Node {

    public Block {
        items = List.copyOf(items);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "Block", position).children(items);
    }
}
