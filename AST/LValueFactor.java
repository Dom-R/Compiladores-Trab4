/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class LValueFactor extends Expr {
    private LValue lValue;
    private Expr expr;

    public LValueFactor(LValue lValue, Expr expr) {
        this.lValue = lValue;
        this.expr = expr;
    }

    public Type getType() {
        return lValue.getType();
    }

    public void genC(PW pw) {
        if(expr != null) {
            if(expr instanceof ReadDoubleFactor || expr instanceof ReadCharFactor || expr instanceof ReadIntegerFactor ) {
                expr.genC(pw);
                lValue.genC(pw);
                pw.out.print(")");
            } else if(expr instanceof StringFactor) { 
				pw.print("strcpy(");
				lValue.genC(pw);
				pw.out.print(", ");
				expr.genC(pw);
				pw.out.println(");");
			} else {
                pw.print("");
                lValue.genC(pw);
                pw.out.print("=");
                expr.genC(pw);
            }
        } else {
            lValue.genC(pw);
        }
    }

}
