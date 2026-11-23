package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record Assignment(Expression target, Expression value,
        SourcePosition position) implements Statement {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "Assignment", position);
        branch.child("target", target);
        branch.child("value", value);
    }
}
