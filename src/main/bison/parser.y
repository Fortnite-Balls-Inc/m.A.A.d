%language "Java"

%define api.package {com.compiler.parser}
%define api.parser.class {Parser}
%define api.parser.public

%define parse.error detailed

/* Keywords */
%token ROUTINE VAR TYPE IS END
%token IF THEN ELSE
%token WHILE LOOP FOR IN REVERSE
%token RETURN PRINT
%token RECORD ARRAY
%token INTEGER REAL BOOLEAN
%token TRUE FALSE NULL
%token AND OR XOR NOT

/* Identifiers and literals */
%token IDENTIFIER
%token INTEGER_LITERAL REAL_LITERAL

/* Operators */
%token ASSIGN
%token PLUS MINUS STAR SLASH PERCENT
%token EQ NEQ LT LE GT GE
%token RETIMM

/* Punctuation */
%token COLON
%token DOT DOTDOT
%token LPAREN RPAREN
%token LBRACKET RBRACKET
%token COMMA SEMICOLON NEWLINE

/* Record initialization */
%token NEW

%%



/* =======================
   Program structure
   ======================= */



/* 
 1. Program may be empty or consist only of separators
 2. Otherwise, a program is a sequence of zero or more separators
 and a declaration sequence
*/
program
    : optional_separators
    | optional_separators declaration_sequence
    ;

/*
 1. Declaration sequence may be a top-level declaration (TLD)
 a sequence of zero or more separators
 2. Otherwise, TLD and a new declaration sequence split
 by a sequence of separators
*/
declaration_sequence
    : top_level_declaration optional_separators
    | top_level_declaration separators declaration_sequence
    ;

/*
 1. TLD may be a simple declaration
 2. Otherwise, a routine declaration

 Note: routine_declaration cannot be included in simple_declaration
 since the body of a routine consists of simple declarations
 and nested routines are not allowed
*/
top_level_declaration
    : simple_declaration
    | routine_declaration
    ;



/* =======================
   Declarations
   ======================= */



simple_declaration
    : variable_declaration
    | type_declaration
    ;

/*
 In a variable declaration, the type is either inferred from the RHS expression
 or stated explicitly with the optional assignment of the RHS expression
*/
variable_declaration
    : VAR IDENTIFIER IS expression
    | VAR IDENTIFIER COLON type optional_initializer
    ;


type_declaration
    : TYPE IDENTIFIER IS type
    ;



/* =======================
   Expressions
   ======================= */



/*
 Priority:

 1. OR < XOR < AND
 2. AND < comparison operator < +/-
 3. +/- < mul/div/mod
 4. mul/div/mod < +/-/NOT expr
 5. +/-/NOT (expr) < literal/call/(expr)/...
*/
expression
    : xor_expression
    | expression OR xor_expression
    ;

xor_expression
    : and_expression
    | xor_expression XOR and_expression 
    ;

and_expression
    : relation
    | and_expression AND relation
    ;

relation
    : simple
    | simple comparison_operator simple
    ;

comparison_operator
    : LT
    | LE
    | GT
    | GE
    | EQ
    | NEQ
    ;

simple
    : factor
    | simple PLUS factor
    | simple MINUS factor
    ;

factor
    : summand
    | factor STAR summand
    | factor SLASH summand
    | factor PERCENT summand
    ;

summand
    : primary
    | PLUS summand
    | MINUS summand
    | NOT summand
    ;

primary
    : INTEGER_LITERAL
    | REAL_LITERAL
    | bool_literal
    | modifiable_primary
    | routine_call
    | constructor_expression
    | LPAREN expression RPAREN
    | NULL
    ;

bool_literal
    : TRUE
    | FALSE
    ;

modifiable_primary
    : IDENTIFIER
    | modifiable_primary DOT IDENTIFIER
    | modifiable_primary LBRACKET expression RBRACKET
    ;

routine_call
    : IDENTIFIER LPAREN optional_newlines optional_arguments RPAREN
    ;

optional_arguments
    : %empty
    | argument_list optional_newlines
    ;

argument_list
    : expression argument_list_tail
    ;

argument_list_tail
    : %empty
    | COMMA optional_newlines expression argument_list_tail
    ;

constructor_expression
    : NEW IDENTIFIER LPAREN optional_newlines optional_members RPAREN
    ;

optional_members
    : %empty
    | member_initialization_list optional_newlines
    ;

member_initialization_list
    : member_initialization member_initialization_list_tail
    ;

member_initialization_list_tail
    : %empty
    | COMMA optional_newlines member_initialization member_initialization_list_tail
    ;

member_initialization
    : IDENTIFIER IS expression
    ;

optional_initializer
    : %empty
    | IS expression
    ;



/* =======================
   Types
   ======================= */



type
    : primitive_type
    | user_type
    | IDENTIFIER
    ;

primitive_type
    : INTEGER
    | REAL
    | BOOLEAN
    ;

user_type
    : array_type
    | record_type
    ;

array_type
    : ARRAY LBRACKET optional_array_size RBRACKET type
    ;

optional_array_size
    : %empty
    | expression
    ;

record_type
    : RECORD optional_separators END
    | RECORD optional_separators record_member_sequence END
    ;

record_member_sequence
    : variable_declaration optional_separators
    | variable_declaration separators record_member_sequence
    ;



/* =======================
   Statements
   ======================= */



statement
    : assignment
    | routine_call
    | while_loop
    | for_loop
    | if_statement
    | print_statement
    | return_statement
    ;

assignment
    : modifiable_primary ASSIGN expression
    ;

while_loop
    : WHILE expression LOOP body END
    ;

body
    : optional_separators
    | optional_separators body_sequence
    ;

body_sequence
    : body_item optional_separators
    | body_item separators body_sequence
    ;

body_item
    : simple_declaration
    | statement
    ;

for_loop
    : FOR IDENTIFIER IN range optional_reverse LOOP body END
    ;

range
    : expression
    | expression DOTDOT expression
    ;

optional_reverse
    : %empty
    | REVERSE
    ;

if_statement
    : IF expression THEN body optional_else END
    ;

optional_else
    : %empty
    | ELSE body
    ;

print_statement
    : PRINT optional_newlines argument_list
    ;

return_statement
    : RETURN
    | RETURN expression
    ;



/* =======================
   Routines
   ======================= */



routine_declaration
    : routine_header
    | routine_header routine_body
    ;

routine_header
    : ROUTINE IDENTIFIER LPAREN optional_parameters RPAREN optional_return_type
    ;

optional_parameters
    : %empty
    | parameter_list
    ;

parameter_list
    : parameter
    | parameter_list COMMA parameter
    ;

parameter
    : IDENTIFIER COLON type
    ;

optional_return_type
    : %empty
    | COLON type
    ;

routine_body
    : IS body END
    | RETIMM expression
    ;



/* =======================
   Separators & Helpers
   ======================= */



optional_separators
    : %empty | separators
    ;

separators
    : separator
    | separators separator
    ;

separator
    : NEWLINE
    | SEMICOLON
    ;

optional_newlines
    : %empty
    | newlines
    ;

newlines
    : NEWLINE
    | NEWLINE newlines
    ;

%%
