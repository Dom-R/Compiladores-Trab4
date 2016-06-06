/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
import AST.*;
import Lexer.*;
import AuxComp.SymbolTable;
import java.util.*;
import java.io.*;

public class Compiler {

	//Program ::= Decl
	public Program compile( char []input, PrintWriter outError, String nomeArq ) {

        symbolTable = new SymbolTable();
        error = new CompilerError(outError, nomeArq);
        lexer = new Lexer(input, error);
        error.setLexer(lexer);

		// Atribui o primeiro symbolo a token
        lexer.nextToken();

        Program program = new Program(Decl());

        return program;
    }

	//Decl ::= ‘void’ ‘main’ ‘(’ ‘)’ StmtBlock
	public Decl Decl() {

		ArrayList<Function> functionList = new ArrayList<Function>();
	
		while( lexer.token == Symbol.VOID || lexer.token == Symbol.INT || lexer.token == Symbol.CHAR || lexer.token == Symbol.DOUBLE ) {
			functionList.add(FunctionDecl());
		}
		
		if(lexer.token != Symbol.EOF) {
			error.signal("EOF expected");
		}
		
		if( symbolTable.getInGlobal("main") == null ) {
			error.signal("Missing function main");
		}
	
		return new Decl(functionList);
	}
	
	public Function FunctionDecl() {
		
		Type type = new VoidType();
		if( lexer.token != Symbol.VOID) {
			type = Type();
			
			// Verifica se o tipo eh uma array
			if(lexer.token == Symbol.LEFTBRACKET) {
				lexer.nextToken();

				if(lexer.token != Symbol.NUMBER)
					error.signal("Number expected");

				// Pega tamanho da array
				int arraySize = lexer.getNumberValue();
				if(arraySize <= 0)
					error.signal("Invalid array size");

				// Seta tamanho da array
				type.setArraySize(arraySize);

				lexer.nextToken();

				// Verifica se foi entrado com um numero nao int
				if(lexer.token == Symbol.DOT)
					error.signal("Array size can only be specified by a integer type");

				if(lexer.token != Symbol.RIGHTBRACKET)
					error.signal("Missing ]");
				lexer.nextToken();
			}
		} else {
			lexer.nextToken();
		}
		
		if(lexer.token != Symbol.IDENT && lexer.token != Symbol.MAIN) {
			error.signal("Missing function name");
		}
		String funcName = lexer.getStringValue();
		
		// Verifica se ja exite uma funcao com esse nome
		if( symbolTable.getInGlobal(funcName) != null ) {
			error.signal("Function " + funcName + " has already been declared!");
		}
		lexer.nextToken();
		
		Function func = new Function(type, funcName);
		
		// Seta variavel gloval de funcao para poder acessar ela nos outros metodos
		currentFunction = func;
		
		symbolTable.putInGlobal(funcName, func);
		
		if( lexer.token != Symbol.LEFTPAR ) {
			error.signal("Missing (");
		}
		lexer.nextToken();
		
		// Verifica se a funcao e a funcao main e se ela nao tem parametros, se tiver da erro
		if ( funcName.compareTo("main") == 0 && lexer.token != Symbol.RIGHTPAR ) {
			error.signal("Main must be a parameterless function");
		}
		
		func.setParamList( Formals() );
		
		if(lexer.token != Symbol.RIGHTPAR) {
			error.signal("Missing )");
		}
		lexer.nextToken();
		
		func.setStatementBlock( StmtBlock() );
		
		// Remove as variaveis locais salvas
		symbolTable.removeLocalIdent();
		
		return func;
	}
	
	private ParamList Formals() {
		ParamList paramList = new ParamList();
		
		if( lexer.token == Symbol.VOID || lexer.token == Symbol.INT || lexer.token == Symbol.CHAR || lexer.token == Symbol.DOUBLE ) {
			paramList.addElement( ParamDecl() );
			while(lexer.token == Symbol.COMMA) {
				lexer.nextToken();
				paramList.addElement( ParamDecl() );
			}
		}
		
		return paramList;
	}
	
