package logic.objetos;
// personaje manejado por el jugador

import javafx.scene.canvas.GraphicsContext;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;

public class Automata extends Movil {
    
    public Automata(float[] posicion) {
        super(posicion, Objeto.OBJ_AUTOMATA);
    }
    
    private void moverAuto(float delta) {
        
    }
    
    private void dispararAuto(float delta) {
        
    }
    
    private Movil buscar() {
        Movil blanco = null;
        return blanco;
    }
    
    @Override
    public void step(float delta) {
        
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        
    }
}
