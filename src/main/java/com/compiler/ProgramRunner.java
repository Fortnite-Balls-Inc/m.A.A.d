package com.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.compiler.ast.Program;

/** Compiles and executes a MAAD program. */
final class ProgramRunner {
    static int run(Program program, Path source, String entry,
                   String[] args)
            throws IOException, InterruptedException {

        Path directory = Files.createTempDirectory("maad-");
        Path output = directory.resolve("Program.j");

        try {
            // Generate Jasmin assembly
            CompilerOutput.write(program, source, output, entry);

            // Assemble all generated classes
            List<String> assemble = new ArrayList<>(List.of(
                    javaExecutable(),
                    "-cp", System.getProperty("java.class.path"),
                    "jasmin.Main",
                    "-d", directory.toString()));

            try (var files = Files.list(directory)) {
                files.filter(file -> file.toString().endsWith(".j"))
                        .forEach(file -> assemble.add(file.toString()));
            }

            int exitCode = execute(assemble);
            if (exitCode != 0) {
                return exitCode;
            }

            // Execute the compiled program
            List<String> command = new ArrayList<>(List.of(
                    javaExecutable(),
                    "-cp", directory.toString(),
                    "Program"));

            command.addAll(Arrays.asList(args));
            return execute(command);

        } finally {
            // Remove generated files
            try (var files = Files.walk(directory)) {
                for (Path file : files.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private static int execute(List<String> command)
            throws IOException, InterruptedException {
        return new ProcessBuilder(command)
                .inheritIO()
                .start()
                .waitFor();
    }

    private static String javaExecutable() {
        Path bin = Path.of(System.getProperty("java.home"), "bin");
        Path executable = bin.resolve("java");

        if (!Files.exists(executable)) {
            executable = bin.resolve("java.exe");
        }

        return executable.toString();
    }
}
