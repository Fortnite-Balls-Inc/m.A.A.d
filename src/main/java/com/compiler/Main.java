package com.compiler;

import com.compiler.lexer.Lexer;
import com.compiler.parser.BisonLexer;
import com.compiler.parser.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
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

            // Print the AST
            parser.getProgram().prettyPrint();
        } catch (IOException e) {   // file not found, no access, ...
            e.printStackTrace();
        }
    }
}
