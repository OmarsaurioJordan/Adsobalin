package logic.objetos;
// representa un punto de respawn para entes

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Solido;
import logic.interfaz.Adsobalin;

public class Base extends Solido {
    
    private Image sprite = new Image("assets/azules/azulbase.png",
        170f * 0.75f * (float)Adsobalin.ESCALA,
        170f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    // grupo al que pertenece
    public int grupo = Adsobalin.GRU_AZUL;
    
    public Base(float[] posicion) {
        super(posicion, Objeto.OBJ_BASE);
    }
    
    public void setGrupo(boolean isAzul) {
        String bcol = "rojos/rojo";
        if (isAzul) {
            bcol = "azules/azul";
        }
        sprite = new Image("assets/" + bcol + "base.png",
            170f * 0.75f * (float)Adsobalin.ESCALA,
            170f * 0.75f * (float)Adsobalin.ESCALA,
            false, false);
    }
    
    @Override
    public void step(float delta) {}
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagen(gc, sprite, posicion);
    }
}
