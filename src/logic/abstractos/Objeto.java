package logic.abstractos;
// clase base para todos los objetos que hay en el mundo

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;

public abstract class Objeto {
    
    // define en que punto x,y se encuentra
    public float[] posicion = {0f, 0f};
    // define el circulo de colision, todas son circulares
    public float radio = 0f;
    // la direccion en la que apunta el sprite principal
    public float angulo = 0f;
    
    // al dibujar la imagen, define el desfase sprite vs posicion x,y
    protected float[] desfase = {0f, 0f};
    
    // constantes para tipo de objeto y depth
    public static final int OBJ_BALDOZA = 0;
    public static final int OBJ_CADAVER = 1;
    public static final int OBJ_BALIN = 2;
    public static final int OBJ_BALA = 3;
    public static final int OBJ_AUTOMATA = 4;
    public static final int OBJ_SOMBRA = 5;
    public static final int OBJ_PLAYER = 6;
    public static final int OBJ_ARBOL = 7;
    public static final int OBJ_BASE = 8;
    
    // especifica el orden de dibujado
    public int myTipo = -1;
    
    public Objeto(float[] posicion, int myTipo, float radio) {
        this.posicion = posicion;
        this.myTipo = myTipo;
        this.radio = radio;
    }
    
    private static Affine getTrans(Image img,
            float[] posicion, float angulo) {
        Affine trans = new Affine();
        // trasladar la imagen al punto central
        trans.appendTranslation(posicion[0] + img.getWidth() / 2f,
                posicion[1] + img.getHeight() / 2f);
        // hacer la rotacion
        trans.appendRotation(angulo * 57.2958f, 0f, 0f);
        // centrar la imagen nuevamente
        trans.appendTranslation(-img.getWidth() / 2f, -img.getHeight() / 2f);
        return trans;
    }
    
    private static void drawTrans(GraphicsContext gc,
            Image img, float[] posicion, Affine trans) {
        gc.save();
        gc.setTransform(trans);
        gc.drawImage(img, posicion[0], posicion[1]);
        gc.restore();
    }
    
    public static void drawImagenExt(GraphicsContext gc,
            Image img, float[] posicion, float angulo) {
        Affine trans = getTrans(img, posicion, angulo);
        drawTrans(gc, img, posicion, trans);
    }
    
    public static void drawImagen(GraphicsContext gc,
            Image img, float[] posicion) {
        gc.drawImage(img, posicion[0], posicion[1]);
    }
    
    public abstract void step(float delta);
    
    public abstract void draw(GraphicsContext gc);
}
