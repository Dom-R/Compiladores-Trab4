/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

import java.util.*;
import java.io.*;

public class Decl {

    private ArrayList<Function> functionList;

    public Decl(ArrayList<Function> functionList) {
        this.functionList = functionList;
    }
    
    public void genC(PW pw) {
        pw.println("#include <stdio.h>");
        pw.println("#include <stdlib.h>");
        pw.println("#include <string.h>");
		
        for( Function f : functionList ) {
			f.genC(pw);
		}
    }

    //Gets e Sets

}
