package com.compiler.ast.statement;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.RoutineCall;

public record RoutineCallStatement(RoutineCall call, SourcePosition position)
        implements Statement {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "RoutineCallStatement", position).child(call);
    }
}
