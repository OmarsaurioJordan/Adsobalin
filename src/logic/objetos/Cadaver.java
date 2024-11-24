package logic.objetos;
// decorado dejado por un ente eliminado

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;

public class Cadaver extends Objeto {
    
    private Image sprite;
    
    // caracteristicas que definen al cadaver
    int grupo = Adsobalin.GRU_AZUL;
    boolean isNPC = true;
    // tiempo para pudrirse y quedar mas sutilmente en el fondo
    private float tempPudrirse = 22f;
    
    public Cadaver(float[] posicion) {
        super(posicion, Objeto.OBJ_CADAVER, Movil.RADIO);
    }
    
    public void setCadaver(int grupo, boolean isNPC, float angulo,
            boolean isPodrido, float tiempoRestante) {
        // si tiempo restante es 0, no habra cambios
        this.grupo = grupo;
        this.isNPC = isNPC;
        this.angulo = angulo;
        if (tiempoRestante > 0) {
            tempPudrirse = tiempoRestante;
        }
        setImagen();
    }
    
    public void setImagen() {
        String bcol = "azules/azulkill";
        if (grupo == Adsobalin.GRU_ROJO) {
            bcol = "rojos/rojokill";
        }
        if (isNPC) {
            bcol += "0.png";
        }
        else {
            bcol += "1.png";
        }
        if (tempPudrirse == 0) {
            bcol = bcol.replace("kill", "hueso");
        }
        sprite = new Image("assets/" + bcol,
            220f * 0.75f * (float)Adsobalin.ESCALA,
            220f * 0.75f * (float)Adsobalin.ESCALA,
            false, false);
    }
    
    @Override
    public void step(float delta) {
        if (tempPudrirse > 0) {
            tempPudrirse -= delta;
            if (tempPudrirse <= 0) {
                tempPudrirse = 0f;
                setImagen();
            }
        }
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
