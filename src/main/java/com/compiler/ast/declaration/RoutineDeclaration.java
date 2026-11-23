package com.compiler.ast.declaration;

import java.util.List;
import java.util.Optional;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.types.TypeNode;

public record RoutineDeclaration(String name, List<Parameter> parameters,
        Optional<TypeNode> returnType, Optional<RoutineBody> body,
        SourcePosition position) implements Declaration {

    public RoutineDeclaration {
        parameters = List.copyOf(parameters);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter branch = AstPrinter.branch(depth,
                "RoutineDeclaration name=\"" + name + "\"", position);
        branch.children("parameters", parameters);
        branch.optionalChild("returnType", returnType);
        branch.optionalChild("body", body);
    }
}
