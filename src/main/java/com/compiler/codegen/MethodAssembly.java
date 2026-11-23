package com.compiler.codegen;

import java.util.IdentityHashMap;
import java.util.Map;

import com.compiler.ast.Node;
import com.compiler.semantic.SemanticType;

import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.REAL;
import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Instructions and local variable slots belonging to one static JVM method. */
final class MethodAssembly {
    private static final int STACK_LIMIT = 1024;

    private final StringBuilder instructions = new StringBuilder();
    private final Map<Node, Integer> localSlots = new IdentityHashMap<>();
    private int nextSlot;

    void emit(String instruction) {
        instructions.append("  ").append(instruction).append('\n');
    }

    void label(String label) {
        instructions.append(label).append(":\n");
    }

    int allocateSlot(SemanticType type) {
        int slot = nextSlot;
        nextSlot += type == REAL ? 2 : 1;
        return slot;
    }

    int declareLocal(Node declaration, SemanticType type) {
        int slot = allocateSlot(type);
        localSlots.put(declaration, slot);
        return slot;
    }

    int localSlot(Node declaration) {
        return localSlots.get(declaration);
    }

    static String instructionPrefix(SemanticType type) {
        if (type == VOID) {
            return "";
        }
        if (type == REAL) {
            return "d";
        }
        return type == INTEGER || type == BOOLEAN ? "i" : "a";
    }

    static String arrayInstruction(SemanticType elementType,
                                   String operation) {
        String prefix = elementType == BOOLEAN
                ? "b" : instructionPrefix(elementType);
        return prefix + "a" + operation;
    }

    void discardResult(SemanticType type) {
        if (type != VOID) {
            emit(type == REAL ? "pop2" : "pop");
        }
    }

    String finish(String name, String descriptor) {
        return "\n.method public static " + name + descriptor
                + "\n.limit stack " + STACK_LIMIT
                + "\n.limit locals " + Math.max(1, nextSlot)
                + "\n" + instructions + ".end method\n";
    }
}
