/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

public class IntegerType extends Type {
    
    public IntegerType() {
        super("integer");
    }
    
   public String getCname() {
      return "int";
   }
   
}