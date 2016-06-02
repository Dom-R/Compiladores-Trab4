/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

//Stmt ::= Expr ‘;’ | ifStmt | WhileStmt | BreakStmt | PrintStmt
abstract public class Stmt {
    
    abstract public void genC(PW pw);
    
}