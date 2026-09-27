package com.compiler.ast;

public record SourcePosition(int line, int column) {
    @Override
    public String toString() {
        return "(line=" + line + ", column=" + column + ")";
    }
}
