# m.A.A.d

**m.A.A.d** — **minimalist Algorithmic Application dialect** — is an imperative programming language developed for the **Compiler Construction** course at **Innopolis University**.

The compiler is implemented in **Java**, with the parser generated using **Bison**. Programs written in `.maad` are compiled to **Jasmin assembly**.

## Team

- [Semen Nadutkin](mailto:s.nadutkin@innopolis.university)
- [Igor Baranov](mailto:ig.baranov@innopolis.university)

## Code generation

Requires JDK 21 or newer. The compiler runs semantic analysis, optimizes the AST,
then generates Jasmin using semantic metadata for the optimized tree.

```sh
mvn compile
java -cp target/classes com.compiler.Main examples/simple_main.maad
java -jar jasmin.jar examples/simple_main.j
java -cp examples simple_main 2.0
```

The optional second argument sets the output `.j` path; `--ast` prints the AST instead.
`--ast` shows the parsed source before semantic analysis and optimization.
Records produce additional `<class>$RecordN.j` files; assemble these with Jasmin too.
The JVM entry point calls `main` or `Main`, parsing primitive parameters from command-line arguments.
Arrays use 1-based indexing. `print` writes each value on its own line.
End-to-end checks: `python3 tests/codegen.py /path/to/jasmin.jar` (after `mvn compile`).

## Semantics and optimization

Names must be declared before use; forward routine declarations support later
definitions and mutual recursion. Diagnostics include source locations and codes
from `DiagnosticCode`; semantic errors prevent code generation.

The optimizer folds constant expressions, removes constant branches and empty loops,
and eliminates unreachable statements and safe unused scalar locals. All source
code is checked before optimization, including branches that will be removed.

Real-to-integer conversion uses `Math.round` (ties toward positive infinity).
Real-to-boolean conversion is rejected; integer constants converted to boolean
must be 0 or 1. Runtime real-to-integer overflow raises `ArithmeticException`.

Examples: `examples/semantic/valid.maad`, `optimizations.maad`, and `errors.maad`.

## Parser

Generate Java parser from Bison source:

```
bison -Wall                                             \
      -o src/main/java/com/compiler/parser/Parser.java  \
      src/main/bison/parser.y
```
