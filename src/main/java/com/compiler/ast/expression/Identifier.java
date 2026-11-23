package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record Identifier(String name, SourcePosition position)
        implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.leaf(depth, "Identifier name=\"" + name + "\"", position);
    }
}
