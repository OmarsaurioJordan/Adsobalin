package logic.abstractos;
// particulas que se disparan en linea recta y colisionan

import javafx.scene.image.Image;
import logic.interfaz.Adsobalin;
import logic.interfaz.Mundo;

public abstract class Proyectil extends Objeto {
    
    // radio para todos los proyectiles colisionables
    public static float RADIO = 6f * (float)Adsobalin.ESCALA;
    // segundos que dura la existencia del proyectil
    public static final float TEMP_EXISTENCIA_MAX = 1f;
    // es la rapidez con que se mueven los proyectiles
    public static final float VELOCIDAD = 600f * (float)Adsobalin.ESCALA;
    
    // segundos que dura la existencia del proyectil
    protected float tempExistencia = TEMP_EXISTENCIA_MAX;
    // identificador unico del jugador o NPC que lo lanzo, Movil.indice
    protected int origen = -1;
    // grupo al que pertenece
    protected int grupo = Adsobalin.GRU_AZUL;
    
    public Proyectil(float[] posicion, int myTipo) {
        super(posicion, myTipo, RADIO);
    }
    
    protected Image setProyectilImg(float angulo, int grupo, int origen) {
        // nota esto va a parte del constructor para que todos los objetos
        // del juego tengan un constructor solo con posicion y tipo
        this.angulo = angulo;
        this.grupo = grupo;
        this.origen = origen;
        String bcol = "rojos/rojoproyectil.png";
        if (grupo == Adsobalin.GRU_AZUL) {
            bcol = "azules/azulproyectil.png";
        }
        Image sprite = new Image("assets/" + bcol,
            110f * 0.75f * (float)Adsobalin.ESCALA,
            110f * 0.75f * (float)Adsobalin.ESCALA,
            false, false);
        return sprite;
    }
    
    protected Object avanzar(float delta, boolean isDestruMovil) {
        // mueve el proyectil y retorna al movil con que choco o null
        // primero se observa si debe destruirse por tiempo limite
        tempExistencia -= delta;
        if (tempExistencia <= 0) {
            Mundo.deleteObjeto(this);
            return null;
        }
        // hace que el proyectil se mueva y si choca sera destruido
        float tramo = VELOCIDAD * delta;
        int pedazos = (int)Math.ceil(tramo / radio);
        float paso = tramo / Math.max(1, pedazos);
        Object otro;
        for (int p = 0; p < pedazos; p++) {
            // dar un pasito para verificar colisiones evitando traspasar cosas
            posicion = Tools.vecMover(posicion, paso, angulo);
            // verificar colision con solidos
            otro = Mundo.colsionObject(posicion,
                radio, Solido.class, this);
            if (otro != null) {
                Mundo.deleteObjeto(this);
                return null;
            }
            // verificar colision con moviles
            otro = Mundo.colsionGrupo(posicion,
                radio, grupo, this);
            if (otro != null) {
                if (isDestruMovil) {
                    Mundo.deleteObjeto(this);
                }
                return otro;
            }
        }
        return null;
    }
}
