%language "Java"

%define api.package {com.compiler.parser}
%define api.parser.class {Parser}
%define api.parser.public

%code imports {
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
}

%code {
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
}

%define parse.error detailed

/* Keywords */
%token <Token> ROUTINE
%token <Token> VAR TYPE
%token <Token> IS
%token END
%token <Token> IF THEN ELSE
%token <Token> WHILE LOOP FOR
%token IN REVERSE
%token <Token> RETURN PRINT
%token <Token> RECORD ARRAY
%token <Token> INTEGER REAL BOOLEAN
%token <Token> TRUE FALSE NULL
%token AND OR XOR
%token <Token> NOT

/* Identifiers and literals */
%token <Token> IDENTIFIER
%token <Token> INTEGER_LITERAL REAL_LITERAL

/* Operators */
%token ASSIGN
%token <Token> PLUS MINUS
%token STAR SLASH PERCENT
%token EQ NEQ LT LE GT GE
%token <Token> RETIMM

/* Punctuation */
%token COLON
%token DOT DOTDOT
%token LPAREN RPAREN
%token LBRACKET RBRACKET
%token COMMA SEMICOLON NEWLINE

/* Record initialization */
%token <Token> NEW

/* Program and blocks */
%type <Program> program
%type <DeclarationList> declaration_sequence
%type <Declaration> top_level_declaration
%type <BlockItemList> body body_sequence
%type <BlockItem> body_item

/* Statements */
%type <Statement> statement
%type <Assignment> assignment
%type <WhileLoop> while_loop
%type <ForLoop> for_loop
%type <IterationSource> range
%type <Boolean> optional_reverse
%type <IfStatement> if_statement
%type <Block> optional_else
%type <PrintStatement> print_statement
%type <ReturnStatement> return_statement

/* Routines */
%type <RoutineDeclaration> routine_declaration
%type <RoutineHeader> routine_header
%type <ParameterList> optional_parameters parameter_list
%type <Parameter> parameter
%type <TypeNode> optional_return_type
%type <RoutineBody> routine_body

/* Expressions */
%type <Expression> expression xor_expression and_expression relation
%type <Expression> simple factor summand primary modifiable_primary
%type <BinaryOperator> comparison_operator
%type <RoutineCall> routine_call
%type <RecordConstructor> constructor_expression
%type <MemberInitialization> member_initialization
%type <ExpressionList> optional_arguments argument_list argument_list_tail
%type <MemberInitializationList> optional_members member_initialization_list member_initialization_list_tail
%type <BooleanLiteral> boolean_literal
%type <RealLiteral> real_literal
%type <IntegerLiteral> integer_literal

/* Declarations */
%type <SimpleDeclaration> simple_declaration
%type <VariableDeclaration> variable_declaration
%type <TypeDeclaration> type_declaration
%type <Expression> optional_initializer optional_array_size

/* Types */
%type <TypeNode> type
%type <UserType> user_type
%type <ArrayType> array_type
%type <RecordType> record_type
%type <PrimitiveType> primitive_type
%type <NamedType> named_type
%type <VariableDeclarationList> record_member_sequence

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
      { program = new Program(List.of(), new SourcePosition(1, 1)); $$ = program; }
    | optional_separators declaration_sequence
      { program = new Program($2.values(), new SourcePosition(1, 1)); $$ = program; }
    ;

/*
 1. Declaration sequence may be a top-level declaration (TLD)
 a sequence of zero or more separators
 2. Otherwise, TLD and a new declaration sequence split
 by a sequence of separators
*/
declaration_sequence
    : top_level_declaration optional_separators
      {
        DeclarationList declarations = new DeclarationList(); // the last TLD
        declarations.addFirst($1);
        $$ = declarations;
      }
    | top_level_declaration separators declaration_sequence
      { $3.addFirst($1); $$ = $3; } // fresh TLD -> push on top
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
      { $$ = $1; }
    | routine_declaration
      { $$ = $1; }
    ;



/* =======================
   Declarations
   ======================= */



