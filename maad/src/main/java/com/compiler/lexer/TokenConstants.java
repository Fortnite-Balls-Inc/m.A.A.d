package com.compiler.lexer;

/**
 * Token type constants. 
 * Class is used instead of enum for compatibility with Bison parser
 */
public final class TokenConstants {

    private TokenConstants() {}

    // End of input
    public static final int EOF = 0;

    // Keywords
    public static final int ROUTINE = 1;
    public static final int VAR     = 2;
    public static final int TYPE    = 3;
    public static final int IS      = 4;
    public static final int END     = 5;
    public static final int IF      = 6;
    public static final int THEN    = 7;
    public static final int ELSE    = 8;
    public static final int WHILE   = 9;
    public static final int LOOP    = 10;
    public static final int FOR     = 11;
    public static final int IN      = 12;
    public static final int REVERSE = 13;
    public static final int RETURN  = 14;
    public static final int PRINT   = 15;
    public static final int RECORD  = 16;
    public static final int ARRAY   = 17;
    public static final int INTEGER = 18;
    public static final int REAL    = 19;
    public static final int BOOLEAN = 20;
    public static final int TRUE    = 21;
    public static final int FALSE   = 22;
    public static final int NULL    = 23;

    // Identifiers and literals
    public static final int IDENTIFIER      = 24;
    public static final int INTEGER_LITERAL = 25;
    public static final int REAL_LITERAL    = 26;

    // Operators and punctuation
    public static final int ASSIGN    = 27;  // :=
    public static final int COLON     = 28;  // :
    public static final int EQ        = 29;  // =
    public static final int NEQ       = 30;  // /=
    public static final int LT        = 31;  // <
    public static final int LE        = 32;  // <=
    public static final int GT        = 33;  // >
    public static final int GE        = 34;  // >=
    public static final int PLUS      = 35;  // +
    public static final int MINUS     = 36;  // -
    public static final int STAR      = 37;  // *
    public static final int SLASH     = 38;  // /
    public static final int PERCENT   = 39;  // %
    public static final int LPAREN    = 40;  // (
    public static final int RPAREN    = 41;  // )
    public static final int LBRACKET  = 42;  // [
    public static final int RBRACKET  = 43;  // ]
    public static final int DOT       = 44;  // .
    public static final int DOTDOT    = 45;  // ..
    public static final int COMMA     = 46;  // ,
    public static final int SEMICOLON = 47;  // ;

    // Logical operators
    public static final int AND = 48;
    public static final int OR  = 49;
    public static final int XOR = 50;
    public static final int NOT = 51;

    // Error
    public static final int UNKNOWN = 52;

    /** Human-readable name for error messages and debugging. */
    public static String name(int tokenType) {
        return switch (tokenType) {
            case EOF -> "EOF";
            case ROUTINE -> "routine";
            case VAR -> "var";
            case TYPE -> "type";
            case IS -> "is";
            case END -> "end";
            case IF -> "if";
            case THEN -> "then";
            case ELSE -> "else";
            case WHILE -> "while";
            case LOOP -> "loop";
            case FOR -> "for";
            case IN -> "in";
            case REVERSE -> "reverse";
            case RETURN -> "return";
            case PRINT -> "print";
            case RECORD -> "record";
            case ARRAY -> "array";
            case INTEGER -> "integer";
            case REAL -> "real";
            case BOOLEAN -> "boolean";
            case TRUE -> "true";
            case FALSE -> "false";
            case NULL -> "null";
            case IDENTIFIER -> "identifier";
            case INTEGER_LITERAL -> "integer literal";
            case REAL_LITERAL -> "real literal";
            case ASSIGN -> ":=";
            case COLON -> ":";
            case EQ -> "=";
            case NEQ -> "/=";
            case LT -> "<";
            case LE -> "<=";
            case GT -> ">";
            case GE -> ">=";
            case PLUS -> "+";
            case MINUS -> "-";
            case STAR -> "*";
            case SLASH -> "/";
            case PERCENT -> "%";
            case LPAREN -> "(";
            case RPAREN -> ")";
            case LBRACKET -> "[";
            case RBRACKET -> "]";
            case DOT -> ".";
            case DOTDOT -> "..";
            case COMMA -> ",";
            case SEMICOLON -> ";";
            case AND -> "and";
            case OR -> "or";
            case XOR -> "xor";
            case NOT -> "not";
            default -> "unknown(" + tokenType + ")";
        };
    }
}