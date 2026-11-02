package com.compiler.optimization;

import com.compiler.ast.Program;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record OptimizationResult(Program program, Map<String, Integer> changes) {

    public OptimizationResult {
        changes = Collections.unmodifiableMap(new LinkedHashMap<>(changes));
    }

    public int countChanges() {
        return changes.values().stream().mapToInt(Integer::intValue).sum();
    }
}
