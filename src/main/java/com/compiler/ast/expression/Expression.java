package com.compiler.ast.expression;

import com.compiler.ast.Node;

/** An expression from the closed set of language AST nodes. */
public sealed interface Expression extends Node permits BinaryExpression, BooleanLiteral,
        FieldAccess, Identifier, IndexAccess, IntegerLiteral, NullLiteral, RealLiteral,
        RecordConstructor, RoutineCall, UnaryExpression {
}