simple_declaration
    : variable_declaration
      { $$ = $1; }
    | type_declaration
      { $$ = $1; }
    ;

/*
 In a variable declaration, the type is either inferred from the RHS expression
 or stated explicitly with the optional assignment of the RHS expression
*/
variable_declaration
    : VAR IDENTIFIER IS expression
      {
        $$ = new VariableDeclaration(
            $2.getStringValue(), Optional.empty(), Optional.of($4), position($1)
        );
      }
    | VAR IDENTIFIER COLON type optional_initializer
      {
        $$ = new VariableDeclaration(
            $2.getStringValue(), Optional.of($4), Optional.ofNullable($5), position($1)
        );
      }
    ;


type_declaration
    : TYPE IDENTIFIER IS type
      { $$ = new TypeDeclaration($2.getStringValue(), $4, position($1)); }
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
      { $$ = $1; }
    | expression OR xor_expression
      { $$ = new BinaryExpression(BinaryOperator.OR, $1, $3, $1.position()); }
    ;

xor_expression
    : and_expression
      { $$ = $1; }
    | xor_expression XOR and_expression
      { $$ = new BinaryExpression(BinaryOperator.XOR, $1, $3, $1.position()); }
    ;

and_expression
    : relation
      { $$ = $1; }
    | and_expression AND relation
      { $$ = new BinaryExpression(BinaryOperator.AND, $1, $3, $1.position()); }
    ;

relation
    : simple
      { $$ = $1; }
    | simple comparison_operator simple
      { $$ = new BinaryExpression($2, $1, $3, $1.position()); }
    ;

comparison_operator
    : LT  { $$ = BinaryOperator.LESS_THAN; }
    | LE  { $$ = BinaryOperator.LESS_THAN_OR_EQUAL; }
    | GT  { $$ = BinaryOperator.GREATER_THAN; }
    | GE  { $$ = BinaryOperator.GREATER_THAN_OR_EQUAL; }
    | EQ  { $$ = BinaryOperator.EQUAL; }
    | NEQ { $$ = BinaryOperator.NOT_EQUAL; }
    ;

simple
    : factor
      { $$ = $1; }
    | simple PLUS factor
      { $$ = new BinaryExpression(BinaryOperator.ADD, $1, $3, $1.position()); }
    | simple MINUS factor
      { $$ = new BinaryExpression(BinaryOperator.SUBTRACT, $1, $3, $1.position()); }
    ;

factor
    : summand
      { $$ = $1; }
    | factor STAR summand
      { $$ = new BinaryExpression(BinaryOperator.MULTIPLY, $1, $3, $1.position()); }
    | factor SLASH summand
      { $$ = new BinaryExpression(BinaryOperator.DIVIDE, $1, $3, $1.position()); }
    | factor PERCENT summand
      { $$ = new BinaryExpression(BinaryOperator.REMAINDER, $1, $3, $1.position()); }
    ;

summand
    : primary
      { $$ = $1; }
    | PLUS summand
      { $$ = new UnaryExpression(UnaryOperator.PLUS, $2, position($1)); }
    | MINUS summand
      { $$ = new UnaryExpression(UnaryOperator.MINUS, $2, position($1)); }
    | NOT summand
      { $$ = new UnaryExpression(UnaryOperator.NOT, $2, position($1)); }
    ;

primary
    : integer_literal
      { $$ = $1; }
    | real_literal
      { $$ = $1; }
    | boolean_literal
      { $$ = $1; }
    | modifiable_primary
      { $$ = $1; }
    | routine_call
      { $$ = $1; }
    | constructor_expression
      { $$ = $1; }
    | LPAREN expression RPAREN
      { $$ = $2; }
    | NULL
      { $$ = new NullLiteral(position($1)); }
    ;

integer_literal
    : INTEGER_LITERAL
      { $$ = new IntegerLiteral($1.getIntValue(), position($1)); }
    ;

real_literal
    : REAL_LITERAL
      { $$ = new RealLiteral($1.getRealValue(), position($1)); }
    ;

