package com.compiler.parser.support;

import com.compiler.ast.BlockItem;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's right-recursive lists. */
public final class BlockItemList {
    private final LinkedList<BlockItem> values = new LinkedList<>();

    public void addFirst(BlockItem value) {
        values.addFirst(value);
    }

    public List<BlockItem> values() {
        return values;
    }
}
