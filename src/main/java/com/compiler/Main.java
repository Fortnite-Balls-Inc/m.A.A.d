package com.compiler;

import com.compiler.lexer.Lexer;
import com.compiler.optimization.OptimizationResult;
import com.compiler.optimization.Optimizer;
import com.compiler.parser.BisonLexer;
import com.compiler.parser.Parser;
import com.compiler.semantic.SemanticAnalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: maad <file.maad>");
            System.exit(1);
        }

        var file = args[0];
        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            // 1. Lexer: split the program into tokens
            Lexer lexer = new Lexer(reader);
            BisonLexer bisonLexer = new BisonLexer(lexer);

            // 2. Parser: parse the program, report parsing errors
            Parser parser = new Parser(bisonLexer);
            parser.setErrorVerbose(true);

            boolean success = parser.parse();
            if (!success) {
                System.err.println("Parsing failed!");
                System.err.printf("Number of errors: %d\n", parser.getNumberOfErrors());
                System.exit(1);
            }

            // 3. Analyzer: perform several semantic checks
            var program = parser.getProgram();
            var analysis = new SemanticAnalyzer().analyze(program);
            if (!analysis.isValid()) {
                // Print the diagnostics
                for (var diagnostic : analysis.diagnostics()) {
                    System.err.println(file + ":" + diagnostic);
                }

                System.err.println("Semantic analysis failed!");
                System.err.printf("Number of errors: %d\n", analysis.diagnostics().size());
                System.exit(1);
            }

            // 4. Optimizer: remove unreachable branches, unused variables, etc.
            System.out.println("==== BEFORE OPTIMIZATIONS ====\n");
            program.prettyPrint();
            System.out.println("\n==============================\n\n");

            OptimizationResult result = new Optimizer().optimize(analysis);
            if (result.countChanges() != 0) {
                program = result.program();
                System.out.println("==== AFTER OPTIMIZATIONS ====\n");
                program.prettyPrint();
                System.out.println("\n=============================");
            }
            
            System.out.printf("Number of changes: %d\n", result.countChanges());
        } catch (IOException e) {   // file not found, no access, ...
            e.printStackTrace();
            System.exit(1);
        }
    }
}
