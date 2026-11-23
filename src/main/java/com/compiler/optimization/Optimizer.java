package com.compiler.optimization;

import com.compiler.ast.Program;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.SemanticAnalyzer;

/** Optimizes an immutable syntax tree after semantic analysis succeeds. */
public final class Optimizer {
    /** Analyzes the source program before rewriting it. */
    public OptimizationResult optimize(Program program) {
        return optimize(new SemanticAnalyzer().analyze(program));
    }

    /** Reuses the types and bindings collected for this input tree. */
    public OptimizationResult optimize(AnalysisResult analysis) {
        if (!analysis.isValid()) {
            throw new IllegalArgumentException(
                    "Cannot optimize a program with semantic errors");
        }
        return new OptimizationPass(analysis).optimizeProgram();
    }
}
