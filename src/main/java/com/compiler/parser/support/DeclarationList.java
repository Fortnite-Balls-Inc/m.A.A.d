package com.compiler.parser.support;

import com.compiler.ast.declaration.Declaration;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's right-recursive lists. */
public final class DeclarationList {
    private final LinkedList<Declaration> values = new LinkedList<>();

    public void addFirst(Declaration value) {
        values.addFirst(value);
    }

    public List<Declaration> values() {
        return values;
    }
}
