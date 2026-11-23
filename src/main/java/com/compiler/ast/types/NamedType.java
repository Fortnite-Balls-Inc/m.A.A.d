package com.compiler.ast.types;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record NamedType(String name, SourcePosition position)
        implements TypeNode {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.leaf(depth, "NamedType name=\"" + name + "\"", position);
    }
}
