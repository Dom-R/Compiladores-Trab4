/*
Dominik Reller 587516
Luan Maia Dias 587737
*/
package AST;

abstract public class Type {

    public Type( String name ) {
        this.name = name;
        this.arraySize = 0;
		this.arrayPos = -2;
    }

    //public static Type doubleType = new DoubleType();
    //public static Type integerType = new IntegerType();
    //public static Type charType    = new CharType();

    abstract public String getCname();

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
		this.arrayPos = -1;
    }

    public int getArraySize() {
        return arraySize;
    }

	public int getArrayPos() {
		return arrayPos;
	}

	public void setArrayPos(int arrayPos) {
		this.arrayPos = arrayPos;
	}

	public void clearArrayPos() {
		if(arrayPos != -2)
			arrayPos = -1;
	}

    private String name;
    private int arraySize; // Tamanho da array. So eh uma array se o valor de arraySize e maior que 0
	private int arrayPos; // -2 se não é uma array, -1 se é uma array e qualquer outro valor >= 0 é o offset do ponteiro para o valor
}
