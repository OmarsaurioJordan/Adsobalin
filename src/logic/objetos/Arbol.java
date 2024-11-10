package logic.objetos;
// obstaculo decorativo

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Solido;
import logic.interfaz.Adsobalin;
import logic.abstractos.Tools;
import logic.interfaz.Mundo;

public class Arbol extends Solido {
    
    private Image sprite = new Image("assets/entorno/arbol.png",
        160f * 0.75f * (float)Adsobalin.ESCALA,
        160f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    public Arbol(float[] posicion) {
        super(posicion, Objeto.OBJ_ARBOL);
        desfase[0] = (float)sprite.getWidth() / 2f;
        desfase[1] = desfase[0];
        angulo = Adsobalin.DADO.nextFloat((float)Math.PI * 2f);
    }
    
    @Override
    public void step(float delta) {
        // Quitar
        angulo += 0.01f;
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenExt(gc, sprite,
                Tools.vecResta(posicion, Mundo.camaraPos),
                angulo);
        /*drawImagen(gc, sprite, Tools.vecResta(
                Tools.vecResta(posicion, Mundo.camaraPos),
                desfase));*/
    }
}
