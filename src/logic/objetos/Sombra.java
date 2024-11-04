package logic.objetos;
// ente sincronizado, es automata o player de otra maquina

import logic.abstractos.Movil;

public class Sombra extends Movil {
    
    public Sombra(float[] posicion) {
        super(posicion, 6);
    }
    
    @Override
    public void step(float delta) {
        
    }
    
    @Override
    public void draw() {
        
    }
}
