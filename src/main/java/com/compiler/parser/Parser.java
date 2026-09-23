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
    S_bool_literal(73),            /* bool_literal  */
    S_modifiable_primary(74),      /* modifiable_primary  */
    S_routine_call(75),            /* routine_call  */
    S_optional_arguments(76),      /* optional_arguments  */
    S_argument_list(77),           /* argument_list  */
    S_argument_list_tail(78),      /* argument_list_tail  */
    S_constructor_expression(79),  /* constructor_expression  */
    S_optional_members(80),        /* optional_members  */
    S_member_initialization_list(81), /* member_initialization_list  */
    S_member_initialization_list_tail(82), /* member_initialization_list_tail  */
    S_member_initialization(83),   /* member_initialization  */
    S_optional_initializer(84),    /* optional_initializer  */
    S_type(85),                    /* type  */
    S_primitive_type(86),          /* primitive_type  */
    S_user_type(87),               /* user_type  */
    S_array_type(88),              /* array_type  */
    S_optional_array_size(89),     /* optional_array_size  */
    S_record_type(90),             /* record_type  */
    S_record_member_sequence(91),  /* record_member_sequence  */
    S_statement(92),               /* statement  */
    S_assignment(93),              /* assignment  */
    S_while_loop(94),              /* while_loop  */
    S_body(95),                    /* body  */
    S_body_sequence(96),           /* body_sequence  */
    S_body_item(97),               /* body_item  */
    S_for_loop(98),                /* for_loop  */
    S_range(99),                   /* range  */
    S_optional_reverse(100),       /* optional_reverse  */
    S_if_statement(101),           /* if_statement  */
    S_optional_else(102),          /* optional_else  */
    S_print_statement(103),        /* print_statement  */
    S_return_statement(104),       /* return_statement  */
    S_routine_declaration(105),    /* routine_declaration  */
    S_routine_header(106),         /* routine_header  */
    S_optional_parameters(107),    /* optional_parameters  */
    S_parameter_list(108),         /* parameter_list  */
    S_parameter(109),              /* parameter  */
    S_optional_return_type(110),   /* optional_return_type  */
    S_routine_body(111),           /* routine_body  */
    S_optional_separators(112),    /* optional_separators  */
    S_separators(113),             /* separators  */
    S_separator(114),              /* separator  */
    S_optional_newlines(115),      /* optional_newlines  */
    S_newlines(116);               /* newlines  */


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
      SymbolKind.S_bool_literal,
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
  "simple", "factor", "summand", "primary", "bool_literal",
  "modifiable_primary", "routine_call", "optional_arguments",
  "argument_list", "argument_list_tail", "constructor_expression",
  "optional_members", "member_initialization_list",
  "member_initialization_list_tail", "member_initialization",
  "optional_initializer", "type", "primitive_type", "user_type",
  "array_type", "optional_array_size", "record_type",
  "record_member_sequence", "statement", "assignment", "while_loop",
  "body", "body_sequence", "body_item", "for_loop", "range",
  "optional_reverse", "if_statement", "optional_else", "print_statement",
  "return_statement", "routine_declaration", "routine_header",
  "optional_parameters", "parameter_list", "parameter",
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
        
