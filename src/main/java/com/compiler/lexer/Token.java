package com.compiler.lexer;

/**
 * A token's type code, value and one-based position. Typed value accessors
 * return null when the token carries a different kind of value.
 */
public final class Token {
    private final int type;
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

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Integer getIntValue() {
        return value instanceof Integer number ? number : null;
    }

    public Double getRealValue() {
        return value instanceof Double number ? number : null;
    }

    public Boolean getBooleanValue() {
        return value instanceof Boolean flag ? flag : null;
    }

    public String getStringValue() {
        return value instanceof String text ? text : null;
    }

    public boolean isEof() {
        return type == TokenConstants.EOF;
    }

    @Override
    public String toString() {
        String name = TokenConstants.name(type);
        String literal = value == null ? "" : "('" + value + "')";
        return name + literal + " at " + line + ':' + column;
    }
}
