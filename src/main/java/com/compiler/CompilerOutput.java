package com.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.compiler.ast.Program;
import com.compiler.codegen.JasminGenerator;
import com.compiler.optimization.Optimizer;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.Diagnostic;
import com.compiler.semantic.SemanticAnalyzer;

/** Compiles a parsed program and writes its Jasmin classes to disk. */
final class CompilerOutput {
    private CompilerOutput() {
    }

    static void write(Program program, Path source, Path output)
            throws IOException {
        String className = outputClassName(output);
        AnalysisResult analysis = analyzeAndOptimize(program, source);
        Map<String, String> classes =
                new JasminGenerator().generateClasses(analysis, className);
        for (Map.Entry<String, String> entry : classes.entrySet()) {
            String generatedName = entry.getKey();
            Path file = generatedName.equals(className)
                    ? output : output.resolveSibling(generatedName + ".j");
            Files.writeString(file, entry.getValue());
        }
    }

    private static String outputClassName(Path output) {
        String fileName = output.getFileName().toString();
        String className = fileName.replaceFirst("\\.j$", "");
        if (!className.matches("[A-Za-z_][A-Za-z_0-9]*")) {
            throw new IllegalArgumentException(
                    "Output class name must be a valid identifier: "
                            + className);
        }
        return className;
    }

    private static AnalysisResult analyzeAndOptimize(Program program,
            Path source) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        AnalysisResult original = analyzer.analyze(program);
        if (!original.isValid()) {
            for (Diagnostic diagnostic : original.diagnostics()) {
                System.err.println(source + ":" + diagnostic);
            }
            throw new IllegalArgumentException("Semantic analysis failed: "
                    + original.diagnostics().size() + " error(s)");
        }

        Program optimized = new Optimizer().optimize(original).program();
        // Type and binding maps use node identity. Rebuild them for this AST.
        AnalysisResult result = analyzer.analyze(optimized);
        if (!result.isValid()) {
            throw new IllegalStateException("Optimizer produced invalid AST: "
                    + result.diagnostics());
        }
        return result;
    }
}
