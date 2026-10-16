package com.compiler.ast.types;

import java.util.List;

import com.compiler.ast.SourcePosition;
import com.compiler.ast.declaration.VariableDeclaration;

public record RecordType(
    List<VariableDeclaration> fields,
    SourcePosition position
) implements UserType {
    public RecordType {
        fields = List.copyOf(fields);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RecordType " + position.toString() + ":");
        if (fields.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (VariableDeclaration item : fields) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
