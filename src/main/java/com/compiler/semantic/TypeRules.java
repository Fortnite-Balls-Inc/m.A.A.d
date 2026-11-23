package com.compiler.semantic;

import java.util.List;
import java.util.Objects;

import static com.compiler.semantic.SemanticType.Scalar.BOOLEAN;
import static com.compiler.semantic.SemanticType.Scalar.ERROR;
import static com.compiler.semantic.SemanticType.Scalar.INTEGER;
import static com.compiler.semantic.SemanticType.Scalar.NULL;
import static com.compiler.semantic.SemanticType.Scalar.REAL;
import static com.compiler.semantic.SemanticType.Scalar.VOID;

/** Type identity, assignment conversions and arithmetic promotion. */
final class TypeRules {
    private TypeRules() {
    }

    static boolean isNumeric(SemanticType type) {
        return type == INTEGER || type == REAL || type == BOOLEAN;
    }

    static SemanticType promote(SemanticType left, SemanticType right) {
        if (left == ERROR || right == ERROR) {
            return ERROR;
        }
        return left == REAL || right == REAL ? REAL : INTEGER;
    }

    static boolean canAssign(SemanticType target, SemanticType source) {
        if (target == ERROR || source == ERROR) {
            return true;
        }
        if (target == source) {
            return target != VOID;
        }
        if (isNumeric(target) && isNumeric(source)) {
            return !(target == BOOLEAN && source == REAL);
        }
        if (target instanceof SemanticType.Record && source == NULL) {
            return true;
        }
        if (target instanceof SemanticType.Array to
                && source instanceof SemanticType.Array from) {
            // Reference assignment preserves the array's element type.
            return sameType(to.element(), from.element())
                    && to.identity() == from.identity()
                    && (to.size() == null || to.size().equals(from.size()));
        }
        return false;
    }

    static boolean sameType(SemanticType first, SemanticType second) {
        if (first == second || first == ERROR || second == ERROR) {
            return true;
        }
        if (first instanceof SemanticType.Array left
                && second instanceof SemanticType.Array right) {
            return left.identity() == right.identity()
                    && Objects.equals(left.size(), right.size())
                    && sameType(left.element(), right.element());
        }
        return false;
    }

    static boolean sameParameters(List<SemanticType> expected,
                                  List<SemanticType> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }
        for (int index = 0; index < expected.size(); index++) {
            if (!sameType(expected.get(index), actual.get(index))) {
                return false;
            }
        }
        return true;
    }
}
