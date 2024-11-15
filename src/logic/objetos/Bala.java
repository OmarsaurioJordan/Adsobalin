package logic.objetos;
// proyectil generado por la misma maquina, no sincronizado

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.interfaz.Adsobalin;

public class Bala extends Proyectil {
    
    private Image sprite = new Image("assets/azules/azulproyectil.png",
        110f * 0.75f * (float)Adsobalin.ESCALA,
        110f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    public Bala(float[] posicion) {
        super(posicion, Objeto.OBJ_BALA);
    }
    
    public void setProyectil(float angulo, int grupo, int origen) {
        sprite = setProyectilImg(angulo, grupo, origen);
    }
    
    @Override
    public void step(float delta) {
        Object otro = avanzar(delta, true);
        if (otro != null) {
            // cuando el proyectil choca con un movil, debe golpearlo
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
