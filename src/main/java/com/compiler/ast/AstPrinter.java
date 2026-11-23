package com.compiler.ast;

import java.util.List;
import java.util.Optional;

/**
 * Prints the indented tree used by the compiler's --ast option. Each node
 * supplies its description and visits its children in source order.
 */
public final class AstPrinter {
    private static final String INDENT = "  ";

    private final int childDepth;

    private AstPrinter(int childDepth) {
        this.childDepth = childDepth;
    }

    /** Prints a node heading and returns a printer for its children. */
    public static AstPrinter branch(int depth, String description,
            SourcePosition position) {
        String indent = INDENT.repeat(depth);
        System.out.println(indent + description + " "
                + position.toString() + ":");
        return new AstPrinter(depth + 1);
    }

    public static void leaf(int depth, String description,
            SourcePosition position) {
        String indent = INDENT.repeat(depth);
        System.out.println(indent + "[" + description + " "
                + position.toString() + "]");
    }

    public void child(Node node) {
        node.prettyPrint(childDepth);
    }

    public void child(String label, Node node) {
        printLabel(label + ":");
        node.prettyPrint(childDepth + 1);
    }

    public void optionalChild(String label, Optional<? extends Node> node) {
        if (node.isPresent()) {
            child(label, node.get());
        } else {
            printLabel(label + ": none");
        }
    }

    public void children(List<? extends Node> nodes) {
        if (nodes.isEmpty()) {
            printLabel("[]");
        } else {
            printNodes(nodes, childDepth);
        }
    }

    public void children(String label, List<? extends Node> nodes) {
        if (nodes.isEmpty()) {
            printLabel(label + ": []");
        } else {
            printLabel(label + ":");
            printNodes(nodes, childDepth + 1);
        }
    }

    private void printLabel(String text) {
        System.out.println(INDENT.repeat(childDepth) + text);
    }

    private static void printNodes(List<? extends Node> nodes, int depth) {
        for (Node node : nodes) {
            node.prettyPrint(depth);
        }
    }
}
