package com.compiler.semantic;

/** Stable diagnostic identifiers used in command-line output. */
public enum DiagnosticCode {
    // Declarations and name resolution.
    E_UNDECLARED,       // Name unavailable at its use site.
    E_DUPLICATE,        // Name redeclared in the same scope.
    E_KIND,             // Name used as the wrong kind of declaration.
    E_FORWARD,          // Routine declared without a definition.
    E_SIGNATURE,        // Definition differs from the forward declaration.

    // Types, values and routine calls.
    E_TYPE,             // Incompatible types.
    E_INFER,            // Cannot infer the variable's type.
    E_CONVERSION,       // Constant value falls outside the conversion range.
    E_LITERAL,          // Invalid literal value.
    E_ARITY,            // Wrong number of arguments.
    E_VOID,             // Procedure call used as a value.
    E_RETURN,           // Return statement disagrees with the declared result.
    E_MISSING_RETURN,   // Function can reach its end without returning.

    // Records, arrays and assignment targets.
    E_CONSTRUCTOR,      // Constructor names a type other than a record.
    E_FIELD,            // Unknown field.
    E_DUPLICATE_FIELD,  // Field initialized twice.
    E_ARRAY_SIZE,       // Negative array size.
    E_INDEX,            // Indexed value has a type other than an array.
    E_BOUNDS,           // Constant index outside array bounds.
    E_ITERATION,        // Invalid source in a for loop.
    E_READONLY,         // Assignment to a read-only binding or field.
    E_LVALUE,           // Invalid assignment target.
    E_DIV_ZERO          // Constant zero divisor.
}
