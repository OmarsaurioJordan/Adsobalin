package logic.objetos;
// personaje manejado por el jugador

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import logic.abstractos.Movil;
import logic.abstractos.Objeto;
import logic.abstractos.Proyectil;
import logic.abstractos.Solido;
import logic.abstractos.Tools;
import logic.interfaz.Adsobalin;
import logic.interfaz.Mundo;

public class Automata extends Movil {
    
    // radio de alcance visual para detectar enemigos
    public static final float VISION = 320f;
    // tiempo medio de errar, el rando es hasta su doble
    public static final float TEMP_ERRAR = 1f;
    // angulo de cambio de direccion maximo, PI * 0.5 es 90°
    public static final float ANG_GIRO = (float)Math.PI * 0.5f;
    
    // guarda el enemigo que esta en mira
    private Movil objetivo = null;
    // el punto de interes a investigar: ultima vez enemigo visto, o ruido
    private float[] posInteres = {0f, 0f};
    // reloj para cambiar de direccion de movimiento
    private float tempErrar = 0f;
    // indica si esta en modo movimiento erratico o no
    private boolean errarMove = false;
    // indica la direccion en la que se movera erraticamente
    private float errarDireccion = 0f;
    // para hacer cambios aleatorios en la direccion de movimiento
    private float errarDesfDir = 0f;
    
    private Image sprite;
    
    public Automata(float[] posicion) {
        super(posicion, Objeto.OBJ_AUTOMATA);
        tempErrar = TEMP_ERRAR + Adsobalin.DADO.nextFloat() * TEMP_ERRAR;
        errarMove = Adsobalin.DADO.nextFloat() > 0.5f;
        azarDireccion();
    }
    
    public void setAvatar(int grupo, int indice) {
        this.grupo = grupo;
        this.indice = indice;
        String bcol = "rojos/rojo0.png";
        if (grupo == Adsobalin.GRU_AZUL) {
            bcol = "azules/azul0.png";
        }
        sprite = new Image("assets/" + bcol,
            120f * 0.75f, 120f * 0.75f, false, false);
    }
    
    private boolean existeObjetivo() {
        if (objetivo != null) {
            if (Mundo.existObjeto(objetivo)) {
                return true;
            }
            objetivo = null;
        }
        return false;
    }
    
    private void temporiErrar(float delta) {
        tempErrar -= delta;
        if (tempErrar <= 0) {
            tempErrar += TEMP_ERRAR + Adsobalin.DADO.nextFloat() * TEMP_ERRAR;
            errarDesfDir = -ANG_GIRO +
                    Adsobalin.DADO.nextFloat() * ANG_GIRO * 2f;
            if (errarMove) {
                if (Adsobalin.DADO.nextFloat() < 0.25f) {
                    errarMove = false;
                }
                errarDireccion += errarDesfDir;
            }
            else {
                azarDireccion();
                if (Adsobalin.DADO.nextFloat() < 0.75f) {
                    errarMove = true;
                    // de vez en cuando buscara a otros moviles al azar
                    if (Adsobalin.DADO.nextFloat() < 0.333) {
                        Movil mov = (Movil)Mundo.getRandObject(
                                Movil.class, this);
                        if (mov != null) {
                            errarDireccion = Tools.vecDireccion(
                                ubicacion, mov.ubicacion);
                        }
                    }
                }
            }
        }
    }
    
    private void azarDireccion() {
        errarDireccion = Adsobalin.DADO.nextFloat() * (float)Math.PI * 2f;
    }
    
