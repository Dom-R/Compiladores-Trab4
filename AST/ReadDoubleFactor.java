/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class ReadDoubleFactor extends Expr {

    public Type getType() {
        return new DoubleType();
    }

    public void genC(PW pw) {
        pw.print("scanf(\"%lf\", &");
    }

}
