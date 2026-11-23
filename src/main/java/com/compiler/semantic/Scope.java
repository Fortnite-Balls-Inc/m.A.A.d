package com.compiler.semantic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.compiler.ast.Node;
import com.compiler.ast.declaration.RoutineDeclaration;
import com.compiler.ast.declaration.TypeDeclaration;
import com.compiler.ast.expression.Identifier;
import com.compiler.ast.expression.RoutineCall;

import static com.compiler.semantic.DiagnosticCode.E_DUPLICATE;
import static com.compiler.semantic.DiagnosticCode.E_FORWARD;
import static com.compiler.semantic.DiagnosticCode.E_KIND;
import static com.compiler.semantic.DiagnosticCode.E_SIGNATURE;
import static com.compiler.semantic.DiagnosticCode.E_UNDECLARED;
import static com.compiler.semantic.SemanticType.Scalar.ERROR;

/** Variables, types and routines share a namespace in each lexical scope. */
final class Scope {
    private final Scope parent;
    private final AnalysisResult.Builder result;
    // Missing routine definitions are reported in declaration order.
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();

    Scope(AnalysisResult.Builder result) {
        this(null, result);
    }

    private Scope(Scope parent, AnalysisResult.Builder result) {
        this.parent = parent;
        this.result = result;
    }

    Scope child() {
        return new Scope(this, result);
    }

    void declareVariable(String name, SemanticType type, Node declaration) {
        declare(name, new Symbol(Kind.VARIABLE, type, declaration));
    }

    void declareType(TypeDeclaration declaration, SemanticType type) {
        declare(declaration.name(), new Symbol(Kind.TYPE, type, declaration));
    }

    void declareRoutine(RoutineDeclaration routine,
                        List<SemanticType> parameters,
                        SemanticType returnType) {
        String name = routine.name();
        Symbol previous = symbols.get(name);
        boolean hasBody = routine.body().isPresent();
        if (previous == null) {
            symbols.put(name,
                    new Routine(returnType, routine, parameters, hasBody));
        } else if (previous instanceof Routine forward
                && !forward.defined && hasBody) {
            if (!TypeRules.sameType(forward.type, returnType)
                    || !TypeRules.sameParameters(forward.parameters,
                            parameters)) {
                result.report(E_SIGNATURE, routine, "Definition of '" + name
                        + "' does not match its forward declaration");
            }
            forward.defined = true;
        } else {
            result.report(E_DUPLICATE, routine,
                    "Duplicate declaration of '" + name + "'");
        }
    }

    void reportUndefinedRoutines() {
        for (var entry : symbols.entrySet()) {
            if (entry.getValue() instanceof Routine routine
                    && !routine.defined) {
                result.report(E_FORWARD, routine.declaration,
                        "Routine '" + entry.getKey() + "' has no definition");
            }
        }
    }

    Symbol resolveVariable(Identifier identifier) {
        return resolve(identifier.name(), Kind.VARIABLE, identifier);
    }

    SemanticType resolveType(String name, Node useSite) {
        Symbol symbol = resolve(name, Kind.TYPE, useSite);
        return symbol == null ? ERROR : symbol.type;
    }

    Routine resolveRoutine(RoutineCall call) {
        return (Routine) resolve(call.name(), Kind.ROUTINE, call);
    }

    Symbol find(String name) {
        for (Scope scope = this; scope != null; scope = scope.parent) {
            Symbol symbol = scope.symbols.get(name);
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    private void declare(String name, Symbol symbol) {
        if (symbols.putIfAbsent(name, symbol) != null) {
            result.report(E_DUPLICATE, symbol.declaration,
                    "Duplicate declaration of '" + name + "' in this scope");
        }
    }

    private Symbol resolve(String name, Kind kind, Node useSite) {
        Symbol symbol = find(name);
        if (symbol == null) {
            result.report(E_UNDECLARED, useSite,
                    "'" + name + "' must be declared before use");
        } else if (symbol.kind != kind) {
            result.report(E_KIND, useSite, "'" + name + "' is not a "
                    + kind.name().toLowerCase(Locale.ROOT));
            return null;
        }
        return symbol;
    }

    private enum Kind {
        VARIABLE, TYPE, ROUTINE
    }

    static class Symbol {
        private final Kind kind;
        final SemanticType type;
        final Node declaration;

        private Symbol(Kind kind, SemanticType type, Node declaration) {
            this.kind = kind;
            this.type = type;
            this.declaration = declaration;
        }
    }

    static final class Routine extends Symbol {
        final List<SemanticType> parameters;
        private boolean defined;

        private Routine(SemanticType returnType, RoutineDeclaration declaration,
                        List<SemanticType> parameters, boolean defined) {
            super(Kind.ROUTINE, returnType, declaration);
            this.parameters = List.copyOf(parameters);
            this.defined = defined;
        }
    }
}
