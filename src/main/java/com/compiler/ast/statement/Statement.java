package com.compiler.ast.statement;

import com.compiler.ast.BlockItem;

/** An executable statement from the closed set of language AST nodes. */
public sealed interface Statement extends BlockItem permits Assignment, ForLoop, IfStatement,
        PrintStatement, ReturnStatement, RoutineCallStatement, ScopedBlockStatement, WhileLoop {
}
