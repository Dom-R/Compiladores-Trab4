/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class Variable {

	private Type type;
	private String ident;

	public Variable(Type type, String ident) {
		this.type = type;
		this.ident = ident;
	}

	public int getArraySize() {
		return type.getArraySize();
	}

	public Type getType() {
		return type;
	}
    
    public String getName() {
        return ident;
    }
    
    public void genC(PW pw) {
        pw.print(type.getCname() + " " + ident);
        int size = getArraySize();
        if( size > 0 ) {
            pw.out.print("[" + getArraySize() + "]");
        }
        pw.out.println(";");
    }
    
}
