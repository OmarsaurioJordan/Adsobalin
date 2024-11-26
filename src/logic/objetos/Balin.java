package logic.objetos;
// proyectil proveniente de sincronizacion

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.abstractos.Movil;

public class Balin extends Proyectil {
    
    private boolean isFromNPC = false;
    
    private Image sprite;
    
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
                // hacer damage directamente y enviar solicitud para puntos
                Movil mov = (Movil)otro;
                mov.angHit = angulo;
                if (mov.golpear(origen)) {
                    
                }
                else {
                    
                }
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