	public Variable ParamDecl() {

        Type type = Type(); // Pega tipo da variavel
        int arraySize = 0; // Default 0(Nao eh array). Se valor for maior que 0 eh array

        // Verifica se variavel eh uma array
        if(lexer.token == Symbol.LEFTBRACKET) {
            lexer.nextToken();

            if(lexer.token != Symbol.NUMBER)
                error.signal("Number expected");

            // Pega tamanho da array
            arraySize = lexer.getNumberValue();
            if(arraySize <= 0)
                error.signal("Invalid array size");

			// Seta tamanho da array
			type.setArraySize(arraySize);

            lexer.nextToken();

            // Verifica se foi entrado com um numero nao int
            if(lexer.token == Symbol.DOT)
                error.signal("Array size can only be specified by a integer type");

            if(lexer.token != Symbol.RIGHTBRACKET)
                error.signal("Missing ]");
            lexer.nextToken();
        }

        // Pega o ident da variavel e ve se ela ja nao foi declarada
        if(lexer.token != Symbol.IDENT)
            error.signal("Expected identifier");

        String identificador = lexer.getStringValue();

        // Verifica se a variavel ja foi declarada
        if(symbolTable.get(identificador) != null)
            error.signal("Variable " + identificador + " has already been declared");

        Variable variable = new Variable(type, identificador);

        symbolTable.putInLocal(identificador, variable);

        lexer.nextToken();

        return variable;
	}

	//StmtBlock ::= ‘{’ { VariableDecl } { Stmt } ‘}’
	public StmtBlock StmtBlock() {
		ArrayList<Variable> variableDecl = new ArrayList<Variable>();
		ArrayList<Stmt> stmt = new ArrayList<Stmt>(); // Stmt

		if(lexer.token != Symbol.OPENBLOCK)
			error.signal("Missing {");
		lexer.nextToken();

		// Verifica se ainda ha variaveis sendo declaradas
		while(lexer.token == Symbol.INT || lexer.token == Symbol.CHAR || lexer.token == Symbol.DOUBLE) {
			variableDecl.add(VariableDecl());
		}

		// Verificar o que colocar aqui
	    while(lexer.token != Symbol.CLOSEBLOCK) {
			stmt.add(Stmt(false)); // Stmts daqui nao se encontram dentro de um while logo a flagWhile sera false para guardar que os statements nao esta dentro de um while
		}
		
		// Verificacão se há um return na funcao que nao eh void
		if(currentFunction.getReturnType().getCname() != "void") {
			Stmt lastStmt = null;
			for( Stmt currentStmt : stmt ) {
				lastStmt = currentStmt;
			}
			if(!(lastStmt instanceof ReturnStmt)) {
				error.signal("Function missing return");
			}
		}

		// Não pode usar o validarToken no final do programa, pois ele acaba andando de token, oq não deve acontecer
		if(lexer.token != Symbol.CLOSEBLOCK)
			error.signal("Missing }");
		lexer.nextToken();

		return new StmtBlock(variableDecl, stmt);
	}

	/*----------------------------------------*/

	/* REFAZER */
	//VariableDecl ::= Variable ‘;’
	//Variable ::= Type Ident
	//Type ::= StdType | ArrayType
	//StdType ::= ‘int’ | ‘double’ | ‘char’
	//ArrayType ::= StdType ‘[’ ‘]’
	public Variable VariableDecl() {

        Type type = Type(); // Pega tipo da variavel
        int arraySize = 0; // Default 0(Nao eh array). Se valor for maior que 0 eh array

        // Verifica se variavel eh uma array
        if(lexer.token == Symbol.LEFTBRACKET) {
            lexer.nextToken();

            if(lexer.token != Symbol.NUMBER)
                error.signal("Number expected");

            // Pega tamanho da array
            arraySize = lexer.getNumberValue();
            if(arraySize <= 0)
                error.signal("Invalid array size");

			// Seta tamanho da array
			type.setArraySize(arraySize);

            lexer.nextToken();

            // Verifica se foi entrado com um numero nao int
            if(lexer.token == Symbol.DOT)
                error.signal("Array size can only be specified by a integer type");

            if(lexer.token != Symbol.RIGHTBRACKET)
                error.signal("Missing ]");
            lexer.nextToken();
        }

        // Pega o ident da variavel e ve se ela ja nao foi declarada
        if(lexer.token != Symbol.IDENT)
            error.signal("Expected identifier");

        String identificador = lexer.getStringValue();

        // Verifica se a variavel ja foi declarada
        if(symbolTable.get(identificador) != null)
            error.signal("Variable " + identificador + " has already been declared");

        Variable variable = new Variable(type, identificador);

        symbolTable.putInLocal(identificador, variable);

        lexer.nextToken();

        if(lexer.token != Symbol.SEMICOLON)
            error.signal("Missing ;");
        lexer.nextToken();

        return variable;
	}

