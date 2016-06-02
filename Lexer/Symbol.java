/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package Lexer;

public enum Symbol {

      EOF("eof"),
      IDENT("Ident"),
      NUMBER("Number"),
      PLUS("+"),
      MINUS("-"),
      MULT("*"),
      DIV("/"),
	  REMAINDER("%"),
      LT("<"),
      LE("<="),
      GT(">"),
      GE(">="),
	  ASSIGN("="),
	  EQ("=="),
      NOT("!"),
      NEQ("!="),
	  OR("||"),
	  AND("&&"),
      LEFTPAR("("),
      RIGHTPAR(")"),
	  OPENBLOCK("{"),
      CLOSEBLOCK("}"),
	  LEFTBRACKET("["),
	  RIGHTBRACKET("]"),
	  COMMA(","),
	  DOT("."),
	  SEMICOLON(";"),
      COLONEQUAL(":="),
      IF("if"),
      ELSE("else"),
      VOID("void"),
      MAIN("main"),
      INT("int"),
      CHAR("char"),
      DOUBLE("double"),
      WHILE("while"),
      BREAK("break"),
      PRINT("print"),
      READINTEGER("readInteger"),
      READDOUBLE("readDouble"),
      READCHAR("readChar"),
      UNDERLINE("_"),
	  CHARACTER("'"),
	  STRING("\""),
	  RETURN("return");

      Symbol(String name) {
          this.name = name;
      }

      public String toString() {
          return name;
      }

      private String name;

}
