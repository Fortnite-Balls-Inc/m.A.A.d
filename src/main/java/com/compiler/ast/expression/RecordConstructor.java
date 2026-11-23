package com.compiler.ast.expression;

import java.util.List;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.SourcePosition;

public record RecordConstructor(String typeName,
        List<MemberInitialization> members, SourcePosition position)
        implements Expression {

    public RecordConstructor {
        members = List.copyOf(members);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth,
                "RecordConstructor typeName=\"" + typeName + "\"", position)
                .children(members);
    }
}