    public Type Type() {
        Type type = null;

        switch(lexer.token) {
            case INT :
              type = new IntegerType();
              break;
            case DOUBLE :
              type = new DoubleType();
              break;
            case CHAR :
              type = new CharType();
              break;
            default :
				error.signal("Type expected");
        }
        lexer.nextToken();

        return type;
    }

	/*----------------------------------------*/

	//Stmt ::= Expr ‘;’ | ifStmt | WhileStmt | BreakStmt | PrintStmt
	public Stmt Stmt(boolean flagWhile) {

		switch(lexer.token) {
			case IF:
				return IfStmt(flagWhile);
			case WHILE:
				return WhileStmt();
			case BREAK:
				return BreakStmt(flagWhile);
			case PRINT:
				return PrintStmt();
			case RETURN:
				return ReturnStmt();
			default:
                Expr expr = Expr(false); // Expr aqui nao esta sendo atribuido a algo
                if(lexer.token != Symbol.SEMICOLON)
                    error.signal("Missing ;");
				lexer.nextToken();
				return expr;
		}

	}

	//IfStmt ::= ‘if’ ‘(’ Expr ‘)’ ‘{’ { Stmt } ‘}’ [ ‘else’ ‘{’ { Stmt } ‘}’ ]
	public IfStmt IfStmt(boolean flagWhile) {
		Expr expr;
		ArrayList<Stmt> stmtif = new ArrayList<Stmt>();
		ArrayList<Stmt> stmtelse = new ArrayList<Stmt>();

		if(lexer.token != Symbol.IF)
			error.signal("Missing if");
		lexer.nextToken();

		if(lexer.token != Symbol.LEFTPAR)
			error.signal("Missing (");
		lexer.nextToken();

		expr = Expr(true);
		
		// Verificao contra string
		if(expr instanceof StringFactor) {
			error.signal("String used outside of a assignment");
		}

		if(lexer.token != Symbol.RIGHTPAR)
			error.signal("Missing )");
		lexer.nextToken();

		// Primeiro bloco do If
		if(lexer.token != Symbol.OPENBLOCK)
			error.signal("Missing {");
		lexer.nextToken();

		while(lexer.token != Symbol.CLOSEBLOCK) {
			stmtif.add(Stmt(flagWhile));
		}

		if(lexer.token != Symbol.CLOSEBLOCK)
			error.signal("Missing }");
		lexer.nextToken();

		// Bloco do Else
		if(lexer.token == Symbol.ELSE) {
			lexer.nextToken();

			if(lexer.token != Symbol.OPENBLOCK)
				error.signal("Missing {");
			lexer.nextToken();

			while(lexer.token != Symbol.CLOSEBLOCK) {
				stmtelse.add(Stmt(flagWhile));
			}

			if(lexer.token != Symbol.CLOSEBLOCK)
				error.signal("Missing }");
			lexer.nextToken();

		} else {
			stmtelse = null;
		}

		return new IfStmt(expr, stmtif, stmtelse);
	}

	//WhileStmt ::= ‘while’ ‘(’ Expr ‘)’ ‘{’ { Stmt } ‘}’
	public WhileStmt WhileStmt() {
		Expr expr;
		ArrayList<Stmt> stmt = new ArrayList<Stmt>();

		if(lexer.token != Symbol.WHILE)
			error.signal("Missing while");
		lexer.nextToken();

		if (lexer.token != Symbol.LEFTPAR)
			error.signal ("Missing (");
	    lexer.nextToken ();

		expr = Expr(true);
		
		// Verificao contra string
		if(expr instanceof StringFactor) {
			error.signal("String used outside of a assignment");
		}

		if(lexer.token != Symbol.RIGHTPAR)
			error.signal("Missing )");
		lexer.nextToken();

		if(lexer.token != Symbol.OPENBLOCK)
			error.signal("Missing {");
		lexer.nextToken();

		while(lexer.token != Symbol.CLOSEBLOCK) {
			stmt.add(Stmt(true)); // Valor sempre sera true, pq todos os statements daqui estao dentro de um while
		}

		if(lexer.token != Symbol.CLOSEBLOCK)
			error.signal("Missing }");
		lexer.nextToken();

		return new WhileStmt(expr, stmt);
	}

