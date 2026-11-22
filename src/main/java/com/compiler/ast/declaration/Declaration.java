package com.compiler.ast.declaration;

import com.compiler.ast.Node;

/** A top-level declaration or a declaration allowed inside a block. */
public sealed interface Declaration extends Node permits RoutineDeclaration, SimpleDeclaration {
}
