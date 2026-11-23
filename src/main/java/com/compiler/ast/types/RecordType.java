package com.compiler.ast.types;

import java.util.List;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;
import com.compiler.ast.declaration.VariableDeclaration;

public record RecordType(List<VariableDeclaration> fields,
        SourcePosition position) implements UserType {

    public RecordType {
        fields = List.copyOf(fields);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "RecordType", position).children(fields);
    }
}
