package com.compiler.semantic;

import com.compiler.ast.Block;
import com.compiler.ast.BlockItem;
import com.compiler.ast.statement.ForLoop;
import com.compiler.ast.statement.IfStatement;
import com.compiler.ast.statement.RangeSource;
import com.compiler.ast.statement.ReturnStatement;
import com.compiler.ast.statement.ScopedBlockStatement;
import com.compiler.ast.statement.WhileLoop;

import java.util.Optional;

/** Conservative fall-through analysis. */
public final class ControlFlow {

    private ControlFlow() {
    }

    /** True when control cannot reach the end of the block, including an infinite loop. */
    public static boolean preventsFallThrough(Block block) {
        return block.items().stream().anyMatch(ControlFlow::preventsFallThrough);
    }

    public static boolean preventsFallThrough(BlockItem item) {
        return switch (item) {
            case ReturnStatement _ -> true;
            case ScopedBlockStatement scopedBlock -> preventsFallThrough(scopedBlock.block());
            case IfStatement conditional -> branchesPreventFallThrough(conditional);
            // "while true" prevents fall through since the language lacks "break"
            case WhileLoop loop -> ConstantEvaluator.evaluateBoolean(loop.condition()).orElse(false);
            case ForLoop loop -> rangeLoopPreventsFallThrough(loop);
            default -> false;
        };
    }

    private static boolean branchesPreventFallThrough(IfStatement conditional) {
        Optional<Boolean> condition = ConstantEvaluator.evaluateBoolean(conditional.condition());
        if (condition.isPresent()) {
            return condition.get() ? preventsFallThrough(conditional.thenBlock())
                    : conditional.elseBlock().map(ControlFlow::preventsFallThrough).orElse(false);
        }
        return preventsFallThrough(conditional.thenBlock())
                && conditional.elseBlock().map(ControlFlow::preventsFallThrough).orElse(false);
    }

    /** Prevents fall through if the loop is range-based,
    body is executed at least once, and the body prevents fall through */
    private static boolean rangeLoopPreventsFallThrough(ForLoop loop) {
        if (!(loop.source() instanceof RangeSource range)) {
            return false;
        }
        Optional<Integer> start = ConstantEvaluator.evaluateInteger(range.start());
        Optional<Integer> end = ConstantEvaluator.evaluateInteger(range.end());
        return start.isPresent() && end.isPresent() && start.get() <= end.get()
                && preventsFallThrough(loop.body());
    }
}
