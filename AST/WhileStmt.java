/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;
import java.util.ArrayList;

public class WhileStmt extends Stmt {
	private Expr expr;
	private ArrayList<Stmt> stmt;

	public WhileStmt(Expr expr, ArrayList<Stmt> stmt) {
		this.expr = expr;
		this.stmt = stmt;
	}

    public void genC(PW pw) {
        pw.print("while(");
        expr.genC(pw);
        pw.out.println(") {");
        pw.add();
        for(Stmt st: stmt) {
            st.genC(pw);
            if(st instanceof Expr) {
                pw.out.println(";");
            }
        }
        pw.sub();
        pw.println("}");
    }


}
