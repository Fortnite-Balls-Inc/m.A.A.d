package com.compiler.ast;

public interface Node {
    SourcePosition position();

    default void prettyPrint() {
        prettyPrint(0);
    }

    void prettyPrint(int h);
}
