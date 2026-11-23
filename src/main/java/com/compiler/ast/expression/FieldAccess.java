package com.compiler.ast.expression;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record FieldAccess(Expression target, String fieldName,
        SourcePosition position) implements Expression {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth,
                "FieldAccess fieldName=\"" + fieldName + "\"", position)
                .child(target);
    }
}
