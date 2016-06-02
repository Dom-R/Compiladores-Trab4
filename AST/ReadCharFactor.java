/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class ReadCharFactor extends Expr {

    public Type getType() {
        return new CharType();
    }

    public void genC(PW pw) {
        pw.print("scanf(\"%c\", &");
    }

}
