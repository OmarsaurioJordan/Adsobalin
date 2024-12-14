package logic.objetos;
// obstaculo decorativo

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Solido;
import logic.interfaz.Adsobalin;

public class Arbol extends Solido {
    
    private Image sprite = new Image("assets/entorno/arbol.png",
        160f * 0.75f, 160f * 0.75f, false, false);
    
    public Arbol(float[] posicion) {
        super(posicion, Objeto.OBJ_ARBOL);
        angulo = Adsobalin.DADO.nextFloat((float)Math.PI * 2f);
    }
    
    @Override
    public void step(float delta) {}
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
