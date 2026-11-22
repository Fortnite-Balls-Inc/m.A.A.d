package com.compiler;

import com.compiler.ast.Program;
import com.compiler.codegen.JasminGenerator;
import com.compiler.optimization.Optimizer;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.SemanticAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Runs semantic analysis and optimization before writing compiler output. */
final class CompilerOutput {
    private CompilerOutput() {
    }

    /** Writes all generated classes beside the requested output file. */
    static void write(Program program, String[] arguments) throws IOException {
        if (arguments.length == 2 && arguments[1].equals("--ast")) {
            program.prettyPrint();
            return;
        }

        Path source = Path.of(arguments[0]);
        String sourceName = source.getFileName().toString().replaceFirst("\\.[^.]+$", "");
        Path output = arguments.length == 2
                ? Path.of(arguments[1]) : source.resolveSibling(sourceName + ".j");
        String className = output.getFileName().toString().replaceFirst("\\.j$", "");

        if (!className.matches("[A-Za-z_][A-Za-z_0-9]*")) {
            throw new IllegalArgumentException(
                    "Output class name must be a valid identifier: " + className);
        }

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        AnalysisResult analysis = analyzer.analyze(program);
        if (!analysis.isValid()) {
            for (var diagnostic : analysis.diagnostics()) {
                System.err.println(source + ":" + diagnostic);
            }
            throw new IllegalArgumentException("Semantic analysis failed: "
                    + analysis.diagnostics().size() + " error(s)");
        }

        var optimized = new Optimizer().optimize(analysis);
        // Optimizer rewrites AST nodes; identity maps must describe the new tree.
        AnalysisResult optimizedAnalysis = analyzer.analyze(optimized.program());
        if (!optimizedAnalysis.isValid()) {
            throw new IllegalStateException("Optimizer produced invalid AST: "
                    + optimizedAnalysis.diagnostics());
        }

        Map<String, String> classes = new JasminGenerator()
                .generateClasses(optimizedAnalysis, className);
        for (Map.Entry<String, String> generatedClass : classes.entrySet()) {
            Path file = generatedClass.getKey().equals(className)
                    ? output : output.resolveSibling(generatedClass.getKey() + ".j");
            Files.writeString(file, generatedClass.getValue());
        }
    }
}
