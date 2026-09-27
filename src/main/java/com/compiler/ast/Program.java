package com.compiler.ast;

import com.compiler.ast.declaration.Declaration;
import java.util.List;

/** The parser uses position (1, 1), the start of the file, including for empty programs. */
public record Program(
    List<Declaration> declarations,
    SourcePosition position
) implements Node {
    public Program {
        declarations = List.copyOf(declarations);
    }

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "Program " + position.toString() + ":");
        if (declarations.isEmpty()) {
            System.out.println(indent + "  []");
        } else {
            for (Declaration item : declarations) {
                item.prettyPrint(h + 1);
            }
        }
    }
}
