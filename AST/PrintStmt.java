/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;
import java.util.ArrayList;

public class PrintStmt extends Stmt {
	private Expr expr;
	private ArrayList<Expr> arrayExpr;

	public PrintStmt(Expr expr, ArrayList<Expr> arrayExpr) {
		this.expr = expr;
		this.arrayExpr = arrayExpr;
	}

    public void genC(PW pw) {
        pw.print("printf(\"%");
		switch(expr.getType().getCname()) {
			case "int":
				pw.out.print("d");
				break;
			case "double":
				pw.out.print("lf");
				break;
			case "char":
				pw.out.print("c");
				break;
		}
		pw.out.print("\", ");
		expr.genC(pw);
		pw.out.println(");");
    }

}
