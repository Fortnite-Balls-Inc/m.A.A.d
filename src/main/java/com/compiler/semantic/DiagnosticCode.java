package com.compiler.semantic;

/** Stable diagnostic identifiers used by the analyzer and command-line output. */
public enum DiagnosticCode {
    // Declarations and name resolution.
    E_UNDECLARED,   // name is not declared/accessible in the current scope
    E_DUPLICATE,    // name is redeclared in the same scope
    E_KIND,         // wrong name usage (e.g. variable as a procedure)
    E_FORWARD,      // no definition for a routine forward declaration provided
    E_SIGNATURE,    // routine definition and forward declaration signatures do not match

    // Types, values, and routine calls.
    E_TYPE,             // incompatible types when assigning, passing, returning, ... an argument
    E_INFER,            // could not infer the variable type from the RHS expression
    E_CONVERSION,       // conversion between the types is possible in general but not in the specific case 
    E_LITERAL,          // invalid literal value (e.g. overflowing real literal)
    E_ARITY,            // invalid number of arguments provided in the function call
    E_VOID,             // procedure without the return value appears on the RHS of an assignment/initialization
    E_RETURN,           // empty return on a procedure with a declared result and vice versa
    E_MISSING_RETURN,   // routine with the declared result may not return a value

    // Records, arrays, and assignment targets.
    E_CONSTRUCTOR,      // new is used on a non-record type
    E_FIELD,            // a non-existent field used
    E_DUPLICATE_FIELD,  // a field is used twice in constructor
    E_ARRAY_SIZE,       // negative array size
    E_INDEX,            // attempting to index a non-array variable
    E_BOUNDS,           // constant index is out of array bounds
    E_ITERATION,        // for <variable> in <expression>: <expression> is not a range or array
    E_READONLY,         // assigning a value to a read-only field/variable (e.g. array.length)
    E_LVALUE,           // invalid value used on the LHS of :=
    E_DIV_ZERO          // zero on the RHS of / or %
}
