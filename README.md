# m.A.A.d

**m.A.A.d** — **minimalist Algorithmic Application dialect** — is an imperative programming language developed for the **Compiler Construction** course at **Innopolis University**.

The compiler is implemented in **Java**, with the parser generated using **Bison**. Programs written in `.maad` are compiled to **Jasmin assembly**.

## Team

- [Semen Nadutkin](mailto:s.nadutkin@innopolis.university)
- [Igor Baranov](mailto:ig.baranov@innopolis.university)

## How to Run
**Prerequisites:** JDK 25, Maven 3.9.12+

```bash
// Replace <your_file> with the path to your MAAD file! :D
mvn -q compile exec:java -Dexec.args="<your_file>.maad"
```

## Parser

**Prerequisites:** Bison 3.8.2+

### Generation with Bison

```
bison -Wall                                             \
      -o src/main/java/com/compiler/parser/Parser.java  \
      src/main/bison/parser.y
```
