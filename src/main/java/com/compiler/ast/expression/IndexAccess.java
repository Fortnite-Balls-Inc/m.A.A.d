package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record IndexAccess(Expression target, Expression index,
        SourcePosition position) implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "IndexAccess", position);
        branch.child("target", target);
        branch.child("index", index);
    }
}
