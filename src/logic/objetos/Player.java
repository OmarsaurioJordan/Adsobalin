package logic.objetos;
// personaje manejado por el jugador

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;
import logic.abstractos.Tools;
import logic.interfaz.Mundo;

public class Player extends Movil {
    
    // para manejar la camara
    private static float MIRA_MAX = 100f * (float)Adsobalin.ESCALA;
    private static float[] DESF_MIRA = {
        (float)Adsobalin.WIDTH / 2f,
        (float)Adsobalin.HEIGHT / 2f
    };
    
    private Image sprite = new Image("assets/azules/azul0.png",
        120f * 0.75f * (float)Adsobalin.ESCALA,
        120f * 0.75f * (float)Adsobalin.ESCALA,
        false, false);
    
    public Player(float[] posicion) {
        super(posicion, Objeto.OBJ_PLAYER);
    }
    
    public void setAvatar() {
        grupo = Adsobalin.grupo;
        nombre = Adsobalin.nombre;
        String bcol = "rojos/rojo";
        if (grupo == Adsobalin.GRU_AZUL) {
            bcol = "azules/azul";
        }
        sprite = new Image("assets/" + bcol + Adsobalin.estilo + ".png",
            120f * 0.75f * (float)Adsobalin.ESCALA,
            120f * 0.75f * (float)Adsobalin.ESCALA,
            false, false);
    }
    
    private void moverComando(float delta) {
        float mira;
        if (Mundo.teclas[Mundo.KEY_DOWN]) {
            if (Mundo.teclas[Mundo.KEY_LEFT]) {
                mira = 135f;
            }
            else if (Mundo.teclas[Mundo.KEY_RIGHT]) {
                mira = 45f;
            }
            else {
                mira = 90f;
            }
        }
        else if (Mundo.teclas[Mundo.KEY_UP]) {
            if (Mundo.teclas[Mundo.KEY_LEFT]) {
                mira = 225f;
            }
            else if (Mundo.teclas[Mundo.KEY_RIGHT]) {
                mira = 315f;
            }
            else {
                mira = 270f;
            }
        }
        else {
            if (Mundo.teclas[Mundo.KEY_LEFT]) {
                mira = 180f;
            }
            else if (Mundo.teclas[Mundo.KEY_RIGHT]) {
                mira = 0f;
            }
            else {
                mira = -1f;
            }
        }
        if (mira != -1) {
            mira *= (float)Math.PI / 180f;
            ubicacion = Tools.vecMover(ubicacion, VELOCIDAD * delta, mira);
        }
    }
    
    private void dispararComando(float delta) {
        
    }
    
    private void moverCamara(float delta) {
        float lejosMira = Tools.vecDistancia(posicion, Mundo.mousePos);
        float anguMira = Tools.vecDireccion(posicion, Mundo.mousePos);
        float[] miraReal = Tools.vecMover(posicion,
                Math.min(lejosMira * 0.5f, MIRA_MAX), anguMira);
        Mundo.camaraPos = Tools.vecInterpolar(Mundo.camaraPos,
                Tools.vecResta(miraReal, DESF_MIRA),
                4f * delta, 10f);
        // poner limites en el cuadro del mundo
        Mundo.camaraPos[0] = (float)Math.max(0f, Math.min(
                2f * Mundo.radioMundo - Adsobalin.WIDTH,
                Mundo.camaraPos[0]));
        Mundo.camaraPos[1] = (float)Math.max(0f, Math.min(
                2f * Mundo.radioMundo - Adsobalin.HEIGHT,
                Mundo.camaraPos[1]));
    }
    
    @Override
    public void step(float delta) {
        // ejecutar los temporizadores
        
        // colisionar con los solidos
        
        // colisionar con otros moviles
        
        // moverse por accion del jugador
        moverComando(delta);
        // ajustar la direccion en que mira
        anguMira = Tools.vecDireccion(posicion, Mundo.mousePos);
        // disparar por accion del jugador
        dispararComando(delta);
        // limites de mundo
        ubicacion = Tools.circleLimitar(Mundo.centroMundo,
                Mundo.radioMundo * 0.95f, ubicacion);
        // sincronizar el movimiento final
        moverSync(delta);
        // mover la camara
        moverCamara(delta);
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawImagenRot(gc, sprite, posicion, angulo);
    }
}
