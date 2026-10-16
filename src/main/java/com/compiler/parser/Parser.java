/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Skeleton implementation for Bison LALR(1) parsers in Java

   Copyright (C) 2007-2015, 2018-2021 Free Software Foundation, Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

package com.compiler.parser;



import java.text.MessageFormat;
import java.util.ArrayList;
/* "%code imports" blocks.  */
/* "src/main/bison/parser.y":7  */

    import com.compiler.lexer.Token;
    import com.compiler.ast.SourcePosition;
    import com.compiler.ast.expression.Expression;
    import com.compiler.ast.expression.BooleanLiteral;
    import com.compiler.ast.expression.IntegerLiteral;
    import com.compiler.ast.expression.RealLiteral;
    import com.compiler.ast.expression.NullLiteral;
    import com.compiler.ast.expression.Identifier;
    import com.compiler.ast.expression.BinaryExpression;
    import com.compiler.ast.expression.BinaryOperator;
    import com.compiler.ast.expression.UnaryExpression;
    import com.compiler.ast.expression.UnaryOperator;
    import com.compiler.ast.expression.FieldAccess;
    import com.compiler.ast.expression.IndexAccess;
    import com.compiler.ast.expression.RoutineCall;
    import com.compiler.ast.expression.RecordConstructor;
    import com.compiler.ast.expression.MemberInitialization;
    import com.compiler.parser.support.ExpressionList;
    import com.compiler.parser.support.MemberInitializationList;
    import com.compiler.ast.types.ArrayType;
    import com.compiler.ast.types.NamedType;
    import com.compiler.ast.types.PrimitiveKind;
    import com.compiler.ast.types.PrimitiveType;
    import com.compiler.ast.types.RecordType;
    import com.compiler.ast.types.TypeNode;
    import com.compiler.ast.types.UserType;
    import com.compiler.ast.declaration.SimpleDeclaration;
    import com.compiler.ast.declaration.VariableDeclaration;
    import com.compiler.ast.declaration.TypeDeclaration;
    import com.compiler.parser.support.VariableDeclarationList;
    import com.compiler.ast.Program;
    import com.compiler.ast.Block;
    import com.compiler.ast.BlockItem;
    import com.compiler.ast.declaration.Declaration;
    import com.compiler.ast.declaration.Parameter;
    import com.compiler.ast.declaration.RoutineDeclaration;
    import com.compiler.ast.declaration.RoutineBody;
    import com.compiler.ast.declaration.BlockRoutineBody;
    import com.compiler.ast.declaration.ExpressionRoutineBody;
    import com.compiler.ast.statement.Statement;
    import com.compiler.ast.statement.Assignment;
    import com.compiler.ast.statement.RoutineCallStatement;
    import com.compiler.ast.statement.WhileLoop;
    import com.compiler.ast.statement.ForLoop;
    import com.compiler.ast.statement.IfStatement;
    import com.compiler.ast.statement.PrintStatement;
    import com.compiler.ast.statement.ReturnStatement;
    import com.compiler.ast.statement.IterationSource;
    import com.compiler.ast.statement.ExpressionSource;
    import com.compiler.ast.statement.RangeSource;
    import com.compiler.parser.support.DeclarationList;
    import com.compiler.parser.support.BlockItemList;
    import com.compiler.parser.support.ParameterList;
    import com.compiler.parser.support.RoutineHeader;
    import java.util.Optional;
    import java.util.List;

/* "src/main/java/com/compiler/parser/Parser.java":104  */

/**
 * A Bison parser, automatically generated from <tt>src/main/bison/parser.y</tt>.
 *
 * @author LALR (1) parser skeleton written by Paolo Bonzini.
 */
public class Parser
{
  /** Version number for the Bison executable that generated this parser.  */
  public static final String bisonVersion = "3.8.2";

  /** Name of the skeleton that generated this parser.  */
  public static final String bisonSkeleton = "lalr1.java";



  /**
   * True if verbose error messages are enabled.
   */
  private boolean yyErrorVerbose = true;

  /**
   * Whether verbose error messages are enabled.
   */
  public final boolean getErrorVerbose() { return yyErrorVerbose; }

  /**
   * Set the verbosity of error messages.
   * @param verbose True to request verbose error messages.
   */
  public final void setErrorVerbose(boolean verbose)
  { yyErrorVerbose = verbose; }




  public enum SymbolKind
  {
    S_YYEOF(0),                    /* "end of file"  */
    S_YYerror(1),                  /* error  */
    S_YYUNDEF(2),                  /* "invalid token"  */
    S_ROUTINE(3),                  /* ROUTINE  */
    S_VAR(4),                      /* VAR  */
    S_TYPE(5),                     /* TYPE  */
    S_IS(6),                       /* IS  */
    S_END(7),                      /* END  */
    S_IF(8),                       /* IF  */
    S_THEN(9),                     /* THEN  */
    S_ELSE(10),                    /* ELSE  */
    S_WHILE(11),                   /* WHILE  */
    S_LOOP(12),                    /* LOOP  */
    S_FOR(13),                     /* FOR  */
    S_IN(14),                      /* IN  */
    S_REVERSE(15),                 /* REVERSE  */
    S_RETURN(16),                  /* RETURN  */
    S_PRINT(17),                   /* PRINT  */
    S_RECORD(18),                  /* RECORD  */
    S_ARRAY(19),                   /* ARRAY  */
    S_INTEGER(20),                 /* INTEGER  */
    S_REAL(21),                    /* REAL  */
    S_BOOLEAN(22),                 /* BOOLEAN  */
    S_TRUE(23),                    /* TRUE  */
    S_FALSE(24),                   /* FALSE  */
    S_NULL(25),                    /* NULL  */
    S_AND(26),                     /* AND  */
    S_OR(27),                      /* OR  */
    S_XOR(28),                     /* XOR  */
    S_NOT(29),                     /* NOT  */
    S_IDENTIFIER(30),              /* IDENTIFIER  */
    S_INTEGER_LITERAL(31),         /* INTEGER_LITERAL  */
    S_REAL_LITERAL(32),            /* REAL_LITERAL  */
    S_ASSIGN(33),                  /* ASSIGN  */
    S_PLUS(34),                    /* PLUS  */
    S_MINUS(35),                   /* MINUS  */
    S_STAR(36),                    /* STAR  */
    S_SLASH(37),                   /* SLASH  */
    S_PERCENT(38),                 /* PERCENT  */
    S_EQ(39),                      /* EQ  */
    S_NEQ(40),                     /* NEQ  */
    S_LT(41),                      /* LT  */
    S_LE(42),                      /* LE  */
    S_GT(43),                      /* GT  */
    S_GE(44),                      /* GE  */
    S_RETIMM(45),                  /* RETIMM  */
    S_COLON(46),                   /* COLON  */
    S_DOT(47),                     /* DOT  */
    S_DOTDOT(48),                  /* DOTDOT  */
    S_LPAREN(49),                  /* LPAREN  */
    S_RPAREN(50),                  /* RPAREN  */
    S_LBRACKET(51),                /* LBRACKET  */
    S_RBRACKET(52),                /* RBRACKET  */
    S_COMMA(53),                   /* COMMA  */
    S_SEMICOLON(54),               /* SEMICOLON  */
    S_NEWLINE(55),                 /* NEWLINE  */
    S_NEW(56),                     /* NEW  */
    S_YYACCEPT(57),                /* $accept  */
    S_program(58),                 /* program  */
    S_declaration_sequence(59),    /* declaration_sequence  */
    S_top_level_declaration(60),   /* top_level_declaration  */
    S_simple_declaration(61),      /* simple_declaration  */
    S_variable_declaration(62),    /* variable_declaration  */
    S_type_declaration(63),        /* type_declaration  */
    S_expression(64),              /* expression  */
    S_xor_expression(65),          /* xor_expression  */
    S_and_expression(66),          /* and_expression  */
    S_relation(67),                /* relation  */
    S_comparison_operator(68),     /* comparison_operator  */
    S_simple(69),                  /* simple  */
    S_factor(70),                  /* factor  */
    S_summand(71),                 /* summand  */
    S_primary(72),                 /* primary  */
    S_integer_literal(73),         /* integer_literal  */
    S_real_literal(74),            /* real_literal  */
    S_boolean_literal(75),         /* boolean_literal  */
    S_modifiable_primary(76),      /* modifiable_primary  */
    S_routine_call(77),            /* routine_call  */
    S_optional_arguments(78),      /* optional_arguments  */
    S_argument_list(79),           /* argument_list  */
    S_argument_list_tail(80),      /* argument_list_tail  */
    S_constructor_expression(81),  /* constructor_expression  */
    S_optional_members(82),        /* optional_members  */
    S_member_initialization_list(83), /* member_initialization_list  */
    S_member_initialization_list_tail(84), /* member_initialization_list_tail  */
    S_member_initialization(85),   /* member_initialization  */
    S_optional_initializer(86),    /* optional_initializer  */
    S_type(87),                    /* type  */
    S_primitive_type(88),          /* primitive_type  */
    S_user_type(89),               /* user_type  */
    S_array_type(90),              /* array_type  */
    S_optional_array_size(91),     /* optional_array_size  */
    S_record_type(92),             /* record_type  */
    S_record_member_sequence(93),  /* record_member_sequence  */
    S_named_type(94),              /* named_type  */
    S_statement(95),               /* statement  */
    S_assignment(96),              /* assignment  */
    S_while_loop(97),              /* while_loop  */
    S_body(98),                    /* body  */
    S_body_sequence(99),           /* body_sequence  */
    S_body_item(100),              /* body_item  */
    S_for_loop(101),               /* for_loop  */
    S_range(102),                  /* range  */
    S_optional_reverse(103),       /* optional_reverse  */
    S_if_statement(104),           /* if_statement  */
    S_optional_else(105),          /* optional_else  */
    S_print_statement(106),        /* print_statement  */
    S_return_statement(107),       /* return_statement  */
    S_routine_declaration(108),    /* routine_declaration  */
    S_routine_header(109),         /* routine_header  */
    S_optional_parameters(110),    /* optional_parameters  */
    S_parameter_list(111),         /* parameter_list  */
    S_parameter(112),              /* parameter  */
    S_optional_return_type(113),   /* optional_return_type  */
    S_routine_body(114),           /* routine_body  */
    S_optional_separators(115),    /* optional_separators  */
    S_separators(116),             /* separators  */
    S_separator(117),              /* separator  */
    S_optional_newlines(118),      /* optional_newlines  */
    S_newlines(119);               /* newlines  */


