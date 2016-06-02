/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;
import java.util.ArrayList;

//IfStmt ::= ‘f’ ‘(’ Expr ‘)’ ‘{’ { Stmt } ‘}’ [ ‘e’ ‘{’ { Stmt } ‘}’ ]
public class IfStmt extends Stmt {
	private Expr expr;
	private ArrayList<Stmt> stmtIf;
	private ArrayList<Stmt> stmtElse;

    public IfStmt (Expr expr, ArrayList<Stmt> stmtIf, ArrayList<Stmt> stmtElse) {
    	this.expr = expr;
    	this.stmtIf = stmtIf;
    	this.stmtElse = stmtElse;
    }

    //Gets e Sets
    public void genC(PW pw) {
        pw.print("if(");
        expr.genC(pw);
        pw.out.println(") {");
        pw.add();
        for(Stmt st : stmtIf) {
            st.genC(pw);
            if(st instanceof Expr) {
                pw.out.println(";");
            }
        }
        pw.sub();
        if(stmtElse != null) {
            pw.println("} else {");
            pw.add();
            for(Stmt st : stmtElse) {
                st.genC(pw);
	            if(st instanceof Expr) {
	                pw.out.println(";");
	            }
            }
            pw.sub();
        }
        pw.println("}");
    }
}
