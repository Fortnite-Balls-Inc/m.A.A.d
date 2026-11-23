package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record BooleanLiteral(boolean value, SourcePosition position)
        implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.leaf(depth, "BooleanLiteral value=" + value, position);
    }
}
