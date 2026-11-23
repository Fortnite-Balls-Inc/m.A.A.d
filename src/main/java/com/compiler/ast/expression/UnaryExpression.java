package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record UnaryExpression(UnaryOperator operator, Expression operand,
        SourcePosition position) implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth,
                "UnaryExpression operator=" + operator, position)
                .child(operand);
    }
}
