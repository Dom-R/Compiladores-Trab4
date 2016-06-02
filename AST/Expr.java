/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

abstract public class Expr extends Stmt {
    
    abstract public void genC(PW pw);
    
    abstract public Type getType();
    
}