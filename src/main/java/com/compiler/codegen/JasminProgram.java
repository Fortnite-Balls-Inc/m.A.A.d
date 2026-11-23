package com.compiler.codegen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.compiler.ast.Node;
import com.compiler.ast.declaration.Declaration;
import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.ast.expression.Expression;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.types.ArrayType;
import com.compiler.ast.types.RecordType;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.SemanticType;

import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Class names, JVM descriptors and semantic bindings for one compilation. */
final class JasminProgram {
    private final AnalysisResult analysis;
    private final String className;
    private final List<VariableDeclaration> globals = new ArrayList<>();
    private final Set<VariableDeclaration> globalBindings =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<String, RoutineDeclaration> routines =
            new LinkedHashMap<>();

    private final Map<SemanticType, ArrayType> arraySyntax =
            new IdentityHashMap<>();
    private final Map<SemanticType.Record, RecordType> recordSyntax =
            new IdentityHashMap<>();
    private final Map<VariableDeclaration, SemanticType.Record> fieldOwners =
            new IdentityHashMap<>();
    private final Map<SemanticType.Record, String> recordNames =
            new IdentityHashMap<>();
    private final Map<String, String> classes = new LinkedHashMap<>();
    private int nextLabel;

    JasminProgram(AnalysisResult analysis, String className) {
        this.analysis = analysis;
        this.className = className;

        // Array sizes and record field defaults are expressions in the AST.
        for (var entry : analysis.resolvedTypes().entrySet()) {
            if (entry.getKey() instanceof ArrayType array) {
                arraySyntax.put(entry.getValue(), array);
            } else if (entry.getKey() instanceof RecordType record) {
                SemanticType.Record type =
                        (SemanticType.Record) entry.getValue();
                recordSyntax.put(type, record);
                for (VariableDeclaration field : record.fields()) {
                    fieldOwners.put(field, type);
                }
            }
        }

        for (Declaration declaration : analysis.program().declarations()) {
            if (declaration instanceof VariableDeclaration variable) {
                globals.add(variable);
                globalBindings.add(variable);
            } else if (declaration instanceof RoutineDeclaration routine
                    && routine.body().isPresent()) {
                routines.put(routine.name(), routine);
            }
        }
    }

    List<VariableDeclaration> globals() {
        return globals;
    }

    Collection<RoutineDeclaration> routines() {
        return routines.values();
    }

    RoutineDeclaration routine(String name) {
        return routines.get(name);
    }

    RoutineDeclaration entryRoutine() {
        return routines.getOrDefault("main", routines.get("Main"));
    }

    String newLabel() {
        return "L" + nextLabel++;
    }

    Map<String, String> finish(String assembly) {
        classes.put(className, assembly);
        return classes;
    }

    static String classHeader(String name) {
        return ".class public " + name + "\n.super java/lang/Object\n";
    }

    SemanticType expressionType(Expression expression) {
        return analysis.expressionTypes().get(expression);
    }

    SemanticType variableType(VariableDeclaration variable) {
        return analysis.variableTypes().get(variable);
    }

    SemanticType parameterType(Parameter parameter) {
        return analysis.resolvedTypes().get(parameter.type());
    }

    SemanticType returnType(RoutineDeclaration routine) {
        return routine.returnType().map(analysis.resolvedTypes()::get)
                .orElse(VOID);
    }

    Node declarationOf(Identifier identifier) {
        return analysis.variableReferences().get(identifier);
    }

    boolean isGlobal(Node declaration) {
        return globalBindings.contains(declaration);
    }

    SemanticType.Record fieldOwner(VariableDeclaration field) {
        return fieldOwners.get(field);
    }

    List<VariableDeclaration> fields(SemanticType.Record record) {
        return recordSyntax.get(record).fields();
    }

    ArrayType arraySyntax(SemanticType.Array array) {
        return arraySyntax.get(array);
    }

    String fieldReference(String owner, String name, SemanticType type) {
        return owner + "/" + name + " " + descriptor(type);
    }

    String globalReference(String name, SemanticType type) {
        return fieldReference(className, name, type);
    }

    String routineReference(RoutineDeclaration routine) {
        return className + "/" + routine.name() + descriptor(routine);
    }

    String descriptor(RoutineDeclaration routine) {
        StringBuilder result = new StringBuilder("(");
        for (Parameter parameter : routine.parameters()) {
            result.append(descriptor(parameterType(parameter)));
        }
        return result.append(')').append(descriptor(returnType(routine)))
                .toString();
    }

    String descriptor(SemanticType type) {
        return switch (type) {
            case SemanticType.Scalar scalar -> switch (scalar) {
                case INTEGER -> "I";
                case REAL -> "D";
                case BOOLEAN -> "Z";
                case VOID -> "V";
                case NULL -> "Ljava/lang/Object;";
                case ERROR -> throw new IllegalStateException(
                        "Unvalidated semantic type");
            };
            case SemanticType.Array array -> "[" + descriptor(array.element());
            case SemanticType.Record record -> "L" + recordName(record) + ";";
        };
    }

    /** Reserves the class name before resolving its field descriptors. */
    String recordName(SemanticType.Record record) {
        String existing = recordNames.get(record);
        if (existing != null) {
            return existing;
        }
        String name = className + "$Record" + recordNames.size();
        recordNames.put(record, name);
        StringBuilder assembly = new StringBuilder(classHeader(name));
        for (VariableDeclaration field : fields(record)) {
            SemanticType type = record.fields().get(field.name());
            assembly.append(".field public \"").append(field.name())
                    .append("\" ").append(descriptor(type)).append('\n');
        }
        assembly.append("\n.method public <init>()V\n")
                .append(".limit stack 1\n.limit locals 1\n")
                .append("  aload_0\n")
                .append("  invokespecial java/lang/Object/<init>()V\n")
                .append("  return\n.end method\n");
        classes.put(name, assembly.toString());
        return name;
    }
}