    private void moverAuto(float delta) {
        float dist = 0f;
        // verificar si el enemigo esta en zona visible
        if (existeObjetivo()) {
            dist = Tools.vecDistancia(ubicacion, objetivo.ubicacion);
            if (dist > VISION || Mundo.colsionLine(ubicacion,
                    objetivo.ubicacion, Proyectil.RADIO, Solido.class)) {
                objetivo = null;
            }
        }
        // moverse siguiendo al objetivo, manteniendose en un alcance medio
        if (existeObjetivo()) {
            if (tempRecarga != 0) {
                // estara ocupado recargando
                if (dist < VISION * 0.9f) {
                    // huir
                    float d = Tools.vecDireccion(
                            objetivo.ubicacion, ubicacion);
                    ubicacion = Tools.vecMover(ubicacion,
                            VELOCIDAD * delta, d + errarDesfDir);
                    azarDireccion();
                }
                else {
                    // errar
                    ubicacion = Tools.vecMover(ubicacion,
                        VELOCIDAD * delta, errarDireccion);
                }
            }
            else if (dist > VISION * 0.75f) {
                // acercarce
                float d = Tools.vecDireccion(ubicacion, objetivo.ubicacion);
                ubicacion = Tools.vecMover(ubicacion,
                        VELOCIDAD * delta, d + errarDesfDir);
                azarDireccion();
            }
            else if (dist < VISION * 0.25f) {
                // alejarse
                float d = Tools.vecDireccion(objetivo.ubicacion, ubicacion);
                ubicacion = Tools.vecMover(ubicacion,
                        VELOCIDAD * delta, d + errarDesfDir);
                azarDireccion();
            }
            else {
                // errar
                ubicacion = Tools.vecMover(ubicacion,
                    VELOCIDAD * delta, errarDireccion);
            }
            posInteres = objetivo.ubicacion.clone();
            anguMira = Tools.vecDireccion(ubicacion, posInteres);
        }
        // ir al ultimo lugar de interes
        else if (posInteres[0] != 0 || posInteres[1] != 0) {
            anguMira = Tools.vecDireccion(ubicacion, posInteres);
            if (tempRecarga != 0) {
                dist = Tools.vecDistancia(posInteres, ubicacion);
                if (dist < VISION * 0.9f) {
                    // huir
                    float d = Tools.vecDireccion(
                            posInteres, ubicacion);
                    ubicacion = Tools.vecMover(ubicacion,
                            VELOCIDAD * delta, d + errarDesfDir);
                    azarDireccion();
                }
                else {
                    // errar
                    ubicacion = Tools.vecMover(ubicacion,
                        VELOCIDAD * delta, errarDireccion);
                }
            }
            else {
                ubicacion = Tools.vecMover(ubicacion,
                        VELOCIDAD * delta, anguMira + errarDesfDir);
                if (Tools.vecDistancia(ubicacion, posInteres) < radio * 2f) {
                    posInteres[0] = 0f;
                    posInteres[1] = 0f;
                }
            }
        }
        // moverse al azar por el mundo
        else if (errarMove) {
            ubicacion = Tools.vecMover(ubicacion,
                    VELOCIDAD * delta, errarDireccion);
            anguMira = errarDireccion;
        }
    }
    
    private void dispararAuto() {
        if (objetivo != null) {
            if (Math.abs(Tools.angDifference(anguMira, angulo)) <
                    (float)Math.PI * 0.2f) {
                disparar(angulo + (-1f + Adsobalin.DADO.nextFloat() * 2f) *
                        (float)Math.PI * 0.05f, true);
            }
        }
    }
    
    private void hacerBusqueda(float delta) {
        float r = Adsobalin.DADO.nextFloat();
        if ((objetivo == null && r < 0.8 * delta) || r < 0.08 * delta) {
            ArrayList<Movil> candidatos = Mundo.getCercanos(
                    ubicacion, VISION, this, grupo);
            if (!candidatos.isEmpty()) {
                if (existeObjetivo()) {
                    Movil c = candidatos.get(Adsobalin.DADO.nextInt(
                        candidatos.size()));
                    if (Tools.vecDistancia(ubicacion, objetivo.ubicacion) >
                            Tools.vecDistancia(ubicacion, c.ubicacion) &&
                            Adsobalin.DADO.nextFloat() > 0.2f) {
                        if (!Mundo.colsionLine(ubicacion,
                                c.ubicacion, Proyectil.RADIO, Solido.class)) {
                            objetivo = c;
                        }
                    }
                }
                else {
                    objetivo = candidatos.get(Adsobalin.DADO.nextInt(
                        candidatos.size()));
                }
            }
        }
    }
    
    public void reaccionarHit() {
        // se tendra en cuenta la direccion de colision
        if (objetivo == null) {
            posInteres = Tools.vecMover(ubicacion, VISION, angHit);
            posInteres = Tools.circleLimitar(Mundo.centroMundo,
                    Mundo.radioMundo * 0.95f, posInteres);
        }
    }
    
    @Override
    public void step(float delta) {
        // ejecutar los temporizadores
        float antInmune = tempInmune;
        temporizar(delta);
        temporiErrar(delta);
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
        // moverse por toma de decisiones
        if (otro == null && mov == null) {
            moverAuto(delta);
        }
        // buscar objetivos y tratar de disparar
        hacerBusqueda(delta);
        dispararAuto();
        // limites de mundo
        float[] ant = ubicacion.clone();
        ubicacion = Tools.circleLimitar(Mundo.centroMundo,
                Mundo.radioMundo * 0.95f, ubicacion);
        if (ant[0] != ubicacion[0] || ant[1] != ubicacion[1]) {
            azarDireccion();
        }
        // sincronizar el movimiento final
        moverSync(delta);
    }
    
    @Override
    public void draw(GraphicsContext gc) {
        drawMovil(gc, sprite);
    }
}
