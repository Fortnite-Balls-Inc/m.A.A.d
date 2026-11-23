package com.compiler.semantic;

import java.util.Locale;
import java.util.Map;

/** Resolved types live outside the syntax tree. Record identity is nominal. */
public sealed interface SemanticType {

    enum Scalar implements SemanticType {
        INTEGER,
        REAL,
        BOOLEAN,
        NULL,
        VOID,
        /** Carries a previous error through type analysis. */
        ERROR;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    /**
     * A null size denotes a length unknown at compile time. Named arrays carry
     * their defining declaration as identity, and anonymous arrays carry null.
     */
    record Array(SemanticType element, Integer size, Object identity)
            implements SemanticType {

        @Override
        public String toString() {
            return "array[" + (size == null ? "" : size) + "] " + element;
        }
    }

    /** Each record declaration introduces a distinct type identity. */
    final class Record implements SemanticType {
        private final String name;
        private final Map<String, SemanticType> fields;

        Record(Map<String, SemanticType> fields) {
            this("anonymous", fields);
        }

        Record(String name, Map<String, SemanticType> fields) {
            this.name = name;
            this.fields = Map.copyOf(fields);
        }

        public Map<String, SemanticType> fields() {
            return fields;
        }

        @Override
        public String toString() {
            return "record " + name;
        }
    }
}
