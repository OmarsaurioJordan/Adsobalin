package logic.abstractos;
// todos los objetos que no pueden ser traspasados

import logic.interfaz.Adsobalin;

public abstract class Solido extends Objeto {
    
    // radio para todos los bloques colisionables
    public static float RADIO = 18f * (float)Adsobalin.ESCALA;
    
    public Solido(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
    }
}
