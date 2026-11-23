package com.compiler.ast.types;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record PrimitiveType(PrimitiveKind kind, SourcePosition position)
        implements TypeNode {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.leaf(depth, "PrimitiveType kind=" + kind, position);
    }
}