boolean_literal
    : TRUE
      { $$ = new BooleanLiteral(true, position($1)); }
    | FALSE
      { $$ = new BooleanLiteral(false, position($1)); }
    ;

modifiable_primary
    : IDENTIFIER
      { $$ = new Identifier($1.getStringValue(), position($1)); }
    | modifiable_primary DOT IDENTIFIER
      { $$ = new FieldAccess($1, $3.getStringValue(), $1.position()); }
    | modifiable_primary LBRACKET expression RBRACKET
      { $$ = new IndexAccess($1, $3, $1.position()); }
    ;

routine_call
    : IDENTIFIER LPAREN optional_newlines optional_arguments RPAREN
      { $$ = new RoutineCall($1.getStringValue(), $4.values(), position($1)); }
    ;

optional_arguments
    : %empty
      { $$ = new ExpressionList(); }
    | argument_list optional_newlines
      { $$ = $1; }
    ;

argument_list
    : expression argument_list_tail
      { $2.addFirst($1); $$ = $2; }
    ;

argument_list_tail
    : %empty
      { $$ = new ExpressionList(); }
    | COMMA optional_newlines expression argument_list_tail
      { $4.addFirst($3); $$ = $4; }
    ;

constructor_expression
    : NEW IDENTIFIER LPAREN optional_newlines optional_members RPAREN
      { $$ = new RecordConstructor($2.getStringValue(), $5.values(), position($1)); }
    ;

optional_members
    : %empty
      { $$ = new MemberInitializationList(); }
    | member_initialization_list optional_newlines
      { $$ = $1; }
    ;

member_initialization_list
    : member_initialization member_initialization_list_tail
      { $2.addFirst($1); $$ = $2; }
    ;

member_initialization_list_tail
    : %empty
      { $$ = new MemberInitializationList(); }
    | COMMA optional_newlines member_initialization member_initialization_list_tail
      { $4.addFirst($3); $$ = $4; }
    ;

member_initialization
    : IDENTIFIER IS expression
      { $$ = new MemberInitialization($1.getStringValue(), $3, position($1)); }
    ;

optional_initializer
    : %empty
      { $$ = null; } // to use as Optional.ofNullable(...)
    | IS expression
      { $$ = $2; }
    ;



/* =======================
   Types
   ======================= */



type
    : primitive_type
      { $$ = $1; }
    | user_type
      { $$ = $1; }
    | named_type
      { $$ = $1; }
    ;

primitive_type
    : INTEGER
      { $$ = new PrimitiveType(PrimitiveKind.INTEGER, position($1)); }
    | REAL
      { $$ = new PrimitiveType(PrimitiveKind.REAL, position($1)); }
    | BOOLEAN
      { $$ = new PrimitiveType(PrimitiveKind.BOOLEAN, position($1)); }
    ;

user_type
    : array_type
      { $$ = $1; }
    | record_type
      { $$ = $1; }
    ;

array_type
    : ARRAY LBRACKET optional_array_size RBRACKET type
      { $$ = new ArrayType(Optional.ofNullable($3), $5, position($1)); }
    ;

optional_array_size
    : %empty
      { $$ = null; }
    | expression
      { $$ = $1; }
    ;

record_type
    : RECORD optional_separators END
      { $$ = new RecordType(List.of(), position($1)); }
    | RECORD optional_separators record_member_sequence END
      { $$ = new RecordType($3.values(), position($1)); }
    ;

record_member_sequence
    : variable_declaration optional_separators
      {
        VariableDeclarationList fields = new VariableDeclarationList();
        fields.addFirst($1);
        $$ = fields;
      }
    | variable_declaration separators record_member_sequence
      { $3.addFirst($1); $$ = $3; }
    ;

named_type
    : IDENTIFIER
      { $$ = new NamedType($1.getStringValue(), position($1)); }
    ;



/* =======================
   Statements
   ======================= */



