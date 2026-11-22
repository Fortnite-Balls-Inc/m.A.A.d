package com.compiler.semantic;

import com.compiler.ast.SourcePosition;

public record Diagnostic(DiagnosticCode code, String message, SourcePosition position) {

    @Override
    public String toString() {
        return position.line() + ":" + position.column() + ": " + code + ": " + message;
    }
}
