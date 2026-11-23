package com.compiler.ast.declaration;

import java.util.Optional;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.types.TypeNode;

public record VariableDeclaration(String name, Optional<TypeNode> type,
        Optional<Expression> initializer, SourcePosition position)
        implements SimpleDeclaration {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth,
                "VariableDeclaration name=\"" + name + "\"", position);
        branch.optionalChild("type", type);
        branch.optionalChild("initializer", initializer);
    }
}
