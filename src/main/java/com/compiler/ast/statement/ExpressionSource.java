package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ExpressionSource(Expression expression, SourcePosition position)
        implements IterationSource {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "ExpressionSource", position)
                .child(expression);
    }
}
