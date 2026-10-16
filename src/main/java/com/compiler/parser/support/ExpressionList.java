package com.compiler.parser.support;

import com.compiler.ast.expression.Expression;

import java.util.LinkedList;
import java.util.List;

/** Typed accumulator for the grammar's right-recursive lists. */
public final class ExpressionList {
    private final LinkedList<Expression> values = new LinkedList<>();

    public void addFirst(Expression value) {
        values.addFirst(value);
    }

    public List<Expression> values() {
        return values;
    }
}
