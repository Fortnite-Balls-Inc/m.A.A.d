package com.compiler.ast.declaration;

import com.compiler.ast.BlockItem;

public sealed interface SimpleDeclaration extends Declaration, BlockItem
        permits TypeDeclaration, VariableDeclaration {
}
