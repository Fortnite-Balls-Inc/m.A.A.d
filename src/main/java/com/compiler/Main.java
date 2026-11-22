package com.compiler;

import com.compiler.lexer.Lexer;
import com.compiler.parser.BisonLexer;
import com.compiler.parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/** Parses a source file and writes the selected compiler output. */
public class Main {
    /** Runs the compiler with a source path and an optional output path or --ast. */
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: maad <source.maad> [output.j | --ast]");
            System.exit(1);
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(args[0]))) {

            Lexer lexer = new Lexer(reader);
            BisonLexer bisonLexer = new BisonLexer(lexer);
            Parser parser = new Parser(bisonLexer);
            parser.setErrorVerbose(true);

            boolean success = parser.parse();
            if (!success) {
                System.out.println("Parsing failed!");
                System.out.printf("Number of errors: %d\n", parser.getNumberOfErrors());
                System.exit(1);
            }

            CompilerOutput.write(parser.getProgram(), args);
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
