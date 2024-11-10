package logic.objetos;
// ente sincronizado, es automata o player de otra maquina

import javafx.scene.canvas.GraphicsContext;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;

public class Sombra extends Movil {
    
    public Sombra(float[] posicion) {
        super(posicion, Objeto.OBJ_SOMBRA);
    }
    
    @Override
    public void step(float delta) {
        
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        
    }
}
