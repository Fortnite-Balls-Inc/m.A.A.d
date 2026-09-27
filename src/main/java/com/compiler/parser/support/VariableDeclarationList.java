package com.compiler.parser.support;

import com.compiler.ast.declaration.VariableDeclaration;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's right-recursive lists. */
public final class VariableDeclarationList {
    private final LinkedList<VariableDeclaration> values = new LinkedList<>();

    public void addFirst(VariableDeclaration value) {
        values.addFirst(value);
    }

    public List<VariableDeclaration> values() {
        return values;
    }
}
