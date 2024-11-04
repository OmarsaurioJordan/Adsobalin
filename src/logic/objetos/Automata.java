package logic.objetos;
// personaje manejado por el jugador

import logic.abstractos.Movil;

public class Automata extends Movil {
    
    public Automata(float[] posicion) {
        super(posicion, 5);
    }
    
    @Override
    public void step(float delta) {
        
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
    public void draw() {
        
    }
}
