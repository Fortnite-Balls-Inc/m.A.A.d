package com.compiler.lexer;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads characters from a {@link Reader} and produces {@link Token}s one at a time via {@link #nextToken()}.
 * <p>The lexer tracks line and column numbers (both 1-based) so that error messages can point to the exact
 * source location.
 */
public final class Lexer {

    private final PushbackReader reader;

    // Current position
    private int line = 1;
    private int column = 1;

    // Token start position (captured before scanning a token)
    private int tokenLine;
    private int tokenColumn;

    private static final Map<String, Integer> KEYWORDS = new HashMap<>();    // Keyword lookup table

    /** Sentinel returned by {@link #read()} at end of input. {@code '\uFFFF'} is a Unicode non-character. */
    private static final char EOF = '\uFFFF';

    static {
        KEYWORDS.put("routine", TokenConstants.ROUTINE);
        KEYWORDS.put("var",     TokenConstants.VAR);
        KEYWORDS.put("type",    TokenConstants.TYPE);
        KEYWORDS.put("is",      TokenConstants.IS);
        KEYWORDS.put("end",     TokenConstants.END);
        KEYWORDS.put("if",      TokenConstants.IF);
        KEYWORDS.put("then",    TokenConstants.THEN);
        KEYWORDS.put("else",    TokenConstants.ELSE);
        KEYWORDS.put("while",   TokenConstants.WHILE);
        KEYWORDS.put("loop",    TokenConstants.LOOP);
        KEYWORDS.put("for",     TokenConstants.FOR);
        KEYWORDS.put("in",      TokenConstants.IN);
        KEYWORDS.put("reverse", TokenConstants.REVERSE);
        KEYWORDS.put("return",  TokenConstants.RETURN);
        KEYWORDS.put("print",   TokenConstants.PRINT);
        KEYWORDS.put("record",  TokenConstants.RECORD);
        KEYWORDS.put("array",   TokenConstants.ARRAY);
        KEYWORDS.put("integer", TokenConstants.INTEGER);
        KEYWORDS.put("real",    TokenConstants.REAL);
        KEYWORDS.put("boolean", TokenConstants.BOOLEAN);
        KEYWORDS.put("true",    TokenConstants.TRUE);
        KEYWORDS.put("false",   TokenConstants.FALSE);
        KEYWORDS.put("null",    TokenConstants.NULL);
        KEYWORDS.put("and",     TokenConstants.AND);
        KEYWORDS.put("or",      TokenConstants.OR);
        KEYWORDS.put("xor",     TokenConstants.XOR);
        KEYWORDS.put("not",     TokenConstants.NOT);
    }

    public Lexer(Reader reader) {
        this.reader = new PushbackReader(reader, 2);
    }

    // to get rid of each time passing line and column into token's constructor:
    private Token makeToken(int type) {
        return new Token(type, tokenLine, tokenColumn);
    }

    private Token makeToken(int type, Object value) {
        return new Token(type, value, tokenLine, tokenColumn);
    }

    /**
     * Scans and returns the next token from the input.
     * @return the next {@link Token}; never {@code null}
     * @throws IOException if reading from the underlying stream fails
     */
    public Token nextToken() throws IOException {
        skipWhitespaceAndComments();
        // Capture position before reading the first character
        tokenLine = line;
        tokenColumn = column;

        char ch = read();
        if (ch == EOF) return makeToken(TokenConstants.EOF);
        if (Character.isLetter(ch) || ch == '_') return scanIdentifier(ch);
        if (Character.isDigit(ch)) return scanNumber(ch);
        return scanOperator(ch);
    }

    private Token scanOperator(char ch) throws IOException {
        char next;
        switch (ch) {
            case ':':
                next = read();
                if (next == '=') return makeToken(TokenConstants.ASSIGN);
                unread(next);
                return makeToken(TokenConstants.COLON);

            case '<':
                next = read();
                if (next == '=') return makeToken(TokenConstants.LE);
                unread(next);
                return makeToken(TokenConstants.LT);

            case '>':
                next = read();
                if (next == '=') return makeToken(TokenConstants.GE);
                unread(next);
                return makeToken(TokenConstants.GT);

            case '/':
                next = read();
                if (next == '=') return makeToken(TokenConstants.NEQ);
                unread(next);
                return makeToken(TokenConstants.SLASH);

            case '.':
                next = read();
                if (next == '.') return makeToken(TokenConstants.DOTDOT);
                unread(next);
                return makeToken(TokenConstants.DOT);

            case '=':
                next = read();
                if (next == '>') return makeToken(TokenConstants.RETIMM);
                unread(next);
                return makeToken(TokenConstants.EQ);

            case '+': return makeToken(TokenConstants.PLUS);
            case '-': return makeToken(TokenConstants.MINUS);
            case '*': return makeToken(TokenConstants.STAR);
            case '%': return makeToken(TokenConstants.PERCENT);
            case '(': return makeToken(TokenConstants.LPAREN);
            case ')': return makeToken(TokenConstants.RPAREN);
            case '[': return makeToken(TokenConstants.LBRACKET);
            case ']': return makeToken(TokenConstants.RBRACKET);
            case ',': return makeToken(TokenConstants.COMMA);
            case ';': return makeToken(TokenConstants.SEMICOLON);
            default : return makeToken(TokenConstants.UNKNOWN, String.valueOf(ch));
        }
    }

    private Token scanIdentifier(char firstChar) throws IOException {
        StringBuilder sb = new StringBuilder().append(firstChar);

        while (true) {
            char ch = read();
            if (Character.isLetterOrDigit(ch) || ch == '_') {
                sb.append(ch);
            } else {
                unread(ch);
                break;
            }
        }

        String text = sb.toString();
        Integer keywordType = KEYWORDS.get(text);

        if (keywordType != null) {
            return switch (keywordType) {
                case TokenConstants.TRUE -> makeToken(TokenConstants.TRUE, Boolean.TRUE);
                case TokenConstants.FALSE -> makeToken(TokenConstants.FALSE, Boolean.FALSE);
                case TokenConstants.NULL -> makeToken(TokenConstants.NULL, null);
                default -> makeToken(keywordType);
            };
        }

        return makeToken(TokenConstants.IDENTIFIER, text);
    }

    private Token scanNumber(char first) throws IOException {
        StringBuilder sb = new StringBuilder().append(first);

        boolean isReal = false;
        while (true) {
            char ch = read();
            if (Character.isDigit(ch)) {
                sb.append(ch);
            } else if (ch == '.' && !isReal) {
                char after = read();  // a real number must have a digit after the dot
                if (Character.isDigit(after)) {
                    isReal = true;
                    sb.append(ch);
                    sb.append(after);
                } else { // not a decimal point (syntax error or ..)
                    unread(after);
                    unread(ch);
                    break;
                }
            } else {
                unread(ch);
                break;
            }
        }

        String text = sb.toString();
        try {
            if (isReal) {
                return makeToken(TokenConstants.REAL_LITERAL, Double.parseDouble(text));
            } else {
                return makeToken(TokenConstants.INTEGER_LITERAL, Integer.parseInt(text));
            }
        } catch (NumberFormatException e) {
            return makeToken(TokenConstants.UNKNOWN, text);
        }
    }

    private void skipWhitespaceAndComments() throws IOException {
        while (true) {
            char ch = read();
            if (ch == EOF) return;

            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
                continue;
            }

            if (ch == '/') {
                char next = read();
                if (next == '/') {
                    while (true) {
                        ch = read();
                        if (ch == EOF || ch == '\n') break;
                    }
                    continue;
                }
                // it's division, push back both characters
                unread(next);
                unread(ch);
                return;
            }
            unread(ch);
            return;
        }
    }

    /** Reads one character, updating line/column tracking. Returns {@link #EOF} at end of input. */
    private char read() throws IOException {
        int ch = reader.read();
        if (ch == -1) return EOF;

        if (ch == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return (char) ch;
    }

    /** Pushes one character back, undoing line/column tracking. */
    private void unread(char ch) throws IOException {
        if (ch == EOF) return;
        reader.unread(ch);

        if (ch == '\n') {
            line--; // after unread() anyway there will be read() that will set proper column
        } else {
            column--;
        }
    }
}