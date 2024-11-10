package logic.abstractos;
// clase base para player, automata y sombra, es un ente dinamico

import logic.objetos.Bala;
import logic.interfaz.Adsobalin;

public abstract class Movil extends Objeto {
    
    // es la rapidez con que se mueven los entes
    public static final float VELOCIDAD = 100f;
    
    // los moviles mueven su ubicacion y luego la posicion la sigue
    protected float[] ubicacion = {0f, 0f};
    // identificador unico que se mantiene entre varias maquinas
    protected int indice = -1;
    // para mostrar el nickname del jugador propietario
    protected String nombre = "";
    
    // se activa para pausar entre disparos
    protected float tempDisparo = 0f;
    // se activa cuando debe recargar municion
    protected float tempRecarga = 0f;
    // se activara para que el ente ilumine de rojo
    protected float tempGolpe = 0f;
    // se activa al aparecer, para dar inmunidad
    protected float tempInmune = 0f;
    // se activara cuando recive danno, para curarse
    protected float tempCuracion = 0f;
    
    // son los puntos de impacto antes de morir
    protected int vida = 5;
    // es la cantidad de municion disponible
    protected int municion = 12;
    // grupo al que pertenece
    protected int grupo = Adsobalin.GRU_LIBRE;
    
    public Movil(float[] posicion, int myTipo) {
        super(posicion, myTipo, 12f * (float)Adsobalin.ESCALA);
        this.ubicacion = posicion;
    }
    
    protected void moverSync(float delta) {
        // hace que la posicion se acerque a la ubicacion
        posicion = Tools.vecInterpolar(posicion, ubicacion,
                2f * delta, 2f * VELOCIDAD * delta);
    }
    
    protected void temporizar(float delta) {
        
    }
    
    protected boolean coliSolidos(float delta) {
        return false;
    }
    
    protected boolean coliMoviles(float delta) {
        return false;
    }
    
    protected boolean limitar() {
        return false;
    }
    
    protected Bala disparar(float direccion) {
        Bala b = null;
        return b;
    }
    
    public void golpear() {
        
    }
    
    public void morir() {
        
    }
}
