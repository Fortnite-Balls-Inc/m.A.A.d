package com.compiler.ast.types;

import java.util.Optional;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;

public record ArrayType(
    Optional<Expression> size,
    TypeNode type,
    SourcePosition position
) implements UserType {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "ArrayType " + position.toString() + ":");
        if (size.isPresent()) {
            System.out.println(indent + "  size:");
            size.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  size: none");
        }
        System.out.println(indent + "  type:");
        type.prettyPrint(h + 2);
    }
}
