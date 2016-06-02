/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

import Lexer.*;

public class CompositeExpr extends Expr {

    private Expr leftExpr, rightExpr;
    private Symbol operator, unary;

    public CompositeExpr(Symbol unary, Expr leftExpr, Symbol operator, Expr rightExpr) {
        this.unary = unary;
        this.leftExpr = leftExpr;
        this.operator = operator;
        this.rightExpr = rightExpr;
    }

    public Type getType() {
		return leftExpr.getType();
	}

    public void genC(PW pw) {
        if(unary != null) {
            pw.out.print(unary.toString());
        }
        leftExpr.genC(pw);
        if(operator != null) {
            if(operator == Symbol.ASSIGN) {
                pw.out.print("==");
            } else {
                pw.out.print(operator.toString());
            }
            rightExpr.genC(pw);
        }
    }

}
