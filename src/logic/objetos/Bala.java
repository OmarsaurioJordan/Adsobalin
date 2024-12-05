package logic.objetos;
// proyectil generado por la misma maquina, no sincronizado

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.interfaz.Adsobalin;
import logic.sincronia.Envios;

public class Bala extends Proyectil {
    
    private Image sprite;
    
    public Bala(float[] posicion) {
        super(posicion, Objeto.OBJ_BALA);
    }
    
    public void setProyectil(float angulo, int grupo, int origen,
            boolean isFromNPC) {
        // el origen es el indice de quien lo lanzo
        sprite = setProyectilImg(angulo, grupo, origen);
        // generar la llave al azar, que contiene el indice
        llave = (int)(Adsobalin.DADO.nextFloat() * 9999999f);
        llave = Integer.parseInt((Adsobalin.indice + 1) + "" + llave);
        // enviar la bala por UDP a otro usuario
        Envios.sendDisparo(origen, llave, posicion, angulo, isFromNPC);
    }
    
    @Override
    public void step(float delta) {
        Object otro = avanzar(delta);
        if (otro != null) {
            // cuando el proyectil choca con un movil, debe golpearlo
            if (otro.getClass() == Player.class ||
                    otro.getClass() == Automata.class) {
                // hacer damage directamente
                Movil mov = (Movil)otro;
                Adsobalin.addPoints(false, origen, mov.indice);
                mov.angHit = angulo;
                if (mov.golpear(origen)) {
                    Adsobalin.addPoints(true, origen, mov.indice);
                }
            }
            else if (otro.getClass() == Sombra.class) {
                // enviar solicitud de damage
                
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
