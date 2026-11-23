package com.compiler.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/** Scans source tokens and records their one-based line and column. */
public final class Lexer {
    private static final char EOF = '\uFFFF';

    // A number followed by ".." can require pushing back both dots.
    private final PushbackReader reader;
    private int line = 1;
    private int column = 1;
    private int previousLineColumn = 1;
    private int tokenLine;
    private int tokenColumn;

    public Lexer(Reader reader) {
        this.reader = new PushbackReader(reader, 2);
    }

    /** Returns the next token, including newline and end-of-input tokens. */
    public Token nextToken() throws IOException {
        skipWhitespaceAndComments();
        tokenLine = line;
        tokenColumn = column;

        char first = read();
        if (first == EOF) {
            return token(TokenConstants.EOF);
        }
        if (Character.isLetter(first) || first == '_') {
            return scanWord(first);
        }
        if (Character.isDigit(first)) {
            return scanNumber(first);
        }
        return scanOperator(first);
    }

    private Token scanWord(char first) throws IOException {
        StringBuilder word = new StringBuilder().append(first);
        char next = read();
        while (Character.isLetterOrDigit(next) || next == '_') {
            word.append(next);
            next = read();
        }
        unread(next);

        String spelling = word.toString();
        int type = TokenConstants.wordType(spelling);
        return switch (type) {
            case TokenConstants.IDENTIFIER -> token(type, spelling);
            case TokenConstants.TRUE -> token(type, true);
            case TokenConstants.FALSE -> token(type, false);
            default -> token(type);
        };
    }

    private Token scanNumber(char first) throws IOException {
        StringBuilder digits = new StringBuilder().append(first);
        char next = readDigits(digits);
        boolean real = false;
        if (next == '.') {
            char fraction = read();
            if (Character.isDigit(fraction)) {
                digits.append('.').append(fraction);
                next = readDigits(digits);
                real = true;
            } else {
                // Leave a field selector or range operator for the next token.
                unread(fraction);
            }
        }
        unread(next);

        String spelling = digits.toString();
        try {
            if (real) {
                return token(TokenConstants.REAL_LITERAL,
                        Double.parseDouble(spelling));
            }
            return token(TokenConstants.INTEGER_LITERAL,
                    Integer.parseInt(spelling));
        } catch (NumberFormatException exception) {
            return token(TokenConstants.UNKNOWN, spelling);
        }
    }

    /** Appends consecutive digits and returns the character after them. */
    private char readDigits(StringBuilder digits) throws IOException {
        char next = read();
        while (Character.isDigit(next)) {
            digits.append(next);
            next = read();
        }
        return next;
    }

    private Token scanOperator(char first) throws IOException {
        int type = switch (first) {
            case ':' -> match('=')
                    ? TokenConstants.ASSIGN : TokenConstants.COLON;
            case '<' -> match('=') ? TokenConstants.LE : TokenConstants.LT;
            case '>' -> match('=') ? TokenConstants.GE : TokenConstants.GT;
            case '/' -> match('=') ? TokenConstants.NEQ : TokenConstants.SLASH;
            case '.' -> match('.') ? TokenConstants.DOTDOT : TokenConstants.DOT;
            case '=' -> match('>') ? TokenConstants.RETIMM : TokenConstants.EQ;
            case '+' -> TokenConstants.PLUS;
            case '-' -> TokenConstants.MINUS;
            case '*' -> TokenConstants.STAR;
            case '%' -> TokenConstants.PERCENT;
            case '(' -> TokenConstants.LPAREN;
            case ')' -> TokenConstants.RPAREN;
            case '[' -> TokenConstants.LBRACKET;
            case ']' -> TokenConstants.RBRACKET;
            case ',' -> TokenConstants.COMMA;
            case ';' -> TokenConstants.SEMICOLON;
            case '\n' -> TokenConstants.NEWLINE;
            default -> TokenConstants.UNKNOWN;
        };
        if (type == TokenConstants.UNKNOWN) {
            return token(type, String.valueOf(first));
        }
        return token(type);
    }

    /** Consumes the next character only when it matches the expected one. */
    private boolean match(char expected) throws IOException {
        char next = read();
        if (next == expected) {
            return true;
        }
        unread(next);
        return false;
    }

    private void skipWhitespaceAndComments() throws IOException {
        while (true) {
            char next = read();
            if (next == ' ' || next == '\t' || next == '\r') {
                continue;
            }
            if (next == '/' && match('/')) {
                // A newline after a comment still separates statements.
                do {
                    next = read();
                } while (next != '\n' && next != EOF);
                unread(next);
                continue;
            }
            unread(next);
            return;
        }
    }

    private Token token(int type) {
        return new Token(type, tokenLine, tokenColumn);
    }

    private Token token(int type, Object value) {
        return new Token(type, value, tokenLine, tokenColumn);
    }

    private char read() throws IOException {
        int next = reader.read();
        if (next == -1) {
            return EOF;
        }
        if (next == '\n') {
            previousLineColumn = column;
            line++;
            column = 1;
        } else {
            column++;
        }
        return (char) next;
    }

    private void unread(char character) throws IOException {
        if (character == EOF) {
            return;
        }
        reader.unread(character);
        if (character == '\n') {
            line--;
            column = previousLineColumn;
        } else {
            column--;
        }
    }
}
