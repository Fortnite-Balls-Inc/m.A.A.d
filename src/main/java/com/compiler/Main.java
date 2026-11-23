package com.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import com.compiler.ast.Program;
import com.compiler.lexer.Lexer;
import com.compiler.parser.BisonLexer;
import com.compiler.parser.Parser;

/** Command-line entry point for compiling a source file or printing its AST. */
public class Main {
    public static void main(String[] args) {
        if (args.length < 1 || args.length > 2) {
            System.err.println("Usage: maad <source.maad> [output.j | --ast]");
            System.exit(1);
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(args[0]))) {
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(new BisonLexer(lexer));
            parser.setErrorVerbose(true);
            if (!parser.parse()) {
                System.out.println("Parsing failed!");
                System.out.printf("Number of errors: %d\n",
                        parser.getNumberOfErrors());
                System.exit(1);
            }

            Program program = parser.getProgram();
            if (args.length == 2 && "--ast".equals(args[1])) {
                program.prettyPrint();
                return;
            }
            Path source = Path.of(args[0]);
            Path output = args.length == 2
                    ? Path.of(args[1]) : defaultOutputPath(source);
            CompilerOutput.write(program, source, output);
        } catch (IOException | IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static Path defaultOutputPath(Path source) {
        String fileName = source.getFileName().toString();
        String baseName = fileName.replaceFirst("\\.[^.]+$", "");
        return source.resolveSibling(baseName + ".j");
    }
}
