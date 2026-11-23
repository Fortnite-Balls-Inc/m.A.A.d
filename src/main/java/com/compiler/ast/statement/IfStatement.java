package com.compiler.ast.statement;

import java.util.Optional;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record IfStatement(Expression condition, Block thenBlock,
        Optional<Block> elseBlock, SourcePosition position)
        implements Statement {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "IfStatement", position);
        branch.child("condition", condition);
        branch.child("then", thenBlock);
        branch.optionalChild("else", elseBlock);
    }
}
