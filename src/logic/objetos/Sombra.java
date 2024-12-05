package logic.objetos;
// ente sincronizado, es automata o player de otra maquina

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;

public class Sombra extends Movil {
    
    // para sonar una vez al iniciar
    private boolean noHaSonadoIni = true;
    
    private Image sprite;
    
    public Sombra(float[] posicion) {
        super(posicion, Objeto.OBJ_SOMBRA);
    }
    
    public void setAvatar(int grupo, int indice, int estilo, String nombre) {
        this.grupo = grupo;
        this.indice = indice;
        this.nombre = nombre;
        this.estilo = estilo;
        String bcol = "rojos/rojo";
        if (grupo == Adsobalin.GRU_AZUL) {
            bcol = "azules/azul";
        }
        sprite = new Image("assets/" + bcol + estilo + ".png",
            120f * 0.75f * (float)Adsobalin.ESCALA,
            120f * 0.75f * (float)Adsobalin.ESCALA,
            false, false);
    }
    
    @Override
    public void step(float delta) {
        // verificar cambio en temporizadores para sonar
        if (noHaSonadoIni) {
            if (tempInmune == 0) {
                noHaSonadoIni = false;
                bullaini();
            }
        }
        // sincronizar el movimiento final
        moverSync(delta);
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawMovil(gc, sprite);
    }
}
