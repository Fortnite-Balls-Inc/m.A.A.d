# m.A.A.d

**m.A.A.d** — **minimalist Algorithmic Application dialect** — is an imperative programming language developed for the **Compiler Construction** course at **Innopolis University**.

The compiler is implemented in **Java**, with the parser generated using **Bison**. Programs written in `.maad` are compiled to **Jasmin assembly**.

## Team

- [Semen Nadutkin](mailto:s.nadutkin@innopolis.university)
- [Igor Baranov](mailto:ig.baranov@innopolis.university)

## Parser

Generate Java parser from Bison source:

```
bison -Wall                                             \
      -o src/main/java/com/compiler/parser/Parser.java  \
      src/main/bison/parser.y
```