package com.compiler.ast.types;

import java.util.Optional;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ArrayType(Optional<Expression> size, TypeNode type,
        SourcePosition position) implements UserType {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth, "ArrayType", position);
        branch.optionalChild("size", size);
        branch.child("type", type);
    }
}
