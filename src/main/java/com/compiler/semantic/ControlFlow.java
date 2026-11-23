package com.compiler.semantic;

import java.util.Optional;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.statement.ForLoop;
import com.compiler.ast.statement.IfStatement;
import com.compiler.ast.statement.RangeSource;
import com.compiler.ast.statement.ReturnStatement;
import com.compiler.ast.statement.ScopedBlockStatement;
import com.compiler.ast.statement.WhileLoop;

/** Determines when a block always returns or runs forever. */
public final class ControlFlow {

    private ControlFlow() {
    }

    public static boolean preventsFallThrough(Block block) {
        return block.items().stream()
                .anyMatch(ControlFlow::preventsFallThrough);
    }

    public static boolean preventsFallThrough(BlockItem item) {
        return switch (item) {
            case ReturnStatement ignored -> true;
            case ScopedBlockStatement scoped ->
                    preventsFallThrough(scoped.block());
            case IfStatement conditional ->
                    branchesPreventFallThrough(conditional);
            // The language has no break statement to exit an infinite loop.
            case WhileLoop loop -> ConstantEvaluator
                    .evaluateBoolean(loop.condition()).orElse(false);
            case ForLoop loop -> rangeLoopPreventsFallThrough(loop);
            default -> false;
        };
    }

    private static boolean branchesPreventFallThrough(IfStatement conditional) {
        Optional<Boolean> condition =
                ConstantEvaluator.evaluateBoolean(conditional.condition());
        if (condition.orElse(false)) {
            return preventsFallThrough(conditional.thenBlock());
        }
        if (condition.isEmpty()
                && !preventsFallThrough(conditional.thenBlock())) {
            return false;
        }
        return conditional.elseBlock()
                .map(ControlFlow::preventsFallThrough).orElse(false);
    }

    private static boolean rangeLoopPreventsFallThrough(ForLoop loop) {
        if (!(loop.source() instanceof RangeSource range)) {
            return false;
        }
        Optional<Integer> start =
                ConstantEvaluator.evaluateInteger(range.start());
        Optional<Integer> end = ConstantEvaluator.evaluateInteger(range.end());
        return start.isPresent() && end.isPresent() && start.get() <= end.get()
                && preventsFallThrough(loop.body());
    }
}
