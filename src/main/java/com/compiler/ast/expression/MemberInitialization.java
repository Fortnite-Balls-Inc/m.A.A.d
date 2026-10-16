package com.compiler.ast.expression;

import com.compiler.ast.Node;
import com.compiler.ast.SourcePosition;

public record MemberInitialization(
    String name,
    Expression value,
    SourcePosition position
) implements Node {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "MemberInitialization name=\"" + name + "\" " + position.toString() + ":");
        value.prettyPrint(h + 1);
    }
}
