package com.compiler.ast.declaration;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.types.TypeNode;
import java.util.List;
import java.util.Optional;

public record RoutineDeclaration(
    String name,
    List<Parameter> parameters,
    Optional<TypeNode> returnType,
    Optional<RoutineBody> body,
    SourcePosition position
) implements Declaration {
    public RoutineDeclaration {
        parameters = List.copyOf(parameters);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RoutineDeclaration name=\"" + name + "\" " + position.toString() + ":");
        if (parameters.isEmpty()) {
            System.out.println(indent + "  parameters: []");
        } else {
            System.out.println(indent + "  parameters:");
            for (Parameter item : parameters) {
                item.prettyPrint(h + 2);
            }
        }
        if (returnType.isPresent()) {
            System.out.println(indent + "  returnType:");
            returnType.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  returnType: none");
        }
        if (body.isPresent()) {
            System.out.println(indent + "  body:");
            body.get().prettyPrint(h + 2);
        } else {
            System.out.println(indent + "  body: none");
        }
    }
}
