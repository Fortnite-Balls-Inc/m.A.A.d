package com.compiler.parser.support;

import java.util.List;
import java.util.Optional;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineBody;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.types.TypeNode;

/** The signature shared by forward declarations and routine definitions. */
public record RoutineHeader(String name, List<Parameter> parameters,
        Optional<TypeNode> returnType, SourcePosition position) {
    public RoutineDeclaration declaration(Optional<RoutineBody> body) {
        return new RoutineDeclaration(name, parameters, returnType, body,
                position);
    }
}
