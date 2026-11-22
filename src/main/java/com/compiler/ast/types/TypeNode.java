package com.compiler.ast.types;

import com.compiler.ast.Node;

/** A primitive, named, array, or record type in the AST. */
public sealed interface TypeNode extends Node permits NamedType, PrimitiveType, UserType {
}
