package com.compiler;

import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader r = new BufferedReader(new FileReader(args[0]))) {
            Lexer l = new Lexer(r);
            Token t = l.nextToken();
            while (!t.isEof()) {
                System.out.println(t);
                t = l.nextToken();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
