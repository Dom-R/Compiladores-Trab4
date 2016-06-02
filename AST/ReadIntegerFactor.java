/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class ReadIntegerFactor extends Expr {

    public Type getType() {
        return new IntegerType();
    }

    public void genC(PW pw) {
        pw.print("scanf(\"%d\", &");
    }

}
