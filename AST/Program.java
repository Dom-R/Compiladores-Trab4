/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;
import java.util.ArrayList;

public class Program {
    
    private Decl decl;
    
    public Program(Decl decl) {
        this.decl = decl;
    }
    
    public void genC(PW pw) {
        decl.genC(pw);
    }
    
}