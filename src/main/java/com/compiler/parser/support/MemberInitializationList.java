package com.compiler.parser.support;

import com.compiler.ast.expression.MemberInitialization;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's right-recursive lists. */
public final class MemberInitializationList {
    private final LinkedList<MemberInitialization> values = new LinkedList<>();

    public void addFirst(MemberInitialization value) {
        values.addFirst(value);
    }

    public List<MemberInitialization> values() {
        return values;
    }
}