	//BreakStmt ::= ‘break’ ‘;’
	public BreakStmt BreakStmt(boolean flagWhile) {

		if(flagWhile == false)
			error.signal("Break used outside a while statement");

		if(lexer.token != Symbol.BREAK)
			error.signal("Missing break");
		lexer.nextToken();

		if(lexer.token != Symbol.SEMICOLON)
			error.signal("Missing ;");
		lexer.nextToken();

		return new BreakStmt();
	}

	//PrintStmt ::= ‘print’ ‘(’ Expr { ‘,’ Expr }‘)’ ‘;’
	public PrintStmt PrintStmt() {
		Expr expr;
		ArrayList<Expr> arrayExpr = new ArrayList<Expr>();

		if(lexer.token != Symbol.PRINT)
			error.signal("Missing print");
		lexer.nextToken();

		if(lexer.token != Symbol.LEFTPAR)
			error.signal("Missing (");
		lexer.nextToken();

		expr = Expr(true);

		while(lexer.token != Symbol.RIGHTPAR) {
			if(lexer.token != Symbol.COMMA)
				error.signal("Missing ,");
			lexer.nextToken();

			arrayExpr.add(Expr(true));
		}

		if(lexer.token != Symbol.RIGHTPAR)
			error.signal("Missing )");
		lexer.nextToken();

		if(lexer.token != Symbol.SEMICOLON)
			error.signal("Missing ;");
		lexer.nextToken();

		return new PrintStmt(expr, arrayExpr);
	}

    private ReturnStmt ReturnStmt() {
        
		if(lexer.token != Symbol.RETURN) {
			error.signal("Missing return");
		}
        lexer.nextToken();
		
		Expr expr = null;
		
		// Verifica se o return de void retorna algo que nao eh void
		if(currentFunction.getReturnType().getCname() == "void" && lexer.token != Symbol.SEMICOLON) {
			error.signal("Wrong returned type");
		}
		
		// Verifica se o return de funcoes nao void retornam void
		if(currentFunction.getReturnType().getCname() != "void" && lexer.token == Symbol.SEMICOLON) {
			error.signal("Wrong returned type");
		}
		
		// Verifica se a funcao tem alguma expressao de retorno
		if(lexer.token != Symbol.SEMICOLON) {
			// Manda flag true pq pode ter outros valores aqui alem de atribuicao
			expr = Expr(true);

			// Verificao contra string
			if(expr instanceof StringFactor) {
				error.signal("String used outside of a assignment");
			}
		
			// Verifica tipos
			String returnType = currentFunction.getReturnType().getCname();
			if( returnType != expr.getType().getCname() ) {
				error.signal("Wrong returned type");
			}
			
			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( currentFunction.getReturnType().getArrayPos() != -1 && expr.getType().getArrayPos() == -1 ) || ( currentFunction.getReturnType().getArrayPos() == -1 && expr.getType().getArrayPos() != -1 ) )
				error.signal("Wrong returned type");
		}
	
		// semantic analysis
		// Are we inside a function ?
        /*if ( currentFunction == null ) 
          //error.show("return statement inside a procedure");
        else if ( ! checkAssignment( currentFunction.getReturnType(), 
                                e.getType() ) )
            error.show("Return type does not match function type");
		*/
		
		if(lexer.token != Symbol.SEMICOLON) {
			error.signal("Missing ;");
		}
		lexer.nextToken();
		
