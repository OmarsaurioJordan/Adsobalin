package logic.objetos;
// proyectil proveniente de sincronizacion

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.abstractos.Movil;
import logic.interfaz.Adsobalin;
import logic.sincronia.Envios;

public class Balin extends Proyectil {
    
    private Image sprite;
    
    public Balin(float[] posicion) {
        super(posicion, Objeto.OBJ_BALIN);
    }
    
    public void setProyectil(float angulo, int grupo,
            int origen, boolean isFromNPC, int llave) {
        // el origen es el Movil.indice de quien lo lanzo
        sprite = setProyectilImg(angulo, grupo, origen, isFromNPC);
        this.llave = llave;
    }
    
    @Override
    public void step(float delta) {
        Object otro = avanzar(delta);
        if (otro != null) {
            // cuando el proyectil choca con un movil, debe golpearlo
            if (otro.getClass() == Player.class && isFromNPC) {
                // hacer damage directamente y enviar solicitud para puntos
                Movil mov = (Movil)otro;
                Adsobalin.addPoints(false, origen, mov.indice);
                mov.angHit = angulo;
                if (mov.golpear()) {
                    Adsobalin.addPoints(true, origen, mov.indice);
                    Envios.sendGolpe(origen, mov.indice, true, llave, angulo);
                }
                else {
                    Envios.sendGolpe(origen, mov.indice, false, llave, angulo);
                }
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        if (sprite == null) {
            // esto es para pruebas, pero nunca deberia verse
            drawMask(gc, grupo);
        }
        else {
            drawImagenRot(gc, sprite, posicion, angulo);
        }
    }
}
