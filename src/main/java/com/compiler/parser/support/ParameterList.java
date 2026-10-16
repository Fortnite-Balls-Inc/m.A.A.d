package com.compiler.parser.support;

import com.compiler.ast.declaration.Parameter;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's left-recursive parameter list. */
public final class ParameterList {
    private final LinkedList<Parameter> values = new LinkedList<>();

    public void addLast(Parameter value) {
        values.addLast(value);
    }

    public List<Parameter> values() {
        return values;
    }
}
