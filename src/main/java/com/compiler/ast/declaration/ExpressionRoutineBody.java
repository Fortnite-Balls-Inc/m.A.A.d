package com.compiler.ast.declaration;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ExpressionRoutineBody(Expression expression,
        SourcePosition position) implements RoutineBody {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "ExpressionRoutineBody", position)
                .child(expression);
    }
}
