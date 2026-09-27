package com.compiler.ast.statement;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.Block;
import java.util.Optional;

public record IfStatement(
    Expression condition,
    Block thenBlock,
    Optional<Block> elseBlock,
    SourcePosition position
) implements Statement {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "IfStatement " + position.toString() + ":");
        System.out.println(indent + "  condition:");
        condition.prettyPrint(h + 2);
        System.out.println(indent + "  then:");
        thenBlock.prettyPrint(h + 2);
        if (elseBlock.isPresent()) {
            System.out.println(indent + "  else:");
            elseBlock.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  else: none");
        }
    }
}