    private final int yycode_;

    SymbolKind (int n) {
      this.yycode_ = n;
    }

    private static final SymbolKind[] values_ = {
      SymbolKind.S_YYEOF,
      SymbolKind.S_YYerror,
      SymbolKind.S_YYUNDEF,
      SymbolKind.S_ROUTINE,
      SymbolKind.S_VAR,
      SymbolKind.S_TYPE,
      SymbolKind.S_IS,
      SymbolKind.S_END,
      SymbolKind.S_IF,
      SymbolKind.S_THEN,
      SymbolKind.S_ELSE,
      SymbolKind.S_WHILE,
      SymbolKind.S_LOOP,
      SymbolKind.S_FOR,
      SymbolKind.S_IN,
      SymbolKind.S_REVERSE,
      SymbolKind.S_RETURN,
      SymbolKind.S_PRINT,
      SymbolKind.S_RECORD,
      SymbolKind.S_ARRAY,
      SymbolKind.S_INTEGER,
      SymbolKind.S_REAL,
      SymbolKind.S_BOOLEAN,
      SymbolKind.S_TRUE,
      SymbolKind.S_FALSE,
      SymbolKind.S_NULL,
      SymbolKind.S_AND,
      SymbolKind.S_OR,
      SymbolKind.S_XOR,
      SymbolKind.S_NOT,
      SymbolKind.S_IDENTIFIER,
      SymbolKind.S_INTEGER_LITERAL,
      SymbolKind.S_REAL_LITERAL,
      SymbolKind.S_ASSIGN,
      SymbolKind.S_PLUS,
      SymbolKind.S_MINUS,
      SymbolKind.S_STAR,
      SymbolKind.S_SLASH,
      SymbolKind.S_PERCENT,
      SymbolKind.S_EQ,
      SymbolKind.S_NEQ,
      SymbolKind.S_LT,
      SymbolKind.S_LE,
      SymbolKind.S_GT,
      SymbolKind.S_GE,
      SymbolKind.S_RETIMM,
      SymbolKind.S_COLON,
      SymbolKind.S_DOT,
      SymbolKind.S_DOTDOT,
      SymbolKind.S_LPAREN,
      SymbolKind.S_RPAREN,
      SymbolKind.S_LBRACKET,
      SymbolKind.S_RBRACKET,
      SymbolKind.S_COMMA,
      SymbolKind.S_SEMICOLON,
      SymbolKind.S_NEWLINE,
      SymbolKind.S_NEW,
      SymbolKind.S_YYACCEPT,
      SymbolKind.S_program,
      SymbolKind.S_declaration_sequence,
      SymbolKind.S_top_level_declaration,
      SymbolKind.S_simple_declaration,
      SymbolKind.S_variable_declaration,
      SymbolKind.S_type_declaration,
      SymbolKind.S_expression,
      SymbolKind.S_xor_expression,
      SymbolKind.S_and_expression,
      SymbolKind.S_relation,
      SymbolKind.S_comparison_operator,
      SymbolKind.S_simple,
      SymbolKind.S_factor,
      SymbolKind.S_summand,
      SymbolKind.S_primary,
      SymbolKind.S_integer_literal,
      SymbolKind.S_real_literal,
      SymbolKind.S_boolean_literal,
      SymbolKind.S_modifiable_primary,
      SymbolKind.S_routine_call,
      SymbolKind.S_optional_arguments,
      SymbolKind.S_argument_list,
      SymbolKind.S_argument_list_tail,
      SymbolKind.S_constructor_expression,
      SymbolKind.S_optional_members,
      SymbolKind.S_member_initialization_list,
      SymbolKind.S_member_initialization_list_tail,
      SymbolKind.S_member_initialization,
      SymbolKind.S_optional_initializer,
      SymbolKind.S_type,
      SymbolKind.S_primitive_type,
      SymbolKind.S_user_type,
      SymbolKind.S_array_type,
      SymbolKind.S_optional_array_size,
      SymbolKind.S_record_type,
      SymbolKind.S_record_member_sequence,
      SymbolKind.S_named_type,
      SymbolKind.S_statement,
      SymbolKind.S_assignment,
      SymbolKind.S_while_loop,
      SymbolKind.S_body,
      SymbolKind.S_body_sequence,
      SymbolKind.S_body_item,
      SymbolKind.S_for_loop,
      SymbolKind.S_range,
      SymbolKind.S_optional_reverse,
      SymbolKind.S_if_statement,
      SymbolKind.S_optional_else,
      SymbolKind.S_print_statement,
      SymbolKind.S_return_statement,
      SymbolKind.S_routine_declaration,
      SymbolKind.S_routine_header,
      SymbolKind.S_optional_parameters,
      SymbolKind.S_parameter_list,
      SymbolKind.S_parameter,
      SymbolKind.S_optional_return_type,
      SymbolKind.S_routine_body,
      SymbolKind.S_optional_separators,
      SymbolKind.S_separators,
      SymbolKind.S_separator,
      SymbolKind.S_optional_newlines,
      SymbolKind.S_newlines
    };

    static final SymbolKind get(int code) {
      return values_[code];
    }

    public final int getCode() {
      return this.yycode_;
    }

    /* YYNAMES_[SYMBOL-NUM] -- String name of the symbol SYMBOL-NUM.
       First, the terminals, then, starting at \a YYNTOKENS_, nonterminals.  */
    private static final String[] yynames_ = yynames_init();
  private static final String[] yynames_init()
  {
    return new String[]
    {
  "end of file", "error", "invalid token", "ROUTINE", "VAR", "TYPE", "IS",
  "END", "IF", "THEN", "ELSE", "WHILE", "LOOP", "FOR", "IN", "REVERSE",
  "RETURN", "PRINT", "RECORD", "ARRAY", "INTEGER", "REAL", "BOOLEAN",
  "TRUE", "FALSE", "NULL", "AND", "OR", "XOR", "NOT", "IDENTIFIER",
  "INTEGER_LITERAL", "REAL_LITERAL", "ASSIGN", "PLUS", "MINUS", "STAR",
  "SLASH", "PERCENT", "EQ", "NEQ", "LT", "LE", "GT", "GE", "RETIMM",
  "COLON", "DOT", "DOTDOT", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET",
  "COMMA", "SEMICOLON", "NEWLINE", "NEW", "$accept", "program",
  "declaration_sequence", "top_level_declaration", "simple_declaration",
  "variable_declaration", "type_declaration", "expression",
  "xor_expression", "and_expression", "relation", "comparison_operator",
  "simple", "factor", "summand", "primary", "integer_literal",
  "real_literal", "boolean_literal", "modifiable_primary", "routine_call",
  "optional_arguments", "argument_list", "argument_list_tail",
  "constructor_expression", "optional_members",
  "member_initialization_list", "member_initialization_list_tail",
  "member_initialization", "optional_initializer", "type",
  "primitive_type", "user_type", "array_type", "optional_array_size",
  "record_type", "record_member_sequence", "named_type", "statement",
  "assignment", "while_loop", "body", "body_sequence", "body_item",
  "for_loop", "range", "optional_reverse", "if_statement", "optional_else",
  "print_statement", "return_statement", "routine_declaration",
  "routine_header", "optional_parameters", "parameter_list", "parameter",
  "optional_return_type", "routine_body", "optional_separators",
  "separators", "separator", "optional_newlines", "newlines", null
    };
  }

    /* The user-facing name of this symbol.  */
    public final String getName() {
      return yynames_[yycode_];
    }
  };


  /**
   * Communication interface between the scanner and the Bison-generated
   * parser <tt>Parser</tt>.
   */
  public interface Lexer {
    /* Token kinds.  */
    /** Token "end of file", to be returned by the scanner.  */
    static final int YYEOF = 0;
    /** Token error, to be returned by the scanner.  */
    static final int YYerror = 256;
    /** Token "invalid token", to be returned by the scanner.  */
    static final int YYUNDEF = 257;
    /** Token ROUTINE, to be returned by the scanner.  */
    static final int ROUTINE = 258;
    /** Token VAR, to be returned by the scanner.  */
    static final int VAR = 259;
    /** Token TYPE, to be returned by the scanner.  */
    static final int TYPE = 260;
    /** Token IS, to be returned by the scanner.  */
    static final int IS = 261;
    /** Token END, to be returned by the scanner.  */
    static final int END = 262;
    /** Token IF, to be returned by the scanner.  */
    static final int IF = 263;
    /** Token THEN, to be returned by the scanner.  */
    static final int THEN = 264;
    /** Token ELSE, to be returned by the scanner.  */
    static final int ELSE = 265;
    /** Token WHILE, to be returned by the scanner.  */
    static final int WHILE = 266;
    /** Token LOOP, to be returned by the scanner.  */
    static final int LOOP = 267;
    /** Token FOR, to be returned by the scanner.  */
    static final int FOR = 268;
    /** Token IN, to be returned by the scanner.  */
    static final int IN = 269;
    /** Token REVERSE, to be returned by the scanner.  */
    static final int REVERSE = 270;
    /** Token RETURN, to be returned by the scanner.  */
    static final int RETURN = 271;
    /** Token PRINT, to be returned by the scanner.  */
    static final int PRINT = 272;
    /** Token RECORD, to be returned by the scanner.  */
    static final int RECORD = 273;
    /** Token ARRAY, to be returned by the scanner.  */
    static final int ARRAY = 274;
    /** Token INTEGER, to be returned by the scanner.  */
    static final int INTEGER = 275;
    /** Token REAL, to be returned by the scanner.  */
    static final int REAL = 276;
    /** Token BOOLEAN, to be returned by the scanner.  */
    static final int BOOLEAN = 277;
    /** Token TRUE, to be returned by the scanner.  */
    static final int TRUE = 278;
    /** Token FALSE, to be returned by the scanner.  */
    static final int FALSE = 279;
    /** Token NULL, to be returned by the scanner.  */
    static final int NULL = 280;
    /** Token AND, to be returned by the scanner.  */
    static final int AND = 281;
    /** Token OR, to be returned by the scanner.  */
    static final int OR = 282;
    /** Token XOR, to be returned by the scanner.  */
    static final int XOR = 283;
    /** Token NOT, to be returned by the scanner.  */
    static final int NOT = 284;
    /** Token IDENTIFIER, to be returned by the scanner.  */
    static final int IDENTIFIER = 285;
    /** Token INTEGER_LITERAL, to be returned by the scanner.  */
    static final int INTEGER_LITERAL = 286;
    /** Token REAL_LITERAL, to be returned by the scanner.  */
    static final int REAL_LITERAL = 287;
    /** Token ASSIGN, to be returned by the scanner.  */
    static final int ASSIGN = 288;
    /** Token PLUS, to be returned by the scanner.  */
    static final int PLUS = 289;
    /** Token MINUS, to be returned by the scanner.  */
    static final int MINUS = 290;
    /** Token STAR, to be returned by the scanner.  */
    static final int STAR = 291;
    /** Token SLASH, to be returned by the scanner.  */
    static final int SLASH = 292;
    /** Token PERCENT, to be returned by the scanner.  */
    static final int PERCENT = 293;
    /** Token EQ, to be returned by the scanner.  */
    static final int EQ = 294;
    /** Token NEQ, to be returned by the scanner.  */
    static final int NEQ = 295;
    /** Token LT, to be returned by the scanner.  */
    static final int LT = 296;
    /** Token LE, to be returned by the scanner.  */
    static final int LE = 297;
    /** Token GT, to be returned by the scanner.  */
    static final int GT = 298;
    /** Token GE, to be returned by the scanner.  */
    static final int GE = 299;
    /** Token RETIMM, to be returned by the scanner.  */
    static final int RETIMM = 300;
    /** Token COLON, to be returned by the scanner.  */
    static final int COLON = 301;
    /** Token DOT, to be returned by the scanner.  */
    static final int DOT = 302;
    /** Token DOTDOT, to be returned by the scanner.  */
    static final int DOTDOT = 303;
    /** Token LPAREN, to be returned by the scanner.  */
    static final int LPAREN = 304;
    /** Token RPAREN, to be returned by the scanner.  */
    static final int RPAREN = 305;
    /** Token LBRACKET, to be returned by the scanner.  */
    static final int LBRACKET = 306;
    /** Token RBRACKET, to be returned by the scanner.  */
    static final int RBRACKET = 307;
    /** Token COMMA, to be returned by the scanner.  */
    static final int COMMA = 308;
    /** Token SEMICOLON, to be returned by the scanner.  */
    static final int SEMICOLON = 309;
    /** Token NEWLINE, to be returned by the scanner.  */
    static final int NEWLINE = 310;
    /** Token NEW, to be returned by the scanner.  */
    static final int NEW = 311;

