package logic.abstractos;
// todo lo que es decorativo y usualmente va al fondo del mundo

import logic.interfaz.Adsobalin;

public abstract class Decorado extends Objeto {
    
    public Decorado(float[] posicion, int myTipo) {
        super(posicion, myTipo, 10f * (float)Adsobalin.ESCALA);
    }
}
