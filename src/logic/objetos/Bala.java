package logic.objetos;
// proyectil generado por la misma maquina, no sincronizado

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
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
        // el origen es el Movil.indice de quien lo lanzo
        sprite = setProyectilImg(angulo, grupo, origen);
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
