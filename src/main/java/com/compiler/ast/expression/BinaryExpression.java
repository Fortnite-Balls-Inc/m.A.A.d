package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record BinaryExpression(BinaryOperator operator, Expression left,
        Expression right, SourcePosition position) implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth,
                "BinaryExpression operator=" + operator, position);
        branch.child("left", left);
        branch.child("right", right);
    }
}
