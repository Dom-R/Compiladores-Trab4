/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class ExprFactor extends Expr {
    private Expr expr;

    public ExprFactor(Expr expr) {
        this.expr = expr;
    }

    public Type getType() {
        return expr.getType();
    }
    
    public void genC(PW pw) {
        pw.out.print("(");
        expr.genC(pw);
        pw.out.print(")");
    }

}
