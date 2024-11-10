package logic.objetos;
// personaje manejado por el jugador

import javafx.scene.canvas.GraphicsContext;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;

public class Player extends Movil {
    
    public Player(float[] posicion) {
        super(posicion, Objeto.OBJ_PLAYER);
    }
    
    private void moverComando(float delta) {
        
    }
    
    private void dispararComando(float delta) {
        
    }
    
    @Override
    public void step(float delta) {
        
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        
    }
}
