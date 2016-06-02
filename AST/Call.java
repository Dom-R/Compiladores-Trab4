package AST;

import java.io.*;
import java.util.*;

public class Call extends Expr {
    
    public Call( Function function, ArrayList<Expr> exprList ) {
        this.function = function;
        this.exprList = exprList;
    }
    
    public void genC( PW pw ) {
        pw.print( function.getName() + "(" );
        if ( exprList != null ) {
			for( Expr e : exprList ) {
				e.genC(pw);
			}
		}
        pw.out.println(");");
    }
    
	public Type getType() {
		return function.getReturnType();
	}
	
    private Function function;
    private ArrayList<Expr> exprList;
}