package com.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import com.compiler.ast.Program;
import com.compiler.lexer.Lexer;
import com.compiler.parser.BisonLexer;
import com.compiler.parser.Parser;

/** Command-line entry point for compiling and running MAAD programs. */
public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: maad <cmd> <source.maad> [<args>]\n\nRead README.md for details.");
            System.exit(1);
        }

        String command = args[0];
        switch (command) {
            // Check for the starting routine specified
            case "run" -> {
                if (args.length < 3) {
                    System.err.println("Usage: maad run <source>.maad <starting-routine> [<arg_1> ... <arg_N>]");
                    System.exit(1);
                }
            }

            // Check for the -o flag and the output file 
            case "compile" -> {
                String err = null;

                switch (args.length) {
                    case 2 -> {}

                    case 4 -> {
                        if (!"-o".equals(args[2])) {
                            err = String.format("Unknown flag %s", args[2]);
                        }
                    }
                
                    default -> {
                        err = String.format("Expected 2 or 4 arguments, got %d", args.length);
                    }
                }

                if (err != null) {
                    System.err.println(err);
                    System.exit(1);
                }
            }

            default -> {
                System.err.printf("Unsupported command %s\n", command);
                System.exit(1);
            }
        }

        String source = args[1];
        if (!source.endsWith(".maad")) {
            System.err.println("Source is not .maad");
            System.exit(1);
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(source))) {
            // Construct AST from the .maad source
            Lexer lexer = new Lexer(reader);
            Parser parser = new Parser(new BisonLexer(lexer));
            parser.setErrorVerbose(true);
            if (!parser.parse()) {
                System.err.println("Parsing failed!");
                System.err.printf("Number of errors: %d\n",
                        parser.getNumberOfErrors());
                System.exit(1);
            }

            // Run the command
            Program program = parser.getProgram();
            Path sourcePath = Path.of(source);
            switch (command) {
                case "run" -> {
                    String entry = args[2];
                    String[] routineArgs = Arrays.copyOfRange(args, 3, args.length);

                    int exitCode = ProgramRunner.run(
                            program, sourcePath, entry, routineArgs);
                    System.exit(exitCode);
                }

                case "compile" -> {
                    Path outputPath = args.length == 4
                            ? Path.of(args[3]) : defaultOutputPath(sourcePath);
                    CompilerOutput.write(program, sourcePath, outputPath);
                }
            }
        } catch (IOException | IllegalArgumentException | InterruptedException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static Path defaultOutputPath(Path source) {
        String fileName = source.getFileName().toString();
        String baseName = fileName.replaceFirst("\\.maad$", "");
        return Path.of(baseName + ".j");
    }
}
