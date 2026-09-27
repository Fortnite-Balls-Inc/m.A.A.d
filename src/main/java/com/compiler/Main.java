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

            boolean success = parser.parse();

            System.out.println(
                    success
                            ? "Parsing successful"
                            : "Parsing failed"
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
