/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class StringFactor extends Expr {
	private String string;
	private Type type;
	
	public StringFactor(String string) {
		this.string = string;
		this.type = new CharType();
		this.type.setArraySize(string.length());
	}

	public Type getType() {
		return type;
	}
    
    public void genC(PW pw) {
        pw.out.print("\"" + string + "\"");
    }

}