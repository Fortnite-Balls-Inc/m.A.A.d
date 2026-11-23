package com.compiler.optimization;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.compiler.ast.Program;

/** The rewritten program and change counts in the order first encountered. */
public record OptimizationResult(Program program,
        Map<String, Integer> changes) {

    public OptimizationResult {
        changes = Collections.unmodifiableMap(new LinkedHashMap<>(changes));
    }

    public int countChanges() {
        return changes.values().stream().mapToInt(Integer::intValue).sum();
    }
}
