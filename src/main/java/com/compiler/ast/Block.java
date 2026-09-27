package com.compiler.ast;

import java.util.List;

public record Block(
    List<BlockItem> items,
    SourcePosition position
) implements Node {
    public Block {
        items = List.copyOf(items);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "Block " + position.toString() + ":");
        if (items.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (BlockItem item : items) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
