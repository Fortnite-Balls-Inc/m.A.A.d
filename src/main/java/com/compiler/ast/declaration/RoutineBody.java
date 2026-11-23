package com.compiler.ast.declaration;

import com.compiler.ast.Node;

public sealed interface RoutineBody extends Node
        permits BlockRoutineBody, ExpressionRoutineBody {
}