    /** Deprecated, use YYEOF instead.  */
    public static final int EOF = YYEOF;


    /**
     * Method to retrieve the semantic value of the last scanned token.
     * @return the semantic value of the last scanned token.
     */
    Object getLVal();

    /**
     * Entry point for the scanner.  Returns the token identifier corresponding
     * to the next token and prepares to return the semantic value
     * of the token.
     * @return the token identifier corresponding to the next token.
     */
    int yylex() throws java.io.IOException;

    /**
     * Emit an errorin a user-defined way.
     *
     *
     * @param msg The string for the error message.
     */
     void yyerror(String msg);


  }


  /**
   * The object doing lexical analysis for us.
   */
  private Lexer yylexer;





  /**
   * Instantiates the Bison-generated parser.
   * @param yylexer The scanner that will supply tokens to the parser.
   */
  public Parser(Lexer yylexer)
  {

    this.yylexer = yylexer;

  }



  private int yynerrs = 0;

  /**
   * The number of syntax errors so far.
   */
  public final int getNumberOfErrors() { return yynerrs; }

  /**
   * Print an error message via the lexer.
   *
   * @param msg The error message.
   */
  public final void yyerror(String msg) {
      yylexer.yyerror(msg);
  }



  private final class YYStack {
    private int[] stateStack = new int[16];
    private Object[] valueStack = new Object[16];

    public int size = 16;
    public int height = -1;

    public final void push(int state, Object value) {
      height++;
      if (size == height) {
        int[] newStateStack = new int[size * 2];
        System.arraycopy(stateStack, 0, newStateStack, 0, height);
        stateStack = newStateStack;

        Object[] newValueStack = new Object[size * 2];
        System.arraycopy(valueStack, 0, newValueStack, 0, height);
        valueStack = newValueStack;

        size *= 2;
      }

      stateStack[height] = state;
      valueStack[height] = value;
    }

    public final void pop() {
      pop(1);
    }

    public final void pop(int num) {
      // Avoid memory leaks... garbage collection is a white lie!
      if (0 < num) {
        java.util.Arrays.fill(valueStack, height - num + 1, height + 1, null);
      }
      height -= num;
    }

    public final int stateAt(int i) {
      return stateStack[height - i];
    }

    public final Object valueAt(int i) {
      return valueStack[height - i];
    }

    // Print the state stack on the debug stream.
    public void print(java.io.PrintStream out) {
      out.print ("Stack now");

      for (int i = 0; i <= height; i++) {
        out.print(' ');
        out.print(stateStack[i]);
      }
      out.println();
    }
  }

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return success (<tt>true</tt>).
   */
  public static final int YYACCEPT = 0;

  /**
   * Returned by a Bison action in order to stop the parsing process and
   * return failure (<tt>false</tt>).
   */
  public static final int YYABORT = 1;



  /**
   * Returned by a Bison action in order to start error recovery without
   * printing an error message.
   */
  public static final int YYERROR = 2;

  /**
   * Internal return codes that are not supported for user semantic
   * actions.
   */
  private static final int YYERRLAB = 3;
  private static final int YYNEWSTATE = 4;
  private static final int YYDEFAULT = 5;
  private static final int YYREDUCE = 6;
  private static final int YYERRLAB1 = 7;
  private static final int YYRETURN = 8;


  private int yyerrstatus_ = 0;


  /**
   * Whether error recovery is being done.  In this state, the parser
   * reads token until it reaches a known state, and then restarts normal
   * operation.
   */
  public final boolean recovering ()
  {
    return yyerrstatus_ == 0;
  }

  /** Compute post-reduction state.
   * @param yystate   the current state
   * @param yysym     the nonterminal to push on the stack
   */
  private int yyLRGotoState(int yystate, int yysym) {
    int yyr = yypgoto_[yysym - YYNTOKENS_] + yystate;
    if (0 <= yyr && yyr <= YYLAST_ && yycheck_[yyr] == yystate)
      return yytable_[yyr];
    else
      return yydefgoto_[yysym - YYNTOKENS_];
  }

  private int yyaction(int yyn, YYStack yystack, int yylen)
  {
    /* If YYLEN is nonzero, implement the default value of the action:
       '$$ = $1'.  Otherwise, use the top of the stack.

       Otherwise, the following line sets YYVAL to garbage.
       This behavior is undocumented and Bison
       users should not rely upon it.  */
    Object yyval = (0 < yylen) ? yystack.valueAt(yylen - 1) : yystack.valueAt(0);

    switch (yyn)
      {
          case 2: /* program: optional_separators  */
  if (yyn == 2)
    /* "src/main/bison/parser.y":195  */
      { program = new Program(List.of(), new SourcePosition(1, 1)); yyval = program; };
  break;


  case 3: /* program: optional_separators declaration_sequence  */
  if (yyn == 3)
    /* "src/main/bison/parser.y":197  */
      { program = new Program(((DeclarationList)(yystack.valueAt (0))).values(), new SourcePosition(1, 1)); yyval = program; };
  break;


  case 4: /* declaration_sequence: top_level_declaration optional_separators  */
  if (yyn == 4)
    /* "src/main/bison/parser.y":208  */
      {
        DeclarationList declarations = new DeclarationList(); // the last TLD
        declarations.addFirst(((Declaration)(yystack.valueAt (1))));
        yyval = declarations;
      };
  break;


  case 5: /* declaration_sequence: top_level_declaration separators declaration_sequence  */
  if (yyn == 5)
    /* "src/main/bison/parser.y":214  */
      { ((DeclarationList)(yystack.valueAt (0))).addFirst(((Declaration)(yystack.valueAt (2)))); yyval = ((DeclarationList)(yystack.valueAt (0))); };
  break;


  case 6: /* top_level_declaration: simple_declaration  */
  if (yyn == 6)
    /* "src/main/bison/parser.y":227  */
      { yyval = ((SimpleDeclaration)(yystack.valueAt (0))); };
  break;


  case 7: /* top_level_declaration: routine_declaration  */
  if (yyn == 7)
    /* "src/main/bison/parser.y":229  */
      { yyval = ((RoutineDeclaration)(yystack.valueAt (0))); };
  break;


  case 8: /* simple_declaration: variable_declaration  */
  if (yyn == 8)
    /* "src/main/bison/parser.y":242  */
      { yyval = ((VariableDeclaration)(yystack.valueAt (0))); };
  break;


  case 9: /* simple_declaration: type_declaration  */
  if (yyn == 9)
    /* "src/main/bison/parser.y":244  */
      { yyval = ((TypeDeclaration)(yystack.valueAt (0))); };
  break;


  case 10: /* variable_declaration: VAR IDENTIFIER IS expression  */
  if (yyn == 10)
    /* "src/main/bison/parser.y":253  */
      {
        yyval = new VariableDeclaration(
            ((Token)(yystack.valueAt (2))).getStringValue(), Optional.empty(), Optional.of(((Expression)(yystack.valueAt (0)))), position(((Token)(yystack.valueAt (3))))
        );
      };
  break;


  case 11: /* variable_declaration: VAR IDENTIFIER COLON type optional_initializer  */
  if (yyn == 11)
    /* "src/main/bison/parser.y":259  */
      {
        yyval = new VariableDeclaration(
            ((Token)(yystack.valueAt (3))).getStringValue(), Optional.of(((TypeNode)(yystack.valueAt (1)))), Optional.ofNullable(((Expression)(yystack.valueAt (0)))), position(((Token)(yystack.valueAt (4))))
        );
      };
  break;


  case 12: /* type_declaration: TYPE IDENTIFIER IS type  */
  if (yyn == 12)
    /* "src/main/bison/parser.y":269  */
      { yyval = new TypeDeclaration(((Token)(yystack.valueAt (2))).getStringValue(), ((TypeNode)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (3))))); };
  break;


