/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

import Lexer.*;
import java.io.*;

public class CompilerError {

    public CompilerError( PrintWriter out, String nomeArq ) {
          // output of an error is done in out
        this.out = out;
		this.nomeArq = nomeArq;
    }

    public void setLexer( Lexer lexer ) {
        this.lexer = lexer;
    }

    public void signal( String strMessage ) {
        out.println("\n" + nomeArq + ":" + lexer.getLineNumber() + ":" + strMessage);
        out.print(lexer.getCurrentLine());
        if ( out.checkError() )
          System.out.println("Error in signaling an error");
        throw new RuntimeException(strMessage);
    }

    private Lexer lexer;
    PrintWriter out;
	private String nomeArq;
}
