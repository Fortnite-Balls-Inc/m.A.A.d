package com.compiler.ast.declaration;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.types.TypeNode;

public record TypeDeclaration(String name, TypeNode type,
        SourcePosition position) implements SimpleDeclaration {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth,
                "TypeDeclaration name=\"" + name + "\"", position)
                .child(type);
    }
}
