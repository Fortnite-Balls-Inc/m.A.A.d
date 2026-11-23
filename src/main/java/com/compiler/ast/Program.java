package com.compiler.ast;

import java.util.List;

import com.compiler.ast.declaration.Declaration;

/** The program starts at line 1, column 1, even in an empty file. */
public record Program(List<Declaration> declarations, SourcePosition position)
        implements Node {

    public Program {
        declarations = List.copyOf(declarations);
    }

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "Program", position).children(declarations);
    }
}
