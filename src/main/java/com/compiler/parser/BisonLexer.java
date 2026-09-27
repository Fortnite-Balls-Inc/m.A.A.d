package com.compiler.parser;

import com.compiler.lexer.Lexer;
import com.compiler.lexer.Token;
import com.compiler.lexer.TokenConstants;

import java.io.IOException;

/**
 * Adapts {@link Lexer} to {@link Parser.Lexer}.
 * <p> Maps {@link Lexer}'s {@link TokenConstants} values to the {@link Parser}'s values.
 * <p> Implements {@link #yylex}, {@link #getLVal}, {@link #yyerror}.
 */
public final class BisonLexer implements Parser.Lexer {

    private final Lexer lexer;
    private Token currentToken;

    public BisonLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    @Override
    public int yylex() throws IOException {
        currentToken = lexer.nextToken();

        return switch (currentToken.getType()) {
            case TokenConstants.EOF -> YYEOF;

            case TokenConstants.ROUTINE -> ROUTINE;
            case TokenConstants.VAR -> VAR;
            case TokenConstants.TYPE -> TYPE;
            case TokenConstants.IS -> IS;
            case TokenConstants.END -> END;
            case TokenConstants.IF -> IF;
            case TokenConstants.THEN -> THEN;
            case TokenConstants.ELSE -> ELSE;
            case TokenConstants.WHILE -> WHILE;
            case TokenConstants.LOOP -> LOOP;
            case TokenConstants.FOR -> FOR;
            case TokenConstants.IN -> IN;
            case TokenConstants.REVERSE -> REVERSE;
            case TokenConstants.RETURN -> RETURN;
            case TokenConstants.PRINT -> PRINT;
            case TokenConstants.RECORD -> RECORD;
            case TokenConstants.ARRAY -> ARRAY;
            case TokenConstants.INTEGER -> INTEGER;
            case TokenConstants.REAL -> REAL;
            case TokenConstants.BOOLEAN -> BOOLEAN;
            case TokenConstants.TRUE -> TRUE;
            case TokenConstants.FALSE -> FALSE;
            case TokenConstants.NULL -> NULL;
            case TokenConstants.NEW -> NEW;

            case TokenConstants.AND -> AND;
            case TokenConstants.OR -> OR;
            case TokenConstants.XOR -> XOR;
            case TokenConstants.NOT -> NOT;

            case TokenConstants.IDENTIFIER -> IDENTIFIER;
            case TokenConstants.INTEGER_LITERAL -> INTEGER_LITERAL;
            case TokenConstants.REAL_LITERAL -> REAL_LITERAL;

            case TokenConstants.ASSIGN -> ASSIGN;
            case TokenConstants.PLUS -> PLUS;
            case TokenConstants.MINUS -> MINUS;
            case TokenConstants.STAR -> STAR;
            case TokenConstants.SLASH -> SLASH;
            case TokenConstants.PERCENT -> PERCENT;

            case TokenConstants.EQ -> EQ;
            case TokenConstants.NEQ -> NEQ;
            case TokenConstants.LT -> LT;
            case TokenConstants.LE -> LE;
            case TokenConstants.GT -> GT;
            case TokenConstants.GE -> GE;

            case TokenConstants.RETIMM -> RETIMM;

            case TokenConstants.COLON -> COLON;
            case TokenConstants.DOT -> DOT;
            case TokenConstants.DOTDOT -> DOTDOT;
            case TokenConstants.LPAREN -> LPAREN;
            case TokenConstants.RPAREN -> RPAREN;
            case TokenConstants.LBRACKET -> LBRACKET;
            case TokenConstants.RBRACKET -> RBRACKET;
            case TokenConstants.COMMA -> COMMA;
            case TokenConstants.SEMICOLON -> SEMICOLON;
            case TokenConstants.NEWLINE -> NEWLINE;

            default -> throw new IllegalArgumentException(
                    "Unknown lexer token type: " + currentToken.getType()
            );
        };
    }

    @Override
    public Object getLVal() {
        return currentToken;
    }

    @Override
    public void yyerror(String message) {
        if (currentToken == null) {
            System.err.println("Syntax error: " + message);
            return;
        }

        System.err.printf(
                "Syntax error at %d:%d: %s%n",
                currentToken.getLine(),
                currentToken.getColumn(),
                message
        );
    }
}
