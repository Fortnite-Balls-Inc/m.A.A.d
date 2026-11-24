# m.A.A.d

**m.A.A.d** — **minimalist Algorithmic Application dialect** — is an imperative programming language developed for the **Compiler Construction** course at **Innopolis University**.

The compiler is implemented in **Java**, with the parser generated using **Bison**. Programs written in `.maad` are compiled to **Jasmin assembly**.

## Team

- [Semen Nadutkin](mailto:s.nadutkin@innopolis.university)
- [Igor Baranov](mailto:ig.baranov@innopolis.university)

## Compiling from Source

### Generating the Parser

_This step is optional. The generated parser files are already included in `src/main/java/com/compiler/parser`._

**Requirements**: Bison 3.8.2+

- Linux / macOS

   ```sh
   bison -Wall                                             \
         -o src/main/java/com/compiler/parser/Parser.java  \
         src/main/bison/parser.y
   ```

- Windows

   ```powershell
   bison -Wall -o src\main\java\com\compiler\parser\Parser.java src\main\bison\parser.y
   ```

### Building the Compiler

The executable JAR is generated at `target/maad.jar` _(`target\maad.jar` in Windows)._

**Requirements**: Apache Maven 3.9.12+, JDK 21+

```sh
mvn package
```

## Running m.A.A.d

_**Prerequisites**: all necessary steps from the **Compiling from Source** stage._

**Requirements**: JDK 21+

### Linux / macOS

```sh
# Compile to Jasmin Assembly
./maad compile <source>.maad [-o <target>.j]

# Run a program
./maad run <source>.maad <starting-routine> [<arg_1> ... <arg_N>]
```

**Examples**:

1. Running a program

   ```sh
   # Factorial of 5
   # Output: 120
   ./maad run examples/recursion.maad Main 5
   ```

2. Compiling `.maad` to `.j`

   ```sh
   # The result is written to recursion.j
   ./maad compile examples/recursion.maad

   # The result is written to examples/custom.j
   ./maad compile examples/recursion.maad -o examples/custom.j
   ```

### Windows

```powershell
# Compile to Jasmin Assembly
.\maad.cmd compile <source>.maad [-o <target>.j]

# Run a program
.\maad run <source>.maad <starting-routine> [<arg_1> ... <arg_N>]
```

**Examples**:

1. Running a program

   ```powershell
   # Factorial of 5
   # Output: 120
   .\maad.cmd run examples\recursion.maad Main 5
   ```

2. Compiling `.maad` to `.j`

   ```powershell
   # The result is written to recursion.j
   .\maad.cmd compile examples\recursion.maad

   # The result is written to examples\custom.j
   .\maad.cmd compile examples\recursion.maad -o examples\custom.j
   ```
