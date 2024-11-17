package logic.objetos;
// proyectil proveniente de sincronizacion

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.abstractos.Movil;
import logic.interfaz.Adsobalin;

public class Balin extends Proyectil {
    
    private boolean isFromNPC = false;
    
    private Image sprite = new Image("assets/azules/azulproyectil.png",
        110f * 0.75f * (float)Adsobalin.ESCALA,
        110f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    public Balin(float[] posicion) {
        super(posicion, Objeto.OBJ_BALIN);
    }
    
    public void setProyectil(float angulo, int grupo,
            int origen, boolean isFromNPC) {
        // el origen es el Movil.indice de quien lo lanzo
        sprite = setProyectilImg(angulo, grupo, origen);
        this.isFromNPC = isFromNPC;
    }
    
    @Override
    public void step(float delta) {
        Object otro = avanzar(delta);
        if (otro != null) {
            // cuando el proyectil choca con un movil, debe golpearlo
            if (otro.getClass() == Player.class && isFromNPC) {
                // hacer damage directamente
                Movil mov = (Movil)otro;
                mov.golpear(origen);
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