statement
    : assignment
      { $$ = $1; }
    | routine_call
      { $$ = new RoutineCallStatement($1, $1.position()); }
    | while_loop
      { $$ = $1; }
    | for_loop
      { $$ = $1; }
    | if_statement
      { $$ = $1; }
    | print_statement
      { $$ = $1; }
    | return_statement
      { $$ = $1; }
    ;

assignment
    : modifiable_primary ASSIGN expression
      { $$ = new Assignment($1, $3, $1.position()); }
    ;

while_loop
    : WHILE expression LOOP body END
      { $$ = new WhileLoop($2, new Block($4.values(), position($3)), position($1)); }
    ;

body
    : optional_separators
      { $$ = new BlockItemList(); }
    | optional_separators body_sequence
      { $$ = $2; }
    ;

body_sequence
    : body_item optional_separators
      {
        BlockItemList items = new BlockItemList();
        items.addFirst($1);
        $$ = items;
      }
    | body_item separators body_sequence
      { $3.addFirst($1); $$ = $3; }
    ;

body_item
    : simple_declaration
      { $$ = $1; }
    | statement
      { $$ = $1; }
    ;

for_loop
    : FOR IDENTIFIER IN range optional_reverse LOOP body END
      {
        $$ = new ForLoop(
            $2.getStringValue(), $4, $5, new Block($7.values(), position($6)), position($1)
        );
      }
    ;

range
    : expression
      { $$ = new ExpressionSource($1, $1.position()); }
    | expression DOTDOT expression
      { $$ = new RangeSource($1, $3, $1.position()); }
    ;

optional_reverse
    : %empty
      { $$ = false; }
    | REVERSE
      { $$ = true; }
    ;

if_statement
    : IF expression THEN body optional_else END
      {
        $$ = new IfStatement(
            $2, new Block($4.values(), position($3)), Optional.ofNullable($5), position($1)
        );
      }
    ;

optional_else
    : %empty
      { $$ = null; }
    | ELSE body
      { $$ = new Block($2.values(), position($1)); }
    ;

print_statement
    : PRINT optional_newlines argument_list
      { $$ = new PrintStatement($3.values(), position($1)); }
    ;

return_statement
    : RETURN
      { $$ = new ReturnStatement(Optional.empty(), position($1)); }
    | RETURN expression
      { $$ = new ReturnStatement(Optional.of($2), position($1)); }
    ;



/* =======================
   Routines
   ======================= */



routine_declaration
    : routine_header
      {
        $$ = new RoutineDeclaration(
            $1.name(), $1.parameters(), $1.returnType(), Optional.empty(), $1.position()
        );
      }
    | routine_header routine_body
      {
        $$ = new RoutineDeclaration(
            $1.name(), $1.parameters(), $1.returnType(), Optional.of($2), $1.position()
        );
      }
    ;

routine_header
    : ROUTINE IDENTIFIER LPAREN optional_parameters RPAREN optional_return_type
      {
        $$ = new RoutineHeader(
            $2.getStringValue(), $4.values(), Optional.ofNullable($6), position($1)
        );
      }
    ;

optional_parameters
    : %empty
      { $$ = new ParameterList(); }
    | parameter_list
      { $$ = $1; }
    ;

parameter_list
    : parameter
      {
        ParameterList parameters = new ParameterList();
        parameters.addLast($1);
        $$ = parameters;
      }
    | parameter_list COMMA parameter
      { $1.addLast($3); $$ = $1; }
    ;

parameter
    : IDENTIFIER COLON type
      { $$ = new Parameter($1.getStringValue(), $3, position($1)); }
    ;

optional_return_type
    : %empty
      { $$ = null; }
    | COLON type
      { $$ = $2; }
    ;

routine_body
    : IS body END
      { $$ = new BlockRoutineBody(new Block($2.values(), position($1)), position($1)); }
    | RETIMM expression
      { $$ = new ExpressionRoutineBody($2, position($1)); }
    ;



/* =======================
   Separators & Helpers
   ======================= */



optional_separators
    : %empty
    | separators
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
