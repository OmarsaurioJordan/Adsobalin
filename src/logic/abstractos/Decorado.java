package logic.abstractos;
// todo lo que es decorativo y usualmente va al fondo del mundo

import logic.interfaz.Adsobalin;

public abstract class Decorado extends Objeto {
    
    // radio para todos los decorados aunque no son colisionables
    public static float RADIO = 9f * (float)Adsobalin.ESCALA;
    
    public Decorado(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
    }
}
