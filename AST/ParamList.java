package AST;

import java.io.*;
import java.util.*;

public class ParamList {
    
    public ParamList() {
        v = new ArrayList<Variable>();
    }
    
    public void addElement( Variable parameter) {
        v.add(parameter);
    }
    
    public int getSize() {
        return v.size();
    }
    
/*    public Enumeration elements() {
        return v.elements();
    }
 *
 */
    public ArrayList<Variable> getParamList() {
        return v;
    }
    
    public void genC( PW pw ) {
        Variable p;
        Iterator e = v.iterator();
        int size = v.size();
        while ( e.hasNext() ) {
          p = (Variable ) e.next();
          pw.out.print( p.getType().getCname() + " " + p.getName() );
          
          if ( --size > 0 )
            pw.out.print(", ");
        }
    }
    
    private ArrayList<Variable> v;
}
