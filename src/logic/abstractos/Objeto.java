package logic.abstractos;
// clase base para todos los objetos que hay en el mundo

public abstract class Objeto {
    
    // define en que punto x,y se encuentra
    protected float[] posicion = {0f, 0f};
    // especifica el orden de dibujado
    // 1:baldoza, 2:cadaver, 3:balin, 4:bala, 5:automata
    // 6:sombra, 7:player, 8:arbol, 9:base
    protected int depth = 0;
    // define el circulo de colision, todas son circulares
    protected float radio = 0f;
    // la direccion en la que apunta el sprite principal
    protected float angulo = 0f;
    
    public Objeto(float[] posicion, int depth, float radio) {
        this.posicion = posicion;
        this.depth = depth;
        this.radio = radio;
    }
    
    public float[] getPosicion() {
        return posicion;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public float getRadio() {
        return radio;
    }
    
    public float getAngulo() {
        return angulo;
    }
    
    public abstract void draw();
}
