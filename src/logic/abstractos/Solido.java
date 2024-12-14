package logic.abstractos;
// todos los objetos que no pueden ser traspasados

public abstract class Solido extends Objeto {
    
    // radio para todos los bloques colisionables
    public static float RADIO = 18f;
    
    public Solido(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
    }
}
