package logic.interfaz;
// interfaz con main loop y lista de objetos, donde se ejecuta el juego

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.abstractos.*;
import logic.objetos.*;

public class Mundo extends GUIs {
    
    // constantes globales de la simulacion
    public static final int RADIO = 1024;
    public static int radioMundo;
    public static float[] centroMundo = new float[2];
    
    // guardara todos los objetos que existen instanciados en el juego
    public static ArrayList<Object> pool = new ArrayList<>();
    
    // duracion de la partida
    public static float tiempoRestante;
    // si deben invocarse NPCs
    private boolean[] npcok;
    
    // posicion de la camara en el mundo
    public static float[] camaraPos = {0f, 0f};
    // difuminado de la camara
    private Image difuminado = new Image("assets/entorno/camara.png",
        Adsobalin.WIDTH, Adsobalin.HEIGHT, false, false);
    // fondo del mundo
    private Image fondo;
    
    public Mundo(Stage raiz, boolean[] npcok, int talla,
            int obstaculos, int tiempo) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_JUEGO;
        radioMundo = (int)(((RADIO / 2) * (1 + talla)) * Adsobalin.ESCALA);
        centroMundo[0] = radioMundo / 2f;
        centroMundo[1] = radioMundo / 2f;
        tiempoRestante = tiempo * 60f;
        this.npcok = npcok;
        
        // cargar el fondo
        fondo = new Image("assets/entorno/fondo.png",
            radioMundo * 2f, radioMundo * 2f, false, false);
        
        // posicionar la camara
        camaraPos[0] = centroMundo[0] - (float)Adsobalin.WIDTH / 2f;
        camaraPos[1] = centroMundo[1] - (float)Adsobalin.HEIGHT / 2f;
        
        // ejecutar por primera vez el juego
        creaBases();
        if (Adsobalin.isServer) {
            creaArboles((float)obstaculos / (float)Lobby.DENSI_OBST_MAX);
            creaDecorados();
        }
        
        // crear el loop principal de simulacion
        new AnimationTimer() {
            private long last = 0L;
            @Override
            public void handle(long now) {
                double delta = (now - last) / 1000000000.0d;
                if (delta < 1d && delta > 0.01d) {
                    step((float)delta);
                    draw();
                }
                last = now;
            }
        }.start();
    }
    
    private void creaBases() {
        
    }
    
    private void creaDecorados() {
        
    }
    
    private void creaArboles(float densidad) {
        // halla el area del circulo del mundo
        float densiMundo = (float)Math.PI * (float)Math.pow(radioMundo, 2);
        // halla el area del circulo de un solido, luego halla la relacion
        densiMundo /= (float)Math.PI * (float)Math.pow(Solido.RADIO, 2);
        // la relacion es el max de arboles que caben, una proporcion es total
        int total = (int)(densiMundo * (densidad * 0.01f));
        // punto en el centro del mundo
        float[] pos;
        // creara muchos arboles
        for (int i = 0; i < total; i++) {
            do {
                // primero elige un punto al azar en el cuadrado
                pos = new float[2];
                pos[0] = Adsobalin.DADO.nextFloat(radioMundo);
                pos[1] = Adsobalin.DADO.nextFloat(radioMundo);
                // verifica que el punto este dentro del circulo gigante
                if (Tools.vecDistancia(centroMundo, pos) > radioMundo) {
                    continue;
                }
            }
            // repetira el ciclo mientras no colisione con otro solido
            while (colsionObject(pos, Solido.RADIO * 2.5f,
                    Solido.class) != null);
            // finalmente crea un arbol
            newObjeto(Arbol.class, pos);
        }
    }
    
    public static Object colsionObject(float[] posicion,
            float radio, Class<?> claseMascara) {
        // retorna el objeto o null si no colisiono
        Object obj;
        Objeto aux;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (claseMascara.isInstance(obj)) {
                aux = (Objeto)obj;
                if (Tools.circleColision(posicion, radio,
                        aux.posicion, aux.radio) != Tools.NO_COLI) {
                    return obj;
                }
            }
        }
        return null;
    }
    
    public static Object newObjeto(Class<?> clase, float[] posicion) {
        Object obj;
        try {
            obj = clase.getConstructor(float[].class).newInstance(posicion);
            pool.add(obj);
        }
        catch (Exception ex) {
            obj = null;
        }
        return obj;
    }
    
    public static void deleteObjeto(Object obj) {
        if (pool.contains(obj)) {
            pool.remove(obj);
        }
    }
    
    public static boolean existObjeto(Object obj) {
        return pool.contains(obj);
    }
    
    private void step(float delta) {
        // ejecuta toda la logica del juego
        
        // ejecuta todas las acciones de los objetos
        Objeto obj;
        for (int i = 0; i <= Objeto.OBJ_BASE; i++) {
            // Tarea agregar aqui switch que evite objetos sin step 
           for (int n = 0; n < pool.size(); n++) {
                obj = (Objeto)pool.get(n);
                if (obj.myTipo == i) {
                    obj.step(delta);
                }
            }
        }
    }
    
    private void draw() {
        // dibuja todo un frame del juego
        // primero dibujara el fondo que ademas limpia el lienzo
        gc.drawImage(fondo, -camaraPos[0], -camaraPos[1]);
        
        // luego dibuja cada uno de los objetos
        Objeto obj;
        for (int i = 0; i <= Objeto.OBJ_BASE; i++) {
            for (int n = 0; n < pool.size(); n++) {
                obj = (Objeto)pool.get(n);
                if (obj.myTipo == i) {
                    obj.draw(gc);
                }
            }
        }
        
        // dibujar la mira difuminada de la camara sobre todo
        gc.drawImage(difuminado, 0f, 0f);
    }
}
