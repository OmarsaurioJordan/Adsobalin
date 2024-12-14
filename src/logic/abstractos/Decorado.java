package logic.abstractos;
// todo lo que es decorativo y usualmente va al fondo del mundo

public abstract class Decorado extends Objeto {
    
    // radio para todos los decorados aunque no son colisionables
    public static float RADIO = 9f;
    
    public Decorado(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
    }
}
