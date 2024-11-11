package logic.objetos;
// decorado del fondo de la pantalla, para personalizacion

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Decorado;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;

public class Baldoza extends Decorado {
    
    private Image sprite = new Image("assets/entorno/baldoza" +
        Adsobalin.DADO.nextInt(12) + ".png",
        80f * 0.75f * (float)Adsobalin.ESCALA,
        80f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    public Baldoza(float[] posicion) {
        super(posicion, Objeto.OBJ_BALDOZA);
        angulo = Adsobalin.DADO.nextFloat((float)Math.PI * 2f);
    }
    
    @Override
    public void step(float delta) {}
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
