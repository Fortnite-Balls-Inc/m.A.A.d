package com.compiler.parser.support;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.types.TypeNode;
import java.util.List;
import java.util.Optional;

public record RoutineHeader(
    String name,
    List<Parameter> parameters,
    Optional<TypeNode> returnType,
    SourcePosition position
) {
}
