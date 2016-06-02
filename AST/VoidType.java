/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class VoidType extends Type {
    
   public VoidType() { super("void"); }
   
   public String getCname() {
      return "void";
   }
}
