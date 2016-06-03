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
		// Printa primeira parte da expr de print
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
			case "string":
				pw.out.print("s");
				break;
		}
		// Printa resto das expressoes que se encontram na arrayExpr
		for( Expr e : arrayExpr ) {
			pw.out.print(" %");
			switch(e.getType().getCname()) {
				case "int":
					pw.out.print("d");
					break;
				case "double":
					pw.out.print("lf");
					break;
				case "char":
					pw.out.print("c");
					break;
				case "string":
					pw.out.print("s");
					break;
			}
		}
		pw.out.print("\", ");
		expr.genC(pw);
		for( Expr e : arrayExpr ) {
			pw.out.print(", ");
			e.genC(pw);
		}
		pw.out.println(");");
    }

}
