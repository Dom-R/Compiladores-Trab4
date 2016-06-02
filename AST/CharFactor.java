/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class CharFactor extends Expr {
	private char character;

	public CharFactor(char character) {
		this.character = character;
	}

	public Type getType() {
		return new CharType();
	}
    
    public void genC(PW pw) {
        pw.out.print("'" + character + "'");
    }

}