        return new ReturnStmt(expr);
    }
	
	//Expr ::= SimExpr [ RelOp Expr]
	public Expr Expr(boolean flagExpr) {
		Expr simexpr;
		Symbol relOp = null;
		Expr expr = null;

		simexpr = SimExpr(flagExpr);

		// Verifica se tem RelOp
		// RelOp ::= ‘=’ | ‘!=’ | ‘<’ | ‘<=’ | ‘>’ | ‘>=’
		if(lexer.token == Symbol.ASSIGN || lexer.token == Symbol.NEQ || lexer.token == Symbol.LT || lexer.token == Symbol.LE || lexer.token == Symbol.GT || lexer.token == Symbol.GE ) {
			
			// Verificao contra string
			if(simexpr instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			relOp = lexer.token;
			lexer.nextToken();
			expr = Expr(true);

			// Verificao contra string
			if(expr instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			// Verificao de Tipo
			if(simexpr.getType().getCname() != expr.getType().getCname() )
				error.signal("Type error in relation operator");

			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( simexpr.getType().getArrayPos() != -1 && expr.getType().getArrayPos() == -1 ) || ( simexpr.getType().getArrayPos() == -1 && expr.getType().getArrayPos() != -1 ) )
				error.signal("Type error in assignment");

			simexpr = new CompositeExpr(null, simexpr, relOp, expr);
		}

		return simexpr;
	}

	//SimExpr ::= [Unary] Term { AddOp Term }
	public Expr SimExpr(boolean flagExpr) {
		Symbol unary = null;
		Expr term, result;

		// Verifica se tem Unary
		//Unary ::= ‘+’ | ‘-’ | ‘!’
		if(lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS || lexer.token == Symbol.NOT) {
			unary = lexer.token;
			lexer.nextToken(); // Unary
		}

		// Chamar termo
		term = Term(flagExpr);

		if(unary == Symbol.NOT && term.getType().getCname() == "double")
			error.signal("Invalid use of ! with double");

		//Expr simexpr = new SimExpr(unary, term);

		if(unary != null) {
			// Verificao contra string
			if(term instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
        	result = new CompositeExpr(unary, term, null, null);
		} else {
			result = term;
		}

		// Verifica se tem AddOp
		//AddOp ::= ‘+’ | ‘-’ | ‘||’
		while(lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS || lexer.token == Symbol.OR) {
			
			// Verificao contra string
			if(term instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			Symbol addOp = lexer.token;
			lexer.nextToken(); // AddOp

			Expr term_addOp = Term(true);

			// Verificao contra string
			if(term_addOp instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			// Verificacao de Tipo
			if(term.getType().getCname() != term_addOp.getType().getCname())
				error.signal("Type error in additive operators");

			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( term.getType().getArrayPos() != -1 && term_addOp.getType().getArrayPos() == -1 ) || ( term.getType().getArrayPos() == -1 && term_addOp.getType().getArrayPos() != -1 ) )
				error.signal("Type error in additive operators");

			result = new CompositeExpr(null, result, addOp, term_addOp);
		}

		return result;
	}

	//Term ::= Factor { MulOp Factor }
	public Expr Term(boolean flagExpr) {
		Expr term = Factor(flagExpr);

		// Impede o uso de % com double
		if( term.getType().getCname() == "double" && lexer.token == Symbol.REMAINDER )
			error.signal("Invalid use of % with double");

		// Verifica se tem MulOp
		//MulOp ::= ‘*’ | ‘/’ | ‘%’ | ‘&&’
		while(lexer.token == Symbol.MULT || lexer.token == Symbol.DIV || lexer.token == Symbol.REMAINDER || lexer.token == Symbol.AND) {
			
			// Verificao contra string
			if(term instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			Symbol mulOp = lexer.token;
			lexer.nextToken(); // MulOp
			Expr factor = Factor(true);

			// Verificao contra string
			if(factor instanceof StringFactor) {
				error.signal("String can not be used with this operator");
			}
			
			// Impede o uso de % com double
			if( factor.getType().getCname() == "double" && lexer.token == Symbol.REMAINDER )
				error.signal("Invalid use of % with double");

			// Verificacao de Tipo
			if(term.getType().getCname() != factor.getType().getCname())
				error.signal("Type error in multiplication operators");

			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( term.getType().getArrayPos() != -1 && factor.getType().getArrayPos() == -1 ) || ( term.getType().getArrayPos() == -1 && factor.getType().getArrayPos() != -1 ) )
				error.signal("Type error in multiplication operators");

			term = new CompositeExpr(null, term, mulOp, factor);
		}

		return term;
	}

	/* Factor ::=   LValue ‘:=’ Expr |
                    LValue |
                    Number |
					‘(’ Expr ‘)’ |
					‘readInteger’ ‘(’ ‘)’ |
					‘readDouble’ ‘(’ ‘)’ |
					‘readChar’ ‘(’ ‘)’
    */
	public Expr Factor(boolean flagExpr) {

		// Impede que use expressoes que não fazem parte de uma atribuição ou que não se encontram na expr de um if, while ou print
		if(flagExpr == false && lexer.token != Symbol.IDENT)
			error.signal("Expression has to be in a atribution");

		switch(lexer.token) {
			case PLUS:
			case MINUS:
			case NUMBER:
				return Number();
			case LEFTPAR:
				lexer.nextToken();
				Expr expr = Expr(flagExpr);
				
				// Verificao contra string
				if(expr instanceof StringFactor) {
					error.signal("String used outside of a assignment");
				}
				
				Expr factor = new ExprFactor(expr);
				if(lexer.token != Symbol.RIGHTPAR)
					error.signal("Missing )");
				lexer.nextToken();
				return factor;
			case READINTEGER:
				lexer.nextToken();
				if(lexer.token != Symbol.LEFTPAR)
					error.signal("Missing (");
				lexer.nextToken();
				if(lexer.token != Symbol.RIGHTPAR)
					error.signal("Missing )");
				lexer.nextToken();
				return new ReadIntegerFactor();
			case READDOUBLE:
				lexer.nextToken();
				if(lexer.token != Symbol.LEFTPAR)
					error.signal("Missing (");
				lexer.nextToken();
				if(lexer.token != Symbol.RIGHTPAR)
					error.signal("Missing )");
				lexer.nextToken();
				return new ReadDoubleFactor();
			case READCHAR:
				lexer.nextToken();
				if(lexer.token != Symbol.LEFTPAR)
					error.signal("Missing (");
				lexer.nextToken();
				if(lexer.token != Symbol.RIGHTPAR)
					error.signal("Missing )");
				lexer.nextToken();
				return new ReadCharFactor();
			case IDENT:
				// verifica se o ident tem o nome de uma funcao
				if(symbolTable.getInGlobal(lexer.getStringValue()) != null ) {
					return Call();
				}
				return LValueFactor(flagExpr);
			case CHARACTER:
				char character = lexer.getCharValue();
				lexer.nextToken();
				return new CharFactor(character);
			case STRING:
				String string = lexer.getStringValue();
				lexer.nextToken();
				return new StringFactor(string);
			default:
				error.signal("Invalid entry");
		}

		return null;
	}

	public LValueFactor LValueFactor(boolean flagExpr) {
		LValue lvalue = LValue();
		Expr expr = null;

		if(lexer.token == Symbol.COLONEQUAL) {
			flagExpr = true; // Agora pode usar expr pq eles fazem parte de uma atribuicao
			lexer.nextToken();
			expr = Expr(flagExpr);

			// Verificacao de tipo
			if(lvalue.getType().getCname() != expr.getType().getCname())
				error.signal("Type error in assignment");
			
			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( lvalue.getType().getArrayPos() != -1 && expr.getType().getArrayPos() == -1 ) || ( lvalue.getType().getArrayPos() == -1 && expr.getType().getArrayPos() != -1 ) )
				error.signal("Type error in assignment");
			else {
				lvalue.getType().clearArrayPos();
				expr.getType().clearArrayPos();
			}
			
			// Verificacao de tamanho com string na atribuicao seguindo informacoes dadas pela tiemi.
			// Segundo ela o tamanho do vetor de arrays deve ser no minimo a quantidade de chars contidos na string + 1 para o \0
			if(expr instanceof StringFactor) {
				if(lvalue.getType().getArraySize() <= expr.getType().getArraySize()) {
					error.signal("Char vector too small for string assignment");
				}
			}
			
		}

		// Verifica se nao havera uma atribuicao com essa variavel, se nao tiver eh para dar erro
		if(flagExpr == false)
			error.signal("Variable has to be in a atribution");

		return new LValueFactor(lvalue, expr);
	}

	//LValue ::= Ident | Ident ‘[’ Expr ‘]’
	public LValue LValue() {
		String ident = lexer.getStringValue();
		Expr expr = null;

		lexer.nextToken();

		// Verifica se existe uma variavel com esse valor
		Variable var = (Variable) symbolTable.getInLocal(ident);
		if( var == null)
			error.signal("Variable not declared");

		if(lexer.token == Symbol.LEFTBRACKET) {
			lexer.nextToken();

			// Pega o valor final da array
			expr = Expr(true);

			// Verifica se a variavel eh uma array
			if(var.getArraySize() <= 0)
				error.signal("Variable is not an array");

			// Verifica se o type da expr eh integer
			if(expr.getType().getCname() != "int")
				error.signal("Only integer allowed");

			// Como nao sabemos o resultado de expr nesse momento estamos setando o valor do offset do ponteiro como 1 so para realizar a analise lexica
			var.getType().setArrayPos(1);

			if(lexer.token != Symbol.RIGHTBRACKET)
				error.signal("Missing ]");
			lexer.nextToken();
		} else {
			// Verifica se a variavel eh uma array
			if(var.getArraySize() > 0) {
				var.getType().setArrayPos(-1);
			}
		}

		return new LValue(var, expr);
	}

	public NumberFactor Number() {

		int oper = 1;
		if(lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
			if(lexer.token == Symbol.MINUS)
				oper = -1;
			lexer.nextToken();
		}

		int decimal = lexer.getNumberValue();

		// Garante que salva se o valor é negativo ou positivo
		decimal = decimal * oper;

		// Sai do primeiro numero
		lexer.nextToken();

		int fracional = -1;
		if(lexer.token == Symbol.DOT) {
			lexer.nextToken();
			if(lexer.token != Symbol.NUMBER)
				error.signal("Missing fractional number");
			fracional = lexer.getNumberValue();
			lexer.nextToken();
		}

		return new NumberFactor(decimal, fracional);
	}
	
    private Call Call() {
        ArrayList<Expr> exprList = null;
        
		if(lexer.token != Symbol.IDENT) {
			error.signal("Missing ident");
		}
        String funcName = lexer.getStringValue();
        lexer.nextToken();
		
        Function function = (Function) symbolTable.getInGlobal(funcName);
		if(function == null) {
			error.signal("Function not declared");
		}
		
        if ( lexer.token != Symbol.LEFTPAR ) {
          error.signal("Missing (");
        }
		lexer.nextToken();
        
		exprList = new  ArrayList<Expr>();
		
		ArrayList<Variable> param_list = function.getParamList().getParamList();
		for( Variable v : param_list ) {
			
			if(lexer.token == Symbol.RIGHTPAR) {
				error.signal("Wrong number of parameters");
			}
			
			Expr expr = Expr(true); // Pode ter outras funcoes que nao é só atribuicao
			
			// Verificao contra string
			if(expr instanceof StringFactor) {
				error.signal("String used outside of a assignment");
			}
			
			// Verificacao de Tipo
			if(v.getType().getCname() != expr.getType().getCname())
				error.signal("Type error in parameter at function call");
			
			// Limpa lixo da posicao salva da array nos parametros que se encontram em outra funcao
			if(v.getArraySize() > 0) {
				v.getType().setArrayPos(-1); // Seta ela como ponteiro
			}

			// Verificacao de atribuiçao entre ponteiro e variavel simples
			if( ( v.getType().getArrayPos() != -1 && expr.getType().getArrayPos() == -1 ) || ( v.getType().getArrayPos() == -1 && expr.getType().getArrayPos() != -1 ) )
				error.signal("Type error in parameter at function call");
			
			exprList.add(expr);
			
			if(lexer.token == Symbol.COMMA) {
				lexer.nextToken();
			}
		}
		
		if(lexer.token != Symbol.RIGHTPAR) {
			error.signal("Missing ) or wrong number of parameters");
		}
		lexer.nextToken();
		
		return new Call(function, exprList);
    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private CompilerError error;
	private Function currentFunction;
}