  case 13: /* expression: xor_expression  */
  if (yyn == 13)
    /* "src/main/bison/parser.y":291  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 14: /* expression: expression OR xor_expression  */
  if (yyn == 14)
    /* "src/main/bison/parser.y":293  */
      { yyval = new BinaryExpression(BinaryOperator.OR, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 15: /* xor_expression: and_expression  */
  if (yyn == 15)
    /* "src/main/bison/parser.y":298  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 16: /* xor_expression: xor_expression XOR and_expression  */
  if (yyn == 16)
    /* "src/main/bison/parser.y":300  */
      { yyval = new BinaryExpression(BinaryOperator.XOR, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 17: /* and_expression: relation  */
  if (yyn == 17)
    /* "src/main/bison/parser.y":305  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 18: /* and_expression: and_expression AND relation  */
  if (yyn == 18)
    /* "src/main/bison/parser.y":307  */
      { yyval = new BinaryExpression(BinaryOperator.AND, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 19: /* relation: simple  */
  if (yyn == 19)
    /* "src/main/bison/parser.y":312  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 20: /* relation: simple comparison_operator simple  */
  if (yyn == 20)
    /* "src/main/bison/parser.y":314  */
      { yyval = new BinaryExpression(((BinaryOperator)(yystack.valueAt (1))), ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 21: /* comparison_operator: LT  */
  if (yyn == 21)
    /* "src/main/bison/parser.y":318  */
          { yyval = BinaryOperator.LESS_THAN; };
  break;


  case 22: /* comparison_operator: LE  */
  if (yyn == 22)
    /* "src/main/bison/parser.y":319  */
          { yyval = BinaryOperator.LESS_THAN_OR_EQUAL; };
  break;


  case 23: /* comparison_operator: GT  */
  if (yyn == 23)
    /* "src/main/bison/parser.y":320  */
          { yyval = BinaryOperator.GREATER_THAN; };
  break;


  case 24: /* comparison_operator: GE  */
  if (yyn == 24)
    /* "src/main/bison/parser.y":321  */
          { yyval = BinaryOperator.GREATER_THAN_OR_EQUAL; };
  break;


  case 25: /* comparison_operator: EQ  */
  if (yyn == 25)
    /* "src/main/bison/parser.y":322  */
          { yyval = BinaryOperator.EQUAL; };
  break;


  case 26: /* comparison_operator: NEQ  */
  if (yyn == 26)
    /* "src/main/bison/parser.y":323  */
          { yyval = BinaryOperator.NOT_EQUAL; };
  break;


  case 27: /* simple: factor  */
  if (yyn == 27)
    /* "src/main/bison/parser.y":328  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 28: /* simple: simple PLUS factor  */
  if (yyn == 28)
    /* "src/main/bison/parser.y":330  */
      { yyval = new BinaryExpression(BinaryOperator.ADD, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 29: /* simple: simple MINUS factor  */
  if (yyn == 29)
    /* "src/main/bison/parser.y":332  */
      { yyval = new BinaryExpression(BinaryOperator.SUBTRACT, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 30: /* factor: summand  */
  if (yyn == 30)
    /* "src/main/bison/parser.y":337  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 31: /* factor: factor STAR summand  */
  if (yyn == 31)
    /* "src/main/bison/parser.y":339  */
      { yyval = new BinaryExpression(BinaryOperator.MULTIPLY, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 32: /* factor: factor SLASH summand  */
  if (yyn == 32)
    /* "src/main/bison/parser.y":341  */
      { yyval = new BinaryExpression(BinaryOperator.DIVIDE, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 33: /* factor: factor PERCENT summand  */
  if (yyn == 33)
    /* "src/main/bison/parser.y":343  */
      { yyval = new BinaryExpression(BinaryOperator.REMAINDER, ((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 34: /* summand: primary  */
  if (yyn == 34)
    /* "src/main/bison/parser.y":348  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 35: /* summand: PLUS summand  */
  if (yyn == 35)
    /* "src/main/bison/parser.y":350  */
      { yyval = new UnaryExpression(UnaryOperator.PLUS, ((Expression)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (1))))); };
  break;


  case 36: /* summand: MINUS summand  */
  if (yyn == 36)
    /* "src/main/bison/parser.y":352  */
      { yyval = new UnaryExpression(UnaryOperator.MINUS, ((Expression)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (1))))); };
  break;


  case 37: /* summand: NOT summand  */
  if (yyn == 37)
    /* "src/main/bison/parser.y":354  */
      { yyval = new UnaryExpression(UnaryOperator.NOT, ((Expression)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (1))))); };
  break;


  case 38: /* primary: integer_literal  */
  if (yyn == 38)
    /* "src/main/bison/parser.y":359  */
      { yyval = ((IntegerLiteral)(yystack.valueAt (0))); };
  break;


  case 39: /* primary: real_literal  */
  if (yyn == 39)
    /* "src/main/bison/parser.y":361  */
      { yyval = ((RealLiteral)(yystack.valueAt (0))); };
  break;


  case 40: /* primary: boolean_literal  */
  if (yyn == 40)
    /* "src/main/bison/parser.y":363  */
      { yyval = ((BooleanLiteral)(yystack.valueAt (0))); };
  break;


  case 41: /* primary: modifiable_primary  */
  if (yyn == 41)
    /* "src/main/bison/parser.y":365  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 42: /* primary: routine_call  */
  if (yyn == 42)
    /* "src/main/bison/parser.y":367  */
      { yyval = ((RoutineCall)(yystack.valueAt (0))); };
  break;


  case 43: /* primary: constructor_expression  */
  if (yyn == 43)
    /* "src/main/bison/parser.y":369  */
      { yyval = ((RecordConstructor)(yystack.valueAt (0))); };
  break;


  case 44: /* primary: LPAREN expression RPAREN  */
  if (yyn == 44)
    /* "src/main/bison/parser.y":371  */
      { yyval = ((Expression)(yystack.valueAt (1))); };
  break;


  case 45: /* primary: NULL  */
  if (yyn == 45)
    /* "src/main/bison/parser.y":373  */
      { yyval = new NullLiteral(position(((Token)(yystack.valueAt (0))))); };
  break;


  case 46: /* integer_literal: INTEGER_LITERAL  */
  if (yyn == 46)
    /* "src/main/bison/parser.y":378  */
      { yyval = new IntegerLiteral(((Token)(yystack.valueAt (0))).getIntValue(), position(((Token)(yystack.valueAt (0))))); };
  break;


  case 47: /* real_literal: REAL_LITERAL  */
  if (yyn == 47)
    /* "src/main/bison/parser.y":383  */
      { yyval = new RealLiteral(((Token)(yystack.valueAt (0))).getRealValue(), position(((Token)(yystack.valueAt (0))))); };
  break;


  case 48: /* boolean_literal: TRUE  */
  if (yyn == 48)
    /* "src/main/bison/parser.y":388  */
      { yyval = new BooleanLiteral(true, position(((Token)(yystack.valueAt (0))))); };
  break;


  case 49: /* boolean_literal: FALSE  */
  if (yyn == 49)
    /* "src/main/bison/parser.y":390  */
      { yyval = new BooleanLiteral(false, position(((Token)(yystack.valueAt (0))))); };
  break;


  case 50: /* modifiable_primary: IDENTIFIER  */
  if (yyn == 50)
    /* "src/main/bison/parser.y":395  */
      { yyval = new Identifier(((Token)(yystack.valueAt (0))).getStringValue(), position(((Token)(yystack.valueAt (0))))); };
  break;


  case 51: /* modifiable_primary: modifiable_primary DOT IDENTIFIER  */
  if (yyn == 51)
    /* "src/main/bison/parser.y":397  */
      { yyval = new FieldAccess(((Expression)(yystack.valueAt (2))), ((Token)(yystack.valueAt (0))).getStringValue(), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 52: /* modifiable_primary: modifiable_primary LBRACKET expression RBRACKET  */
  if (yyn == 52)
    /* "src/main/bison/parser.y":399  */
      { yyval = new IndexAccess(((Expression)(yystack.valueAt (3))), ((Expression)(yystack.valueAt (1))), ((Expression)(yystack.valueAt (3))).position()); };
  break;


  case 53: /* routine_call: IDENTIFIER LPAREN optional_newlines optional_arguments RPAREN  */
  if (yyn == 53)
    /* "src/main/bison/parser.y":404  */
      { yyval = new RoutineCall(((Token)(yystack.valueAt (4))).getStringValue(), ((ExpressionList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (4))))); };
  break;


  case 54: /* optional_arguments: %empty  */
  if (yyn == 54)
    /* "src/main/bison/parser.y":409  */
      { yyval = new ExpressionList(); };
  break;


  case 55: /* optional_arguments: argument_list optional_newlines  */
  if (yyn == 55)
    /* "src/main/bison/parser.y":411  */
      { yyval = ((ExpressionList)(yystack.valueAt (1))); };
  break;


  case 56: /* argument_list: expression argument_list_tail  */
  if (yyn == 56)
    /* "src/main/bison/parser.y":416  */
      { ((ExpressionList)(yystack.valueAt (0))).addFirst(((Expression)(yystack.valueAt (1)))); yyval = ((ExpressionList)(yystack.valueAt (0))); };
  break;


  case 57: /* argument_list_tail: %empty  */
  if (yyn == 57)
    /* "src/main/bison/parser.y":421  */
      { yyval = new ExpressionList(); };
  break;


  case 58: /* argument_list_tail: COMMA optional_newlines expression argument_list_tail  */
  if (yyn == 58)
    /* "src/main/bison/parser.y":423  */
      { ((ExpressionList)(yystack.valueAt (0))).addFirst(((Expression)(yystack.valueAt (1)))); yyval = ((ExpressionList)(yystack.valueAt (0))); };
  break;


  case 59: /* constructor_expression: NEW IDENTIFIER LPAREN optional_newlines optional_members RPAREN  */
  if (yyn == 59)
    /* "src/main/bison/parser.y":428  */
      { yyval = new RecordConstructor(((Token)(yystack.valueAt (4))).getStringValue(), ((MemberInitializationList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (5))))); };
  break;


  case 60: /* optional_members: %empty  */
  if (yyn == 60)
    /* "src/main/bison/parser.y":433  */
      { yyval = new MemberInitializationList(); };
  break;


  case 61: /* optional_members: member_initialization_list optional_newlines  */
  if (yyn == 61)
    /* "src/main/bison/parser.y":435  */
      { yyval = ((MemberInitializationList)(yystack.valueAt (1))); };
  break;


  case 62: /* member_initialization_list: member_initialization member_initialization_list_tail  */
  if (yyn == 62)
    /* "src/main/bison/parser.y":440  */
      { ((MemberInitializationList)(yystack.valueAt (0))).addFirst(((MemberInitialization)(yystack.valueAt (1)))); yyval = ((MemberInitializationList)(yystack.valueAt (0))); };
  break;


  case 63: /* member_initialization_list_tail: %empty  */
  if (yyn == 63)
    /* "src/main/bison/parser.y":445  */
      { yyval = new MemberInitializationList(); };
  break;


  case 64: /* member_initialization_list_tail: COMMA optional_newlines member_initialization member_initialization_list_tail  */
  if (yyn == 64)
    /* "src/main/bison/parser.y":447  */
      { ((MemberInitializationList)(yystack.valueAt (0))).addFirst(((MemberInitialization)(yystack.valueAt (1)))); yyval = ((MemberInitializationList)(yystack.valueAt (0))); };
  break;


  case 65: /* member_initialization: IDENTIFIER IS expression  */
  if (yyn == 65)
    /* "src/main/bison/parser.y":452  */
      { yyval = new MemberInitialization(((Token)(yystack.valueAt (2))).getStringValue(), ((Expression)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (2))))); };
  break;


  case 66: /* optional_initializer: %empty  */
  if (yyn == 66)
    /* "src/main/bison/parser.y":457  */
      { yyval = null; };
  break;


  case 67: /* optional_initializer: IS expression  */
  if (yyn == 67)
    /* "src/main/bison/parser.y":459  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 68: /* type: primitive_type  */
  if (yyn == 68)
    /* "src/main/bison/parser.y":472  */
      { yyval = ((PrimitiveType)(yystack.valueAt (0))); };
  break;


  case 69: /* type: user_type  */
  if (yyn == 69)
    /* "src/main/bison/parser.y":474  */
      { yyval = ((UserType)(yystack.valueAt (0))); };
  break;


  case 70: /* type: named_type  */
  if (yyn == 70)
    /* "src/main/bison/parser.y":476  */
      { yyval = ((NamedType)(yystack.valueAt (0))); };
  break;


  case 71: /* primitive_type: INTEGER  */
  if (yyn == 71)
    /* "src/main/bison/parser.y":481  */
      { yyval = new PrimitiveType(PrimitiveKind.INTEGER, position(((Token)(yystack.valueAt (0))))); };
  break;


  case 72: /* primitive_type: REAL  */
  if (yyn == 72)
    /* "src/main/bison/parser.y":483  */
      { yyval = new PrimitiveType(PrimitiveKind.REAL, position(((Token)(yystack.valueAt (0))))); };
  break;


  case 73: /* primitive_type: BOOLEAN  */
  if (yyn == 73)
    /* "src/main/bison/parser.y":485  */
      { yyval = new PrimitiveType(PrimitiveKind.BOOLEAN, position(((Token)(yystack.valueAt (0))))); };
  break;


  case 74: /* user_type: array_type  */
  if (yyn == 74)
    /* "src/main/bison/parser.y":490  */
      { yyval = ((ArrayType)(yystack.valueAt (0))); };
  break;


  case 75: /* user_type: record_type  */
  if (yyn == 75)
    /* "src/main/bison/parser.y":492  */
      { yyval = ((RecordType)(yystack.valueAt (0))); };
  break;


  case 76: /* array_type: ARRAY LBRACKET optional_array_size RBRACKET type  */
  if (yyn == 76)
    /* "src/main/bison/parser.y":497  */
      { yyval = new ArrayType(Optional.ofNullable(((Expression)(yystack.valueAt (2)))), ((TypeNode)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (4))))); };
  break;


  case 77: /* optional_array_size: %empty  */
  if (yyn == 77)
    /* "src/main/bison/parser.y":502  */
      { yyval = null; };
  break;


  case 78: /* optional_array_size: expression  */
  if (yyn == 78)
    /* "src/main/bison/parser.y":504  */
      { yyval = ((Expression)(yystack.valueAt (0))); };
  break;


  case 79: /* record_type: RECORD optional_separators END  */
  if (yyn == 79)
    /* "src/main/bison/parser.y":509  */
      { yyval = new RecordType(List.of(), position(((Token)(yystack.valueAt (2))))); };
  break;


  case 80: /* record_type: RECORD optional_separators record_member_sequence END  */
  if (yyn == 80)
    /* "src/main/bison/parser.y":511  */
      { yyval = new RecordType(((VariableDeclarationList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (3))))); };
  break;


  case 81: /* record_member_sequence: variable_declaration optional_separators  */
  if (yyn == 81)
    /* "src/main/bison/parser.y":516  */
      {
        VariableDeclarationList fields = new VariableDeclarationList();
        fields.addFirst(((VariableDeclaration)(yystack.valueAt (1))));
        yyval = fields;
      };
  break;


  case 82: /* record_member_sequence: variable_declaration separators record_member_sequence  */
  if (yyn == 82)
    /* "src/main/bison/parser.y":522  */
      { ((VariableDeclarationList)(yystack.valueAt (0))).addFirst(((VariableDeclaration)(yystack.valueAt (2)))); yyval = ((VariableDeclarationList)(yystack.valueAt (0))); };
  break;


  case 83: /* named_type: IDENTIFIER  */
  if (yyn == 83)
    /* "src/main/bison/parser.y":527  */
      { yyval = new NamedType(((Token)(yystack.valueAt (0))).getStringValue(), position(((Token)(yystack.valueAt (0))))); };
  break;


  case 84: /* statement: assignment  */
  if (yyn == 84)
    /* "src/main/bison/parser.y":540  */
      { yyval = ((Assignment)(yystack.valueAt (0))); };
  break;


  case 85: /* statement: routine_call  */
  if (yyn == 85)
    /* "src/main/bison/parser.y":542  */
      { yyval = new RoutineCallStatement(((RoutineCall)(yystack.valueAt (0))), ((RoutineCall)(yystack.valueAt (0))).position()); };
  break;


  case 86: /* statement: while_loop  */
  if (yyn == 86)
    /* "src/main/bison/parser.y":544  */
      { yyval = ((WhileLoop)(yystack.valueAt (0))); };
  break;


  case 87: /* statement: for_loop  */
  if (yyn == 87)
    /* "src/main/bison/parser.y":546  */
      { yyval = ((ForLoop)(yystack.valueAt (0))); };
  break;


  case 88: /* statement: if_statement  */
  if (yyn == 88)
    /* "src/main/bison/parser.y":548  */
      { yyval = ((IfStatement)(yystack.valueAt (0))); };
  break;


  case 89: /* statement: print_statement  */
  if (yyn == 89)
    /* "src/main/bison/parser.y":550  */
      { yyval = ((PrintStatement)(yystack.valueAt (0))); };
  break;


  case 90: /* statement: return_statement  */
  if (yyn == 90)
    /* "src/main/bison/parser.y":552  */
      { yyval = ((ReturnStatement)(yystack.valueAt (0))); };
  break;


  case 91: /* assignment: modifiable_primary ASSIGN expression  */
  if (yyn == 91)
    /* "src/main/bison/parser.y":557  */
      { yyval = new Assignment(((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 92: /* while_loop: WHILE expression LOOP body END  */
  if (yyn == 92)
    /* "src/main/bison/parser.y":562  */
      { yyval = new WhileLoop(((Expression)(yystack.valueAt (3))), new Block(((BlockItemList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (2))))), position(((Token)(yystack.valueAt (4))))); };
  break;


  case 93: /* body: optional_separators  */
  if (yyn == 93)
    /* "src/main/bison/parser.y":567  */
      { yyval = new BlockItemList(); };
  break;


  case 94: /* body: optional_separators body_sequence  */
  if (yyn == 94)
    /* "src/main/bison/parser.y":569  */
      { yyval = ((BlockItemList)(yystack.valueAt (0))); };
  break;


  case 95: /* body_sequence: body_item optional_separators  */
  if (yyn == 95)
    /* "src/main/bison/parser.y":574  */
      {
        BlockItemList items = new BlockItemList();
        items.addFirst(((BlockItem)(yystack.valueAt (1))));
        yyval = items;
      };
  break;


  case 96: /* body_sequence: body_item separators body_sequence  */
  if (yyn == 96)
    /* "src/main/bison/parser.y":580  */
      { ((BlockItemList)(yystack.valueAt (0))).addFirst(((BlockItem)(yystack.valueAt (2)))); yyval = ((BlockItemList)(yystack.valueAt (0))); };
  break;


  case 97: /* body_item: simple_declaration  */
  if (yyn == 97)
    /* "src/main/bison/parser.y":585  */
      { yyval = ((SimpleDeclaration)(yystack.valueAt (0))); };
  break;


  case 98: /* body_item: statement  */
  if (yyn == 98)
    /* "src/main/bison/parser.y":587  */
      { yyval = ((Statement)(yystack.valueAt (0))); };
  break;


  case 99: /* for_loop: FOR IDENTIFIER IN range optional_reverse LOOP body END  */
  if (yyn == 99)
    /* "src/main/bison/parser.y":592  */
      {
        yyval = new ForLoop(
            ((Token)(yystack.valueAt (6))).getStringValue(), ((IterationSource)(yystack.valueAt (4))), ((Boolean)(yystack.valueAt (3))), new Block(((BlockItemList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (2))))), position(((Token)(yystack.valueAt (7))))
        );
      };
  break;


  case 100: /* range: expression  */
  if (yyn == 100)
    /* "src/main/bison/parser.y":601  */
      { yyval = new ExpressionSource(((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (0))).position()); };
  break;


  case 101: /* range: expression DOTDOT expression  */
  if (yyn == 101)
    /* "src/main/bison/parser.y":603  */
      { yyval = new RangeSource(((Expression)(yystack.valueAt (2))), ((Expression)(yystack.valueAt (0))), ((Expression)(yystack.valueAt (2))).position()); };
  break;


  case 102: /* optional_reverse: %empty  */
  if (yyn == 102)
    /* "src/main/bison/parser.y":608  */
      { yyval = false; };
  break;


  case 103: /* optional_reverse: REVERSE  */
  if (yyn == 103)
    /* "src/main/bison/parser.y":610  */
      { yyval = true; };
  break;


  case 104: /* if_statement: IF expression THEN body optional_else END  */
  if (yyn == 104)
    /* "src/main/bison/parser.y":615  */
      {
        yyval = new IfStatement(
            ((Expression)(yystack.valueAt (4))), new Block(((BlockItemList)(yystack.valueAt (2))).values(), position(((Token)(yystack.valueAt (3))))), Optional.ofNullable(((Block)(yystack.valueAt (1)))), position(((Token)(yystack.valueAt (5))))
        );
      };
  break;


  case 105: /* optional_else: %empty  */
  if (yyn == 105)
    /* "src/main/bison/parser.y":624  */
      { yyval = null; };
  break;


  case 106: /* optional_else: ELSE body  */
  if (yyn == 106)
    /* "src/main/bison/parser.y":626  */
      { yyval = new Block(((BlockItemList)(yystack.valueAt (0))).values(), position(((Token)(yystack.valueAt (1))))); };
  break;


  case 107: /* print_statement: PRINT optional_newlines argument_list  */
  if (yyn == 107)
    /* "src/main/bison/parser.y":631  */
      { yyval = new PrintStatement(((ExpressionList)(yystack.valueAt (0))).values(), position(((Token)(yystack.valueAt (2))))); };
  break;


  case 108: /* return_statement: RETURN  */
  if (yyn == 108)
    /* "src/main/bison/parser.y":636  */
      { yyval = new ReturnStatement(Optional.empty(), position(((Token)(yystack.valueAt (0))))); };
  break;


  case 109: /* return_statement: RETURN expression  */
  if (yyn == 109)
    /* "src/main/bison/parser.y":638  */
      { yyval = new ReturnStatement(Optional.of(((Expression)(yystack.valueAt (0)))), position(((Token)(yystack.valueAt (1))))); };
  break;


  case 110: /* routine_declaration: routine_header  */
  if (yyn == 110)
    /* "src/main/bison/parser.y":651  */
      {
        yyval = new RoutineDeclaration(
            ((RoutineHeader)(yystack.valueAt (0))).name(), ((RoutineHeader)(yystack.valueAt (0))).parameters(), ((RoutineHeader)(yystack.valueAt (0))).returnType(), Optional.empty(), ((RoutineHeader)(yystack.valueAt (0))).position()
        );
      };
  break;


  case 111: /* routine_declaration: routine_header routine_body  */
  if (yyn == 111)
    /* "src/main/bison/parser.y":657  */
      {
        yyval = new RoutineDeclaration(
            ((RoutineHeader)(yystack.valueAt (1))).name(), ((RoutineHeader)(yystack.valueAt (1))).parameters(), ((RoutineHeader)(yystack.valueAt (1))).returnType(), Optional.of(((RoutineBody)(yystack.valueAt (0)))), ((RoutineHeader)(yystack.valueAt (1))).position()
        );
      };
  break;


  case 112: /* routine_header: ROUTINE IDENTIFIER LPAREN optional_parameters RPAREN optional_return_type  */
  if (yyn == 112)
    /* "src/main/bison/parser.y":666  */
      {
        yyval = new RoutineHeader(
            ((Token)(yystack.valueAt (4))).getStringValue(), ((ParameterList)(yystack.valueAt (2))).values(), Optional.ofNullable(((TypeNode)(yystack.valueAt (0)))), position(((Token)(yystack.valueAt (5))))
        );
      };
  break;


  case 113: /* optional_parameters: %empty  */
  if (yyn == 113)
    /* "src/main/bison/parser.y":675  */
      { yyval = new ParameterList(); };
  break;


  case 114: /* optional_parameters: parameter_list  */
  if (yyn == 114)
    /* "src/main/bison/parser.y":677  */
      { yyval = ((ParameterList)(yystack.valueAt (0))); };
  break;


  case 115: /* parameter_list: parameter  */
  if (yyn == 115)
    /* "src/main/bison/parser.y":682  */
      {
        ParameterList parameters = new ParameterList();
        parameters.addLast(((Parameter)(yystack.valueAt (0))));
        yyval = parameters;
      };
  break;


  case 116: /* parameter_list: parameter_list COMMA parameter  */
  if (yyn == 116)
    /* "src/main/bison/parser.y":688  */
      { ((ParameterList)(yystack.valueAt (2))).addLast(((Parameter)(yystack.valueAt (0)))); yyval = ((ParameterList)(yystack.valueAt (2))); };
  break;


  case 117: /* parameter: IDENTIFIER COLON type  */
  if (yyn == 117)
    /* "src/main/bison/parser.y":693  */
      { yyval = new Parameter(((Token)(yystack.valueAt (2))).getStringValue(), ((TypeNode)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (2))))); };
  break;


  case 118: /* optional_return_type: %empty  */
  if (yyn == 118)
    /* "src/main/bison/parser.y":698  */
      { yyval = null; };
  break;


  case 119: /* optional_return_type: COLON type  */
  if (yyn == 119)
    /* "src/main/bison/parser.y":700  */
      { yyval = ((TypeNode)(yystack.valueAt (0))); };
  break;


  case 120: /* routine_body: IS body END  */
  if (yyn == 120)
    /* "src/main/bison/parser.y":705  */
      { yyval = new BlockRoutineBody(new Block(((BlockItemList)(yystack.valueAt (1))).values(), position(((Token)(yystack.valueAt (2))))), position(((Token)(yystack.valueAt (2))))); };
  break;


  case 121: /* routine_body: RETIMM expression  */
  if (yyn == 121)
    /* "src/main/bison/parser.y":707  */
      { yyval = new ExpressionRoutineBody(((Expression)(yystack.valueAt (0))), position(((Token)(yystack.valueAt (1))))); };
  break;



/* "src/main/java/com/compiler/parser/Parser.java":1646  */

        default: break;
      }

    yystack.pop(yylen);
    yylen = 0;
    /* Shift the result of the reduction.  */
    int yystate = yyLRGotoState(yystack.stateAt(0), yyr1_[yyn]);
    yystack.push(yystate, yyval);
    return YYNEWSTATE;
  }




  /**
   * Parse input from the scanner that was specified at object construction
   * time.  Return whether the end of the input was reached successfully.
   *
   * @return <tt>true</tt> if the parsing succeeds.  Note that this does not
   *          imply that there were no syntax errors.
   */
  public boolean parse() throws java.io.IOException

  {


    /* Lookahead token kind.  */
    int yychar = YYEMPTY_;
    /* Lookahead symbol kind.  */
    SymbolKind yytoken = null;

    /* State.  */
    int yyn = 0;
    int yylen = 0;
    int yystate = 0;
    YYStack yystack = new YYStack ();
    int label = YYNEWSTATE;



    /* Semantic value of the lookahead.  */
    Object yylval = null;



    yyerrstatus_ = 0;
    yynerrs = 0;

    /* Initialize the stack.  */
    yystack.push (yystate, yylval);



    for (;;)
      switch (label)
      {
        /* New state.  Unlike in the C/C++ skeletons, the state is already
           pushed when we come here.  */
      case YYNEWSTATE:

        /* Accept?  */
        if (yystate == YYFINAL_)
          return true;

        /* Take a decision.  First try without lookahead.  */
        yyn = yypact_[yystate];
        if (yyPactValueIsDefault (yyn))
          {
            label = YYDEFAULT;
            break;
          }

        /* Read a lookahead token.  */
        if (yychar == YYEMPTY_)
          {

            yychar = yylexer.yylex ();
            yylval = yylexer.getLVal();

          }

        /* Convert token to internal form.  */
        yytoken = yytranslate_ (yychar);

        if (yytoken == SymbolKind.S_YYerror)
          {
            // The scanner already issued an error message, process directly
            // to error recovery.  But do not keep the error token as
            // lookahead, it is too special and may lead us to an endless
            // loop in error recovery. */
            yychar = Lexer.YYUNDEF;
            yytoken = SymbolKind.S_YYUNDEF;
            label = YYERRLAB1;
          }
        else
          {
            /* If the proper action on seeing token YYTOKEN is to reduce or to
               detect an error, take that action.  */
            yyn += yytoken.getCode();
            if (yyn < 0 || YYLAST_ < yyn || yycheck_[yyn] != yytoken.getCode()) {
              label = YYDEFAULT;
            }

            /* <= 0 means reduce or error.  */
            else if ((yyn = yytable_[yyn]) <= 0)
              {
                if (yyTableValueIsError(yyn)) {
                  label = YYERRLAB;
                } else {
                  yyn = -yyn;
                  label = YYREDUCE;
                }
              }

            else
              {
                /* Shift the lookahead token.  */
                /* Discard the token being shifted.  */
                yychar = YYEMPTY_;

                /* Count tokens shifted since error; after three, turn off error
                   status.  */
                if (yyerrstatus_ > 0)
                  --yyerrstatus_;

                yystate = yyn;
                yystack.push(yystate, yylval);
                label = YYNEWSTATE;
              }
          }
        break;

      /*-----------------------------------------------------------.
      | yydefault -- do the default action for the current state.  |
      `-----------------------------------------------------------*/
      case YYDEFAULT:
        yyn = yydefact_[yystate];
        if (yyn == 0)
          label = YYERRLAB;
        else
          label = YYREDUCE;
        break;

      /*-----------------------------.
      | yyreduce -- Do a reduction.  |
      `-----------------------------*/
      case YYREDUCE:
        yylen = yyr2_[yyn];
        label = yyaction(yyn, yystack, yylen);
        yystate = yystack.stateAt(0);
        break;

      /*------------------------------------.
      | yyerrlab -- here on detecting error |
      `------------------------------------*/
      case YYERRLAB:
        /* If not already recovering from an error, report this error.  */
        if (yyerrstatus_ == 0)
          {
            ++yynerrs;
            if (yychar == YYEMPTY_)
              yytoken = null;
            yyreportSyntaxError(new Context(this, yystack, yytoken));
          }

        if (yyerrstatus_ == 3)
          {
            /* If just tried and failed to reuse lookahead token after an
               error, discard it.  */

            if (yychar <= Lexer.YYEOF)
              {
                /* Return failure if at end of input.  */
                if (yychar == Lexer.YYEOF)
                  return false;
              }
            else
              yychar = YYEMPTY_;
          }

        /* Else will try to reuse lookahead token after shifting the error
           token.  */
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------.
      | errorlab -- error raised explicitly by YYERROR.  |
      `-------------------------------------------------*/
      case YYERROR:
        /* Do not reclaim the symbols of the rule which action triggered
           this YYERROR.  */
        yystack.pop (yylen);
        yylen = 0;
        yystate = yystack.stateAt(0);
        label = YYERRLAB1;
        break;

      /*-------------------------------------------------------------.
      | yyerrlab1 -- common code for both syntax error and YYERROR.  |
      `-------------------------------------------------------------*/
      case YYERRLAB1:
        yyerrstatus_ = 3;       /* Each real token shifted decrements this.  */

        // Pop stack until we find a state that shifts the error token.
        for (;;)
          {
            yyn = yypact_[yystate];
            if (!yyPactValueIsDefault (yyn))
              {
                yyn += SymbolKind.S_YYerror.getCode();
                if (0 <= yyn && yyn <= YYLAST_
                    && yycheck_[yyn] == SymbolKind.S_YYerror.getCode())
                  {
                    yyn = yytable_[yyn];
                    if (0 < yyn)
                      break;
                  }
              }

            /* Pop the current state because it cannot handle the
             * error token.  */
            if (yystack.height == 0)
              return false;


            yystack.pop ();
            yystate = yystack.stateAt(0);
          }

        if (label == YYABORT)
          /* Leave the switch.  */
          break;



        /* Shift the error token.  */

        yystate = yyn;
        yystack.push (yyn, yylval);
        label = YYNEWSTATE;
        break;

        /* Accept.  */
      case YYACCEPT:
        return true;

        /* Abort.  */
      case YYABORT:
        return false;
      }
}




  /**
   * Information needed to get the list of expected tokens and to forge
   * a syntax error diagnostic.
   */
  public static final class Context {
    Context(Parser parser, YYStack stack, SymbolKind token) {
      yyparser = parser;
      yystack = stack;
      yytoken = token;
    }

    private Parser yyparser;
    private YYStack yystack;


    /**
     * The symbol kind of the lookahead token.
     */
    public final SymbolKind getToken() {
      return yytoken;
    }

    private SymbolKind yytoken;
    static final int NTOKENS = Parser.YYNTOKENS_;

    /**
     * Put in YYARG at most YYARGN of the expected tokens given the
     * current YYCTX, and return the number of tokens stored in YYARG.  If
     * YYARG is null, return the number of expected tokens (guaranteed to
     * be less than YYNTOKENS).
     */
    int getExpectedTokens(SymbolKind yyarg[], int yyargn) {
      return getExpectedTokens (yyarg, 0, yyargn);
    }

    int getExpectedTokens(SymbolKind yyarg[], int yyoffset, int yyargn) {
      int yycount = yyoffset;
      int yyn = yypact_[this.yystack.stateAt(0)];
      if (!yyPactValueIsDefault(yyn))
        {
          /* Start YYX at -YYN if negative to avoid negative
             indexes in YYCHECK.  In other words, skip the first
             -YYN actions for this state because they are default
             actions.  */
          int yyxbegin = yyn < 0 ? -yyn : 0;
          /* Stay within bounds of both yycheck and yytname.  */
          int yychecklim = YYLAST_ - yyn + 1;
          int yyxend = yychecklim < NTOKENS ? yychecklim : NTOKENS;
          for (int yyx = yyxbegin; yyx < yyxend; ++yyx)
            if (yycheck_[yyx + yyn] == yyx && yyx != SymbolKind.S_YYerror.getCode()
                && !yyTableValueIsError(yytable_[yyx + yyn]))
              {
                if (yyarg == null)
                  yycount += 1;
                else if (yycount == yyargn)
                  return 0; // FIXME: this is incorrect.
                else
                  yyarg[yycount++] = SymbolKind.get(yyx);
              }
        }
      if (yyarg != null && yycount == yyoffset && yyoffset < yyargn)
        yyarg[yycount] = null;
      return yycount - yyoffset;
    }
  }




  private int yysyntaxErrorArguments(Context yyctx, SymbolKind[] yyarg, int yyargn) {
    /* There are many possibilities here to consider:
       - If this state is a consistent state with a default action,
         then the only way this function was invoked is if the
         default action is an error action.  In that case, don't
         check for expected tokens because there are none.
       - The only way there can be no lookahead present (in tok) is
         if this state is a consistent state with a default action.
         Thus, detecting the absence of a lookahead is sufficient to
         determine that there is no unexpected or expected token to
         report.  In that case, just report a simple "syntax error".
       - Don't assume there isn't a lookahead just because this
         state is a consistent state with a default action.  There
         might have been a previous inconsistent state, consistent
         state with a non-default action, or user semantic action
         that manipulated yychar.  (However, yychar is currently out
         of scope during semantic actions.)
       - Of course, the expected token list depends on states to
         have correct lookahead information, and it depends on the
         parser not to perform extra reductions after fetching a
         lookahead from the scanner and before detecting a syntax
         error.  Thus, state merging (from LALR or IELR) and default
         reductions corrupt the expected token list.  However, the
         list is correct for canonical LR with one exception: it
         will still contain any token that will not be accepted due
         to an error action in a later state.
    */
    int yycount = 0;
    if (yyctx.getToken() != null)
      {
        if (yyarg != null)
          yyarg[yycount] = yyctx.getToken();
        yycount += 1;
        yycount += yyctx.getExpectedTokens(yyarg, 1, yyargn);
      }
    return yycount;
  }


  /**
   * Build and emit a "syntax error" message in a user-defined way.
   *
   * @param ctx  The context of the error.
   */
  private void yyreportSyntaxError(Context yyctx) {
      if (yyErrorVerbose) {
          final int argmax = 5;
          SymbolKind[] yyarg = new SymbolKind[argmax];
          int yycount = yysyntaxErrorArguments(yyctx, yyarg, argmax);
          String[] yystr = new String[yycount];
          for (int yyi = 0; yyi < yycount; ++yyi) {
              yystr[yyi] = yyarg[yyi].getName();
          }
          String yyformat;
          switch (yycount) {
              default:
              case 0: yyformat = "syntax error"; break;
              case 1: yyformat = "syntax error, unexpected {0}"; break;
              case 2: yyformat = "syntax error, unexpected {0}, expecting {1}"; break;
              case 3: yyformat = "syntax error, unexpected {0}, expecting {1} or {2}"; break;
              case 4: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3}"; break;
              case 5: yyformat = "syntax error, unexpected {0}, expecting {1} or {2} or {3} or {4}"; break;
          }
          yyerror(new MessageFormat(yyformat).format(yystr));
      } else {
          yyerror("syntax error");
      }
  }

  /**
   * Whether the given <code>yypact_</code> value indicates a defaulted state.
   * @param yyvalue   the value to check
   */
  private static boolean yyPactValueIsDefault(int yyvalue) {
    return yyvalue == yypact_ninf_;
  }

  /**
   * Whether the given <code>yytable_</code>
   * value indicates a syntax error.
   * @param yyvalue the value to check
   */
  private static boolean yyTableValueIsError(int yyvalue) {
    return yyvalue == yytable_ninf_;
  }

  private static final short yypact_ninf_ = -139;
  private static final short yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
      51,  -139,  -139,    58,    76,    51,  -139,  -139,     1,    41,
      52,  -139,    51,  -139,  -139,  -139,  -139,    14,  -139,    42,
       9,    87,  -139,     8,    51,    89,  -139,    85,    89,   107,
     107,  -139,   109,    31,  -139,  -139,  -139,    89,    47,  -139,
    -139,    89,    89,    89,   101,    90,   104,   120,  -139,   100,
      66,  -139,  -139,  -139,  -139,  -139,    27,  -139,  -139,   103,
      83,    97,  -139,    90,    51,   102,  -139,  -139,  -139,  -139,
     145,  -139,  -139,  -139,  -139,  -139,  -139,  -139,    89,    89,
     122,    89,    99,  -139,    36,  -139,  -139,  -139,  -139,  -139,
      51,  -139,  -139,  -139,  -139,  -139,    99,  -139,  -139,    23,
     106,    89,    89,    89,    89,    89,  -139,  -139,  -139,  -139,
    -139,  -139,    89,    89,    89,    89,   126,    89,   107,   111,
      85,    26,    89,    89,  -139,    61,    45,   146,    90,    99,
      89,  -139,    89,  -139,    21,    89,  -139,    99,   104,   120,
    -139,    66,    66,     6,  -139,  -139,  -139,  -139,    -3,  -139,
     107,  -139,  -139,  -139,    51,   152,    90,   110,    90,    51,
      51,    89,  -139,   -10,  -139,    90,  -139,   113,    99,   131,
    -139,  -139,  -139,    10,  -139,   107,   154,   158,    18,   151,
      99,  -139,  -139,  -139,   161,   123,    99,   117,  -139,  -139,
      51,   167,  -139,    89,  -139,   163,    89,    89,  -139,  -139,
      99,  -139,  -139,  -139,    90,    51,   -10,    90,   131,   169,
    -139,   117,  -139,  -139
    };
  }

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
  private static final short[] yydefact_ = yydefact_init();
  private static final short[] yydefact_init()
  {
    return new short[]
    {
     122,   127,   126,     0,     2,   123,   124,     1,     0,     0,
       0,     3,   122,     6,     8,     9,     7,   110,   125,     0,
       0,     0,     4,   123,   122,     0,   111,   113,     0,     0,
       0,     5,     0,    93,    48,    49,    45,     0,    50,    46,
      47,     0,     0,     0,     0,   121,    13,    15,    17,    19,
      27,    30,    34,    38,    39,    40,    41,    42,    43,     0,
       0,   114,   115,    10,   122,     0,    71,    72,    73,    83,
      66,    68,    69,    74,    75,    70,    12,   120,     0,     0,
       0,   108,   128,    97,     0,    85,    98,    84,    86,    94,
     122,    87,    88,    89,    90,    37,   128,    35,    36,     0,
       0,     0,     0,     0,     0,     0,    25,    26,    21,    22,
      23,    24,     0,     0,     0,     0,     0,     0,     0,   118,
       0,     0,    77,     0,    11,     0,     0,     0,   109,   130,
       0,   129,     0,    95,   123,    54,    44,   128,    14,    16,
      18,    28,    29,    20,    31,    32,    33,    51,     0,   117,
       0,   112,   116,    79,   122,     0,    78,     0,    67,   122,
     122,     0,   131,    57,   107,    91,    96,     0,   128,    60,
      52,   119,    81,   123,    80,     0,   105,     0,   100,   102,
     128,    56,    53,    55,     0,     0,   128,    63,    82,    76,
     122,     0,    92,     0,   103,     0,     0,     0,    59,    61,
     128,    62,   106,   104,   101,   122,    57,    65,     0,     0,
      58,    63,    99,    64
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -139,  -139,   155,  -139,   -26,  -113,  -139,   -25,    78,    75,
      77,  -139,    69,   -19,   -14,  -139,  -139,  -139,  -139,   -24,
     -23,  -139,    48,   -22,  -139,  -139,  -139,   -29,   -21,  -139,
     -28,  -139,  -139,  -139,  -139,  -139,    12,  -139,  -139,  -139,
    -139,  -138,    54,  -139,  -139,  -139,  -139,  -139,  -139,  -139,
    -139,  -139,  -139,  -139,  -139,    70,  -139,  -139,     4,    -6,
      -4,   -91,    57
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     3,    11,    12,    13,    14,    15,   163,    46,    47,
      48,   112,    49,    50,    51,    52,    53,    54,    55,    56,
      57,   167,   164,   181,    58,   185,   186,   201,   187,   124,
      70,    71,    72,    73,   157,    74,   155,    75,    86,    87,
      88,    32,    89,    90,    91,   179,   195,    92,   191,    93,
      94,    16,    17,    60,    61,    62,   151,    26,    33,     5,
       6,   130,   131
    };
  }

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
  private static final short[] yytable_ = yytable_init();
  private static final short[] yytable_init()
  {
    return new short[]
    {
      45,    18,    76,    63,     4,   135,    23,    83,   154,    84,
      85,     8,     9,    10,     9,    28,    22,   101,    99,    18,
      24,   176,   177,    95,   101,     9,    10,    97,    98,    78,
       9,    19,    79,   153,    80,     9,    10,    81,    82,    78,
     104,   105,    79,   180,    80,   101,   169,    81,    82,   170,
     101,    38,   202,   125,   126,    29,   128,   160,     7,    25,
     154,    38,     1,     2,     1,     2,   193,   209,   121,   132,
     159,    20,   101,   136,   116,     1,     2,   183,   117,     8,
       9,    10,    21,   116,   134,   141,   142,   117,   101,   196,
     149,    27,   148,    30,   133,   199,    96,   156,   158,   144,
     145,   146,   113,   114,   115,     1,     2,   165,    83,   208,
      84,    85,    34,    35,    36,    59,    77,   101,    37,    38,
      39,    40,   171,    41,    42,    64,    65,    66,    67,    68,
      18,   100,   102,   119,   104,   105,   178,    69,    43,   106,
     107,   108,   109,   110,   111,    44,   103,   189,   173,   118,
     120,   123,   127,   122,   129,   137,   147,   150,   172,   174,
     161,   184,   175,   182,   190,   192,   194,   197,   204,    18,
     200,   206,   207,   198,   203,   205,   212,   139,    31,   138,
     140,   143,   213,   168,   210,   188,   162,   211,   166,     0,
     152
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
      25,     5,    30,    28,     0,    96,    12,    33,   121,    33,
      33,     3,     4,     5,     4,     6,    12,    27,    43,    23,
       6,   159,   160,    37,    27,     4,     5,    41,    42,     8,
       4,    30,    11,     7,    13,     4,     5,    16,    17,     8,
      34,    35,    11,    53,    13,    27,   137,    16,    17,    52,
      27,    30,   190,    78,    79,    46,    81,    12,     0,    45,
     173,    30,    54,    55,    54,    55,    48,   205,    64,    33,
       9,    30,    27,    50,    47,    54,    55,   168,    51,     3,
       4,     5,    30,    47,    90,   104,   105,    51,    27,   180,
     118,    49,   117,     6,    90,   186,    49,   122,   123,   113,
     114,   115,    36,    37,    38,    54,    55,   132,   134,   200,
     134,   134,    23,    24,    25,    30,     7,    27,    29,    30,
      31,    32,   150,    34,    35,    18,    19,    20,    21,    22,
     134,    30,    28,    50,    34,    35,   161,    30,    49,    39,
      40,    41,    42,    43,    44,    56,    26,   175,   154,    46,
      53,     6,    30,    51,    55,    49,    30,    46,   154,     7,
      14,    30,    52,    50,    10,     7,    15,     6,   193,   173,
      53,   196,   197,    50,     7,    12,     7,   102,    23,   101,
     103,   112,   211,   135,   206,   173,   129,   208,   134,    -1,
     120
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,    54,    55,    58,   115,   116,   117,     0,     3,     4,
       5,    59,    60,    61,    62,    63,   108,   109,   117,    30,
      30,    30,   115,   116,     6,    45,   114,    49,     6,    46,
       6,    59,    98,   115,    23,    24,    25,    29,    30,    31,
      32,    34,    35,    49,    56,    64,    65,    66,    67,    69,
      70,    71,    72,    73,    74,    75,    76,    77,    81,    30,
     110,   111,   112,    64,    18,    19,    20,    21,    22,    30,
      87,    88,    89,    90,    92,    94,    87,     7,     8,    11,
      13,    16,    17,    61,    76,    77,    95,    96,    97,    99,
     100,   101,   104,   106,   107,    71,    49,    71,    71,    64,
      30,    27,    28,    26,    34,    35,    39,    40,    41,    42,
      43,    44,    68,    36,    37,    38,    47,    51,    46,    50,
      53,   115,    51,     6,    86,    64,    64,    30,    64,    55,
     118,   119,    33,   115,   116,   118,    50,    49,    65,    66,
      67,    70,    70,    69,    71,    71,    71,    30,    64,    87,
      46,   113,   112,     7,    62,    93,    64,    91,    64,     9,
      12,    14,   119,    64,    79,    64,    99,    78,    79,   118,
      52,    87,   115,   116,     7,    52,    98,    98,    64,   102,
      53,    80,    50,   118,    30,    82,    83,    85,    93,    87,
      10,   105,     7,    48,    15,   103,   118,     6,    50,   118,
      53,    84,    98,     7,    64,    12,    64,    64,   118,    98,
      80,    85,     7,    84
    };
  }

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
  private static final byte[] yyr1_ = yyr1_init();
  private static final byte[] yyr1_init()
  {
    return new byte[]
    {
       0,    57,    58,    58,    59,    59,    60,    60,    61,    61,
      62,    62,    63,    64,    64,    65,    65,    66,    66,    67,
      67,    68,    68,    68,    68,    68,    68,    69,    69,    69,
      70,    70,    70,    70,    71,    71,    71,    71,    72,    72,
      72,    72,    72,    72,    72,    72,    73,    74,    75,    75,
      76,    76,    76,    77,    78,    78,    79,    80,    80,    81,
      82,    82,    83,    84,    84,    85,    86,    86,    87,    87,
      87,    88,    88,    88,    89,    89,    90,    91,    91,    92,
      92,    93,    93,    94,    95,    95,    95,    95,    95,    95,
      95,    96,    97,    98,    98,    99,    99,   100,   100,   101,
     102,   102,   103,   103,   104,   105,   105,   106,   107,   107,
     108,   108,   109,   110,   110,   111,   111,   112,   113,   113,
     114,   114,   115,   115,   116,   116,   117,   117,   118,   118,
     119,   119
    };
  }

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
  private static final byte[] yyr2_ = yyr2_init();
  private static final byte[] yyr2_init()
  {
    return new byte[]
    {
       0,     2,     1,     2,     2,     3,     1,     1,     1,     1,
       4,     5,     4,     1,     3,     1,     3,     1,     3,     1,
       3,     1,     1,     1,     1,     1,     1,     1,     3,     3,
       1,     3,     3,     3,     1,     2,     2,     2,     1,     1,
       1,     1,     1,     1,     3,     1,     1,     1,     1,     1,
       1,     3,     4,     5,     0,     2,     2,     0,     4,     6,
       0,     2,     2,     0,     4,     3,     0,     2,     1,     1,
       1,     1,     1,     1,     1,     1,     5,     0,     1,     3,
       4,     2,     3,     1,     1,     1,     1,     1,     1,     1,
       1,     3,     5,     1,     2,     2,     3,     1,     1,     8,
       1,     3,     0,     1,     6,     0,     2,     3,     1,     2,
       1,     2,     6,     0,     1,     1,     3,     3,     0,     2,
       3,     2,     0,     1,     1,     2,     1,     1,     0,     1,
       1,     2
    };
  }




  /* YYTRANSLATE_(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
     as returned by yylex, with out-of-bounds checking.  */
  private static final SymbolKind yytranslate_(int t)
  {
    // Last valid token kind.
    int code_max = 311;
    if (t <= 0)
      return SymbolKind.S_YYEOF;
    else if (t <= code_max)
      return SymbolKind.get(yytranslate_table_[t]);
    else
      return SymbolKind.S_YYUNDEF;
  }
  private static final byte[] yytranslate_table_ = yytranslate_table_init();
  private static final byte[] yytranslate_table_init()
  {
    return new byte[]
    {
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19,    20,    21,    22,    23,    24,
      25,    26,    27,    28,    29,    30,    31,    32,    33,    34,
      35,    36,    37,    38,    39,    40,    41,    42,    43,    44,
      45,    46,    47,    48,    49,    50,    51,    52,    53,    54,
      55,    56
    };
  }


  private static final int YYLAST_ = 190;
  private static final int YYEMPTY_ = -2;
  private static final int YYFINAL_ = 7;
  private static final int YYNTOKENS_ = 57;

/* Unqualified %code blocks.  */
/* "src/main/bison/parser.y":66  */

    private Program program;

    /**
     * Returns the AST after parse() has returned true.
     * @throws IllegalStateException if no program has been constructed
     */
    public Program getProgram() {
        if (program == null) {
            throw new IllegalStateException("No program AST is available; parse() must succeed first.");
        }
        return program;
    }

    private static SourcePosition position(Token token) {
        return new SourcePosition(token.getLine(), token.getColumn());
    }

/* "src/main/java/com/compiler/parser/Parser.java":2377  */

}
/* "src/main/bison/parser.y":743  */

