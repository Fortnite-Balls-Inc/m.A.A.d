package com.compiler.ast.declaration;

import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;

public record BlockRoutineBody(
    Block block,
    SourcePosition position
) implements RoutineBody {

    @Override
    public void prettyPrint(int h) {
        String indent = "  ".repeat(h);
        System.out.println(indent + "BlockRoutineBody " + position.toString() + ":");
        block.prettyPrint(h + 1);
    }
}
