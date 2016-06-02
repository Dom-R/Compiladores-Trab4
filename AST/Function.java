package AST;

import java.io.*;

public class Function {
    
    public Function( Type returnType, String name ) {
		this.returnType = returnType;
        this.name = name;
    }
	
    public String getName() {
        return name;
    }
    
    public void setParamList( ParamList paramList ) {
        this.paramList = paramList;
    }
    
    public ParamList getParamList() {
        return paramList;
    }
    
    public void setStatementBlock( StmtBlock statementBlock ) {
        this.statementBlock = statementBlock;
    }
	
    public Type getReturnType() {
        return returnType;
    }
    
    public void genC( PW pw ) {
        
		if(returnType != null)
			pw.out.print(returnType.getCname() + " " + name + "(");
		else
			pw.out.print("void" + " " + name + "(");
        if ( paramList != null ) 
          paramList.genC(pw);
        pw.out.println(") {");
        pw.add();
        pw.out.println();
        statementBlock.genC(pw);
        pw.sub();
        pw.out.println("}");
        
    }
    
    // fields should be accessible in subclasses
    private String name;
    private StmtBlock statementBlock;
    private ParamList paramList;
    private Type returnType; // null significa que eh void
}