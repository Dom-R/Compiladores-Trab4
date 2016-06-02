/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class LValue {
	private Expr expr;
	private Variable variable;

    public LValue(Variable variable, Expr expr) {
    	this.expr = expr;
    	this.variable = variable;
    }

	public Type getType() {
		return variable.getType();
	}
    
    public void genC(PW pw) {
        pw.out.print(variable.getName());
        if(expr != null) {
            pw.out.print("[");
            expr.genC(pw);
            pw.out.print("]");
        }
    }
    
}
