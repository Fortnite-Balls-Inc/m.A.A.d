package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record RangeSource(Expression start, Expression end,
        SourcePosition position) implements IterationSource {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "RangeSource", position);
        branch.child("start", start);
        branch.child("end", end);
    }
}
