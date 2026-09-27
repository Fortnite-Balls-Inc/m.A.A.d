package com.compiler.ast.expression;

import com.compiler.ast.SourcePosition;

import java.util.List;

public record RecordConstructor(
    String typeName,
    List<MemberInitialization> members,
    SourcePosition position
) implements Expression {
    public RecordConstructor {
        members = List.copyOf(members);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "RecordConstructor typeName=\"" + typeName + "\" " + position.toString() + ":");
        if (members.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (MemberInitialization item : members) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