/* "src/main/java/com/compiler/parser/Parser.java":695  */

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

  private static final short yypact_ninf_ = -121;
  private static final short yytable_ninf_ = -1;

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
  private static final short[] yypact_ = yypact_init();
  private static final short[] yypact_init()
  {
    return new short[]
    {
      29,  -121,  -121,    28,    70,    29,  -121,  -121,     5,    22,
      24,  -121,    29,  -121,  -121,  -121,  -121,    11,  -121,    20,
       9,    37,  -121,     8,    29,    86,  -121,    31,    86,   128,
     128,  -121,    81,   121,  -121,  -121,  -121,    86,    44,  -121,
    -121,    86,    86,    86,    60,    69,    78,    71,  -121,   136,
      41,  -121,  -121,  -121,    -9,  -121,  -121,    67,    64,    75,
    -121,    69,    29,    72,  -121,  -121,  -121,  -121,   116,  -121,
    -121,  -121,  -121,  -121,  -121,    86,    86,    94,    86,    76,
    -121,    25,  -121,  -121,  -121,  -121,  -121,    29,  -121,  -121,
    -121,  -121,  -121,    76,  -121,  -121,    18,    87,    86,    86,
      86,    86,    86,  -121,  -121,  -121,  -121,  -121,  -121,    86,
      86,    86,    86,   100,    86,   128,    93,    31,    40,    86,
      86,  -121,    14,    13,   126,    69,    76,    86,  -121,    86,
    -121,    16,    86,  -121,    76,    78,    71,  -121,    41,    41,
      51,  -121,  -121,  -121,  -121,     7,  -121,   128,  -121,  -121,
    -121,    29,   134,    69,    91,    69,    29,    29,    86,  -121,
      -5,  -121,    69,  -121,   102,    76,   123,  -121,  -121,  -121,
      10,  -121,   128,   144,   149,    12,   142,    76,  -121,  -121,
    -121,   153,   110,    76,   108,  -121,  -121,    29,   155,  -121,
      86,  -121,   151,    86,    86,  -121,  -121,    76,  -121,  -121,
    -121,    69,    29,    -5,    69,   123,   157,  -121,   108,  -121,
    -121
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
     119,   124,   123,     0,     2,   120,   121,     1,     0,     0,
       0,     3,   119,     6,     8,     9,     7,   107,   122,     0,
       0,     0,     4,   120,   119,     0,   108,   110,     0,     0,
       0,     5,     0,    90,    46,    47,    45,     0,    48,    38,
      39,     0,     0,     0,     0,   118,    13,    15,    17,    19,
      27,    30,    34,    40,    41,    42,    43,     0,     0,   111,
     112,    10,   119,     0,    69,    70,    71,    68,    64,    66,
      67,    72,    73,    12,   117,     0,     0,     0,   105,   125,
      94,     0,    82,    95,    81,    83,    91,   119,    84,    85,
      86,    87,    37,   125,    35,    36,     0,     0,     0,     0,
       0,     0,     0,    25,    26,    21,    22,    23,    24,     0,
       0,     0,     0,     0,     0,     0,   115,     0,     0,    75,
       0,    11,     0,     0,     0,   106,   127,     0,   126,     0,
      92,   120,    52,    44,   125,    14,    16,    18,    28,    29,
      20,    31,    32,    33,    49,     0,   114,     0,   109,   113,
      77,   119,     0,    76,     0,    65,   119,   119,     0,   128,
      55,   104,    88,    93,     0,   125,    58,    50,   116,    79,
     120,    78,     0,   102,     0,    97,    99,   125,    54,    51,
      53,     0,     0,   125,    61,    80,    74,   119,     0,    89,
       0,   100,     0,     0,     0,    57,    59,   125,    60,   103,
     101,    98,   119,    55,    63,     0,     0,    56,    61,    96,
      62
    };
  }

/* YYPGOTO[NTERM-NUM].  */
  private static final short[] yypgoto_ = yypgoto_init();
  private static final short[] yypgoto_init()
  {
    return new short[]
    {
    -121,  -121,   150,  -121,   -26,  -113,  -121,   -25,    74,    68,
      82,  -121,    65,     1,   -11,  -121,  -121,   -24,   -23,  -121,
      49,   -20,  -121,  -121,  -121,   -22,   -21,  -121,   -28,  -121,
    -121,  -121,  -121,  -121,    15,  -121,  -121,  -121,  -120,    56,
    -121,  -121,  -121,  -121,  -121,  -121,  -121,  -121,  -121,  -121,
    -121,  -121,    73,  -121,  -121,     4,    -6,    -4,   -85,    62
    };
  }

/* YYDEFGOTO[NTERM-NUM].  */
  private static final short[] yydefgoto_ = yydefgoto_init();
  private static final short[] yydefgoto_init()
  {
    return new short[]
    {
       0,     3,    11,    12,    13,    14,    15,   160,    46,    47,
      48,   109,    49,    50,    51,    52,    53,    54,    55,   164,
     161,   178,    56,   182,   183,   198,   184,   121,    68,    69,
      70,    71,   154,    72,   152,    83,    84,    85,    32,    86,
      87,    88,   176,   192,    89,   188,    90,    91,    16,    17,
      58,    59,    60,   148,    26,    33,     5,     6,   127,   128
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
      45,    18,    73,    61,     4,   151,    23,    80,   132,    81,
      82,     8,     9,    10,     9,    28,    22,    24,    96,    18,
       9,    10,    98,   156,    75,   157,    92,    76,     7,    77,
      94,    95,    78,    79,    98,    19,   173,   174,   113,    98,
      98,    98,   114,    30,     9,    98,    38,   150,   177,   166,
     122,   123,    20,   125,    21,    29,    25,   151,   129,   167,
     190,    57,     1,     2,     1,     2,   118,   199,   133,    27,
       1,     2,   113,     8,     9,    10,   114,   110,   111,   112,
     180,   131,   206,     1,     2,   101,   102,   146,    74,   145,
      97,   130,   193,    93,   153,   155,    98,   100,   196,   141,
     142,   143,   138,   139,   162,    80,    99,    81,    82,    34,
      35,    36,   205,   115,   116,    37,    38,    39,    40,   168,
      41,    42,   120,   119,   124,     9,    10,    18,   117,    75,
     144,   126,    76,   175,    77,    43,   134,    78,    79,   147,
     158,   171,    44,   172,   186,   170,    62,    63,    64,    65,
      66,    38,   179,   181,   187,   169,   189,   191,    67,   194,
     195,   197,   200,   202,   209,   201,    18,   136,   203,   204,
     101,   102,   135,    31,   140,   103,   104,   105,   106,   107,
     108,   165,   137,   207,   208,   185,   210,   163,   159,     0,
     149
    };
  }

