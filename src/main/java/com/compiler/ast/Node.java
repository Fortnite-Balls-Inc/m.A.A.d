package com.compiler.ast;

/** A syntax tree node and its location in the source program. */
public interface Node {
    SourcePosition position();

    default void prettyPrint() {
        prettyPrint(0);
    }

    /** Writes this subtree with two spaces of indentation per level. */
    void prettyPrint(int depth);
}
