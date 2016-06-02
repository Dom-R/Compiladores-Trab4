/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;
import java.util.ArrayList;

public class StmtBlock {
	private ArrayList<Variable> variableDecl;
	private ArrayList<Stmt> stmt;

	public StmtBlock(ArrayList<Variable> variableDecl, ArrayList<Stmt> stmt ) {
		this.variableDecl = variableDecl;
		this.stmt = stmt;
	}
    
    public void genC(PW pw) {
        for( Variable v : variableDecl ) {
            v.genC(pw);
        }
        for( Stmt st : stmt ) {
            st.genC(pw);
            if(st instanceof Expr) {
                pw.out.println(";");
            }
        }
    }

}
