package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.Node;
import com.compiler.ast.SourcePosition;

public record MemberInitialization(String name, Expression value,
        SourcePosition position) implements Node {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth,
                "MemberInitialization name=\"" + name + "\"", position)
                .child(value);
    }
}
