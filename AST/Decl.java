/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class Decl {

    private StmtBlock stmtBlock;

    public Decl(StmtBlock stmtBlock) {
        this.stmtBlock = stmtBlock;
    }
    
    public void genC(PW pw) {
        pw.println("#include <stdio.h>");
        pw.println("#include <stdlib.h>");
        pw.println("void main() {");
        pw.add();
        stmtBlock.genC(pw);
        pw.sub();
        pw.println("}");
    }

    //Gets e Sets

}
