package logic.abstractos;
// clase base para player, automata y sombra, es un ente dinamico

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.BlendMode;
import logic.objetos.Bala;
import logic.objetos.Player;
import logic.objetos.Automata;
import logic.interfaz.Adsobalin;
import logic.interfaz.Mundo;

public abstract class Movil extends Objeto {
    
    // radio para todos los moviles colisionables
    public static float RADIO = 12f * (float)Adsobalin.ESCALA;
    // es la rapidez con que se mueven los entes
    public static final float VELOCIDAD = 200f * (float)Adsobalin.ESCALA;
    // es la rapidez con que la mira del avatar sigue al mouse
    public static final float VELROT_MIRA = 6f;
    // los puntos de vida maximos
    public static final int VIDA_MAX = 5;
    // la municion maxima almacenada
    public static final int MUNICION_MAX = 12;
    // segundos que dura la cadencia entre disparos
    public static final float TEMP_DISPARO_MAX = 0.2f;
    // segundos que dura la recarga de 1 municion
    public static final float TEMP_RECARGA_MAX = 0.3f;
    // segundos que dura la iluminacion al ser golpeado
    public static final float TEMP_GOLPE_MAX = 0.5f;
    // segundos que dura la inmunidad inicial parpadeante
    public static final float TEMP_INMUNE_MAX = 5f;
    // segundos que dura la curacion de una sola vida
    public static final float TEMP_CURACION_MAX = 1f;
    // segundos que hay que esperar para poder curar vidas
    public static final float TEMP_REGENERACION_MAX = 6f;
    // segundos intermitencia
    public static final float TEMP_INTERMIT_MAX = 0.25f;
    
    // los moviles mueven su ubicacion y luego la posicion la sigue
    public float[] ubicacion = {0f, 0f};
    // el angulo verdadero en que apunta, manejado por codigo, radianes
    protected float anguMira = 0f;
    // identificador unico que se mantiene entre varias maquinas
    public int indice = -1;
    // para mostrar el nickname del jugador propietario
    protected String nombre = "";
    
    // se activa para pausar entre disparos
    protected float tempDisparo = 0f;
    // se activa cuando debe recargar municion
    protected float tempRecarga = 0f;
    // se activara para que el ente ilumine de rojo
    protected float tempGolpe = 0f;
    // se activa al aparecer, para dar inmunidad
    protected float tempInmune = TEMP_INMUNE_MAX;
    // se activara al curar una vida y continuar con las demas
    protected float tempCuracion = 0f;
    // se activara cuando recive danno, para curarse
    protected float tempRegeneracion = 0f;
    // para parpadear cuando hay inmunidad
    protected float tempIntermit = 0f;
    
    // ajustes de color al ser golpeado
    private ColorAdjust ajusteCol = new ColorAdjust();
    
    // son los puntos de impacto antes de morir
    public int vida = VIDA_MAX;
    // es la cantidad de municion disponible
    public int municion = MUNICION_MAX;
    // grupo al que pertenece
    public int grupo = Adsobalin.GRU_AZUL;
    
    public Movil(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
        this.ubicacion = posicion;
        ajusteCol.setBrightness(-0.5d);
    }
    
    protected void moverSync(float delta) {
        // hace que la posicion se acerque a la ubicacion
        posicion = Tools.vecInterpolar(posicion, ubicacion,
                12f * delta, VELOCIDAD * delta * 2f);
        // hace que el angulo se acerque a la mira
        angulo = Tools.interpAngle(angulo, anguMira, VELROT_MIRA * delta);
    }
    
    protected void temporizar(float delta) {
        // permite disparar de nuevo
        if (tempDisparo != 0) {
            tempDisparo = Math.max(0f, tempDisparo - delta);
        }
        // llena toda la municion de a pocos
        if (tempRecarga != 0) {
            tempRecarga = Math.max(0f, tempRecarga - delta);
            if (tempRecarga == 0) {
                municion = Math.min(municion + 1, MUNICION_MAX);
                if (municion < MUNICION_MAX) {
                    tempRecarga = TEMP_RECARGA_MAX;
                }
            }
        }
        // desactiva el efecto de golpe
        if (tempGolpe != 0) {
            tempGolpe = Math.max(0f, tempGolpe - delta);
        }
        // desactiva la inmunidad
        if (tempInmune != 0) {
            tempInmune = Math.max(0f, tempInmune - delta);
        }
        // curacion continua de vida, hasta llenarla
        if (tempCuracion != 0) {
            tempCuracion = Math.max(0f, tempCuracion - delta);
            if (tempCuracion == 0) {
                vida = Math.min(vida + 1, VIDA_MAX);
                if (vida < VIDA_MAX) {
                    tempCuracion = TEMP_CURACION_MAX;
                }
            }
        }
        // conteo largo que finalmente permitira la curacion de vida
        if (tempRegeneracion != 0) {
            tempRegeneracion = Math.max(0f, tempRegeneracion - delta);
            if (tempRegeneracion == 0) {
                tempCuracion = 0.1f;
            }
        }
        // parpadear con inmunidad
        tempIntermit -= delta;
        if (tempIntermit <= 0) {
            tempIntermit += TEMP_INTERMIT_MAX;
        }
    }
    
    protected Bala disparar(float angulo) {
        if (tempDisparo == 0 && tempRecarga == 0 && municion > 0) {
            Bala b = (Bala)Mundo.newObjeto(Bala.class,
                    Tools.vecMover(posicion, Movil.RADIO * 2f,
                            this.angulo + (float)Math.PI / 7f));
            b.setProyectil(angulo, grupo, indice);
            municion -= 1;
            if (municion == 0) {
                tempRecarga = TEMP_RECARGA_MAX;
            }
            else {
                tempDisparo = TEMP_DISPARO_MAX;
            }
            return b;
        }
        return null;
    }
    
    protected void recargar() {
        if (municion < MUNICION_MAX) {
            if (tempRecarga == 0) {
                tempRecarga = TEMP_RECARGA_MAX;
            }
        }
    }
    
    public boolean golpear(int indOrigenProy) {
        // retorna verdadero si muere
        if (tempInmune == 0) {
            vida = Math.max(0, vida - 1);
            if (vida == 0) {
                morir(indOrigenProy);
                return true;
            }
            else {
                tempGolpe = TEMP_GOLPE_MAX;
                tempRegeneracion = TEMP_REGENERACION_MAX;
                tempCuracion = 0f;
            }
        }
        return false;
    }
    
    public void morir(int indOrigenProy) {
        Mundo.deleteObjeto(this);
        // crear cadaver
        
        // reiniciar contador de respawn
        if (getClass() == Player.class) {
            Mundo.tempRespawnPlayer = Mundo.TEMP_RESPAWN_MAX;
        }
        else if (getClass() == Automata.class) {
            Mundo.setRespawnNPC(indice);
        }
    }
    
    protected void drawMovil(GraphicsContext gc, Image sprite) {
        if (tempGolpe != 0) {
            gc.save();
            BlendMode blendMode = BlendMode.SRC_ATOP;
            gc.setGlobalBlendMode(blendMode);
            gc.setEffect(ajusteCol);
        }
        if (tempInmune == 0 || tempIntermit < TEMP_INTERMIT_MAX / 2f) {
            drawImagenRot(gc, sprite, posicion, angulo);
        }
        if (tempGolpe != 0) {
            gc.restore();
        }
    }
}
