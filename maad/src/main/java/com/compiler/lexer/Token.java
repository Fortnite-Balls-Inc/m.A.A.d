package com.compiler.lexer;

/**
 * A single lexical token.
 */
public final class Token {

    private final int type;       // TokenType constant
    private final Object value;
    private final int line;
    private final int column;

    public Token(int type, Object value, int line, int column) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
    }

    public Token(int type, int line, int column) {
        this(type, null, line, column);
    }

    public int getType()  { 
        return type;
    }

    public Object getValue()  {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Integer getIntValue() {
        return (value instanceof Integer i) ? i : null;
    }

    public Double getRealValue() {
        return (value instanceof Double d) ? d : null;
    }

    public Boolean getBooleanValue() {
        return (value instanceof Boolean b) ? b : null;
    }

    public boolean isEof() {
        return type == TokenConstants.EOF;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TokenConstants.name(type));
        if (value != null) {
            sb.append("('").append(value).append("')");
        }
        sb.append(" at ").append(line).append(':').append(column);
        return sb.toString();
    }
}