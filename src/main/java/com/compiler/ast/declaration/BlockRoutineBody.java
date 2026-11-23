package com.compiler.ast.declaration;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.Block;
import com.compiler.ast.SourcePosition;

public record BlockRoutineBody(Block block, SourcePosition position)
        implements RoutineBody {

    @Override
    public void prettyPrint(int depth) {
        AstPrinter.branch(depth, "BlockRoutineBody", position).child(block);
    }
}
