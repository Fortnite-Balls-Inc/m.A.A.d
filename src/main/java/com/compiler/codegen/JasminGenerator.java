package com.compiler.codegen;

import java.util.Map;

import com.compiler.ast.declaration.Parameter;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.VariableDeclaration;
import com.compiler.semantic.AnalysisResult;
import com.compiler.semantic.SemanticType;

/** Emits a program and its record classes from a validated syntax tree. */
public final class JasminGenerator {
    /**
     * Returns Jasmin assembly keyed by class name.
     *
     * @param analysis semantic analysis of the program to emit
     * @param className JVM name for the program class
     * @param entryName name of the starting routine, or null
     * @return assembly for every generated class
     */
    public Map<String, String> generateClasses(AnalysisResult analysis,
                                               String className,
                                               String entryName) {
        if (!analysis.isValid()) {
            throw new IllegalArgumentException(
                    "Cannot generate code with semantic errors");
        }

        JasminProgram program = new JasminProgram(analysis, className);

        RoutineDeclaration entry = entryName == null
                ? program.entryRoutine()
                : program.routine(entryName);

        if (entryName != null && entry == null) {
            throw new IllegalArgumentException(
                    "Unknown routine: " + entryName);
        }

        StringBuilder assembly = new StringBuilder(
                JasminProgram.classHeader(className));

        for (VariableDeclaration variable : program.globals()) {
            SemanticType type = program.variableType(variable);
            assembly.append(".field public static \"").append(variable.name())
                    .append("\" ").append(program.descriptor(type))
                    .append('\n');
        }

        assembly.append(classInitializer(program));

        for (RoutineDeclaration routine : program.routines()) {
            RoutineGenerator generator = new RoutineGenerator(program, routine);
            assembly.append(generator.generate());
        }

        if (entry != null) {
            assembly.append(entryPoint(program, entry));
        }

        return program.finish(assembly.toString());
    }

    private String classInitializer(JasminProgram program) {
        MethodAssembly method = new MethodAssembly();
        ExpressionGenerator expressions =
                new ExpressionGenerator(program, method);

        for (VariableDeclaration global : program.globals()) {
            expressions.emitInitializer(global);
            String field = program.globalReference(global.name(),
                    program.variableType(global));
            method.emit("putstatic " + field);
        }

        method.emit("return");
        return method.finish("<clinit>", "()V");
    }

    private String entryPoint(JasminProgram program, RoutineDeclaration entry) {
        MethodAssembly method = new MethodAssembly();

        for (int index = 0; index < entry.parameters().size(); index++) {
            Parameter parameter = entry.parameters().get(index);
            String parser = argumentParser(program.parameterType(parameter));

            method.emit("aload_0");
            method.emit("ldc " + index);
            method.emit("aaload");
            method.emit("invokestatic " + parser);
        }

        method.emit("invokestatic " + program.routineReference(entry));
        method.discardResult(program.returnType(entry));
        method.emit("return");

        return method.finish("main", "([Ljava/lang/String;)V");
    }

    private String argumentParser(SemanticType type) {
        return switch (type) {
            case SemanticType.Scalar.REAL ->
                "java/lang/Double/parseDouble(Ljava/lang/String;)D";
            case SemanticType.Scalar.BOOLEAN ->
                "java/lang/Boolean/parseBoolean(Ljava/lang/String;)Z";
            case SemanticType.Scalar.INTEGER ->
                "java/lang/Integer/parseInt(Ljava/lang/String;)I";
            default -> throw new IllegalArgumentException(
                    "Entry parameters must be primitive");
        };
    }
}
