package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record RealLiteral(double value, SourcePosition position)
        implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.leaf(depth, "RealLiteral value=" + value, position);
    }
}
