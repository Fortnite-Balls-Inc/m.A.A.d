package com.compiler.ast.statement;

import java.util.List;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record PrintStatement(List<Expression> expressions,
        SourcePosition position) implements Statement {

    public PrintStatement {
        expressions = List.copyOf(expressions);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "PrintStatement", position)
                .children(expressions);
    }
}
