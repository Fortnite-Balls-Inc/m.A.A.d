package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record WhileLoop(Expression condition, Block body,
        SourcePosition position) implements Statement {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "WhileLoop", position);
        branch.child("condition", condition);
        branch.child("body", body);
    }
}
