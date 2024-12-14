package logic.objetos;
// ente sincronizado, es automata o player de otra maquina

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;
import logic.sincronia.Conector;

public class Sombra extends Movil {
    
    // para sonar una vez al iniciar
    private boolean noHaSonadoIni = true;
    // ping si no recibe actualizacion en un tiempo se destruye
    public float myPing = Conector.PING;
    
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
            120f * 0.75f, 120f * 0.75f, false, false);
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
        // sonar el chillido
        if (!isHit && tempGolpe != 0) {
            chillar();
        }
        isHit = tempGolpe != 0;
        // eliminarse si no hay actualizacion
        myPing -= delta;
        if (myPing <= 0) {
            morir();
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        if (sprite == null) {
            // esto es para pruebas, pero nunca deberia verse
            drawMask(gc, grupo);
        }
        else {
            drawMovil(gc, sprite);
        }
    }
}
