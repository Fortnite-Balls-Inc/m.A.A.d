package com.compiler.ast.statement;

import java.util.Optional;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ReturnStatement(Optional<Expression> value,
        SourcePosition position) implements Statement {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "ReturnStatement", position)
                .optionalChild("value", value);
    }
}