private static final short[] yycheck_ = yycheck_init();
  private static final short[] yycheck_init()
  {
    return new short[]
    {
      25,     5,    30,    28,     0,   118,    12,    33,    93,    33,
      33,     3,     4,     5,     4,     6,    12,     6,    43,    23,
       4,     5,    27,     9,     8,    12,    37,    11,     0,    13,
      41,    42,    16,    17,    27,    30,   156,   157,    47,    27,
      27,    27,    51,     6,     4,    27,    30,     7,    53,   134,
      75,    76,    30,    78,    30,    46,    45,   170,    33,    52,
      48,    30,    54,    55,    54,    55,    62,   187,    50,    49,
      54,    55,    47,     3,     4,     5,    51,    36,    37,    38,
     165,    87,   202,    54,    55,    34,    35,   115,     7,   114,
      30,    87,   177,    49,   119,   120,    27,    26,   183,   110,
     111,   112,   101,   102,   129,   131,    28,   131,   131,    23,
      24,    25,   197,    46,    50,    29,    30,    31,    32,   147,
      34,    35,     6,    51,    30,     4,     5,   131,    53,     8,
      30,    55,    11,   158,    13,    49,    49,    16,    17,    46,
      14,     7,    56,    52,   172,   151,    18,    19,    20,    21,
      22,    30,    50,    30,    10,   151,     7,    15,    30,     6,
      50,    53,     7,    12,     7,   190,   170,    99,   193,   194,
      34,    35,    98,    23,   109,    39,    40,    41,    42,    43,
      44,   132,   100,   203,   205,   170,   208,   131,   126,    -1,
     117
    };
  }

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
  private static final byte[] yystos_ = yystos_init();
  private static final byte[] yystos_init()
  {
    return new byte[]
    {
       0,    54,    55,    58,   112,   113,   114,     0,     3,     4,
       5,    59,    60,    61,    62,    63,   105,   106,   114,    30,
      30,    30,   112,   113,     6,    45,   111,    49,     6,    46,
       6,    59,    95,   112,    23,    24,    25,    29,    30,    31,
      32,    34,    35,    49,    56,    64,    65,    66,    67,    69,
      70,    71,    72,    73,    74,    75,    79,    30,   107,   108,
     109,    64,    18,    19,    20,    21,    22,    30,    85,    86,
      87,    88,    90,    85,     7,     8,    11,    13,    16,    17,
      61,    74,    75,    92,    93,    94,    96,    97,    98,   101,
     103,   104,    71,    49,    71,    71,    64,    30,    27,    28,
      26,    34,    35,    39,    40,    41,    42,    43,    44,    68,
      36,    37,    38,    47,    51,    46,    50,    53,   112,    51,
       6,    84,    64,    64,    30,    64,    55,   115,   116,    33,
     112,   113,   115,    50,    49,    65,    66,    67,    70,    70,
      69,    71,    71,    71,    30,    64,    85,    46,   110,   109,
       7,    62,    91,    64,    89,    64,     9,    12,    14,   116,
      64,    77,    64,    96,    76,    77,   115,    52,    85,   112,
     113,     7,    52,    95,    95,    64,    99,    53,    78,    50,
     115,    30,    80,    81,    83,    91,    85,    10,   102,     7,
      48,    15,   100,   115,     6,    50,   115,    53,    82,    95,
       7,    64,    12,    64,    64,   115,    95,    78,    83,     7,
      82
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
      72,    72,    72,    72,    72,    72,    73,    73,    74,    74,
      74,    75,    76,    76,    77,    78,    78,    79,    80,    80,
      81,    82,    82,    83,    84,    84,    85,    85,    85,    86,
      86,    86,    87,    87,    88,    89,    89,    90,    90,    91,
      91,    92,    92,    92,    92,    92,    92,    92,    93,    94,
      95,    95,    96,    96,    97,    97,    98,    99,    99,   100,
     100,   101,   102,   102,   103,   104,   104,   105,   105,   106,
     107,   107,   108,   108,   109,   110,   110,   111,   111,   112,
     112,   113,   113,   114,   114,   115,   115,   116,   116
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
       1,     1,     1,     1,     3,     1,     1,     1,     1,     3,
       4,     5,     0,     2,     2,     0,     4,     6,     0,     2,
       2,     0,     4,     3,     0,     2,     1,     1,     1,     1,
       1,     1,     1,     1,     5,     0,     1,     3,     4,     2,
       3,     1,     1,     1,     1,     1,     1,     1,     3,     5,
       1,     2,     2,     3,     1,     1,     8,     1,     3,     0,
       1,     6,     0,     2,     3,     1,     2,     1,     2,     6,
       0,     1,     1,     3,     3,     0,     2,     3,     2,     0,
       1,     1,     2,     1,     1,     0,     1,     1,     2
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


}
/* "src/main/bison/parser.y":433  */

