package logic.objetos;
// personaje manejado por el jugador

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.interfaz.Adsobalin;
import logic.abstractos.Tools;
import logic.interfaz.Mundo;
import logic.abstractos.Solido;
import logic.interfaz.Sonidos;

public class Player extends Movil {
    
    // para manejar la camara
    private static float MIRA_MAX = 100f * (float)Adsobalin.ESCALA;
    private static float[] DESF_MIRA = {
        (float)Adsobalin.WIDTH / 2f,
        (float)Adsobalin.HEIGHT / 2f
    };
    
    private Image sprite;
    
    public Player(float[] posicion) {
        super(posicion, Objeto.OBJ_PLAYER);
    }
    
    public void setAvatar(int indice) {
        grupo = Adsobalin.grupo;
        nombre = Adsobalin.nombre;
        estilo = Adsobalin.estilo;
        this.indice = indice;
        String bcol = "rojos/rojo";
        if (grupo == Adsobalin.GRU_AZUL) {
            bcol = "azules/azul";
        }
        sprite = new Image("assets/" + bcol + estilo + ".png",
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
        if (Mundo.teclas[Mundo.KEY_CLICL]) {
            disparar(angulo);
        }
        else if (Mundo.teclas[Mundo.KEY_CLICR] ||
                Mundo.teclas[Mundo.KEY_R]) {
            recargar();
        }
    }
    
    private void moverCamara(float delta) {
        float lejosMira = Tools.vecDistancia(posicion, Mundo.mousePos);
        float anguMira = Tools.vecDireccion(posicion, Mundo.mousePos);
        float[] miraReal = Tools.vecMover(posicion,
                Math.min(lejosMira * 0.5f, MIRA_MAX), anguMira);
        Mundo.camaraPos = Tools.vecInterpolar(Mundo.camaraPos,
                Tools.vecResta(miraReal, DESF_MIRA),
                4f * delta, 100f);
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
        int antVida = vida;
        int antMunicion = municion;
        float antInmune = tempInmune;
        temporizar(delta);
        if (vida != antVida) {
            if (vida == VIDA_MAX) {
                Sonidos.sonidoPos(Sonidos.SND_CURACION, posicion);
            }
        }
        if (municion != antMunicion) {
            Sonidos.sonidoPos(Sonidos.SND_RECARGA, posicion);
        }
        if (tempInmune != antInmune) {
            if (tempInmune == 0) {
                bullaini();
            }
        }
        // colisionar con los solidos
        Object otro = Mundo.colsionObject(ubicacion,
                radio, Solido.class, this);
        if (otro != null) {
            Objeto otr = (Objeto)otro;
            ubicacion = Tools.vecMover(ubicacion, VELOCIDAD * delta,
                    Tools.vecDireccion(otr.posicion, ubicacion));
        }
        // colisionar con otros moviles
        Object mov = Mundo.colsionObject(ubicacion,
                radio, Movil.class, this);
        if (mov != null) {
            Movil m = (Movil)mov;
            ubicacion = Tools.vecMover(ubicacion, VELOCIDAD * delta,
                    Tools.vecDireccion(m.ubicacion, ubicacion));
            m.ubicacion = Tools.vecMover(m.ubicacion, VELOCIDAD * delta,
                    Tools.vecDireccion(ubicacion, m.ubicacion));
        }
        // moverse por accion del jugador
        if (otro == null && mov == null) {
            moverComando(delta);
        }
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
        drawMovil(gc, sprite);
    }
}
