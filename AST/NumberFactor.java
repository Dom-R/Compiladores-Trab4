/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class NumberFactor extends Expr {
    private int decimal;
    private int fracional;

    public NumberFactor(int decimal, int fracional) {
        this.decimal = decimal;
        this.fracional = fracional;
    }

    public Type getType() {
        if(fracional == -1)
            return new IntegerType();
        else
            return new DoubleType();
    }
    
    public void genC(PW pw) {
        pw.out.print(decimal);
        if(fracional != -1) {
            pw.out.print("." + fracional);
        }
    }

}
