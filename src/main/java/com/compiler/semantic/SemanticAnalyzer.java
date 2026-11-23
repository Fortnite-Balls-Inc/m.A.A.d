package com.compiler.semantic;

import com.compiler.ast.Program;

/** Resolves declarations and checks a program in source order. */
public final class SemanticAnalyzer {
    public AnalysisResult analyze(Program program) {
        return new AnalysisPass().analyzeProgram(program);
    }
}
