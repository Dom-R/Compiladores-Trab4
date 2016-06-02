/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class BreakStmt extends Stmt {
    public void genC(PW pw) {
        pw.println("break;");
    }
}