package logic.interfaz;
// interfaz con main loop y lista de objetos, donde se ejecuta el juego

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.robot.Robot;
import javafx.scene.image.Image;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import logic.abstractos.*;
import logic.objetos.*;

public class Mundo extends GUIs {
    
    // constantes globales de la simulacion
    public static final int RADIO = 720;
    public static int radioMundo;
    public static float[] centroMundo = new float[2];
    
    // guardara todos los objetos que existen instanciados en el juego
    public static ArrayList<Object> pool = new ArrayList<>();
    
    // duracion de la partida
    public static float tiempoRestante;
    // si deben invocarse NPCs
    private boolean[] npcok;
    
    // guardar las teclas pulsadas
    public static int KEY_UP = 0;
    public static int KEY_DOWN = 1;
    public static int KEY_LEFT = 2;
    public static int KEY_RIGHT = 3;
    public static int KEY_R = 4;
    public static int KEY_CLICL = 5;
    public static int KEY_CLICR = 6;
    public static boolean[] teclas = new boolean[7];
    
    // posicion de la camara en el mundo
    public static float[] camaraPos = {0f, 0f};
    // posicion del mouse
    public static float[] mousePos = {0f, 0f};
    // sistema para obtener coordenadas de mouse
    private static Robot adquisidor = new Robot();
    // imagen del mouse
    private Image mouseImg = new Image("assets/entorno/mouse.png",
        72f * 0.75f * Adsobalin.ESCALA,
        72f * 0.75f * Adsobalin.ESCALA, false, false);
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
        centroMundo[0] = radioMundo;
        centroMundo[1] = radioMundo;
        tiempoRestante = tiempo * 60f;
        this.npcok = npcok;
        setCursor(Cursor.NONE);
        
        // cargar el fondo
        fondo = new Image("assets/entorno/fondo.png",
            radioMundo * 2f, radioMundo * 2f, false, false);
        
        // posicionar la camara
        camaraPos[0] = centroMundo[0] - (float)Adsobalin.WIDTH / 2f;
        camaraPos[1] = centroMundo[1] - (float)Adsobalin.HEIGHT / 2f;
        
        // ejecutar por primera vez el juego
        creaBases();
        if (Adsobalin.isServer) {
            // crea arboles
            CreaElementosRandom(((float)obstaculos /
                    Lobby.DENSI_OBST_MAX) * 0.3f,
                    Arbol.class, Solido.RADIO, 2.5f, Solido.class);
            // crea decorados
            CreaElementosRandom(0.5f, Baldoza.class,
                    Decorado.RADIO, 2.5f, Baldoza.class);
        }
        
        //Quitar
        Player ply = (Player)newObjeto(Player.class, centroMundo);
        ply.setAvatar();
        
        // leer la pulsacion de teclas
        setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> teclas[KEY_UP] = true;
                case S -> teclas[KEY_DOWN] = true;
                case A -> teclas[KEY_LEFT] = true;
                case D -> teclas[KEY_RIGHT] = true;
                case R -> teclas[KEY_R] = true;
            }
        });
        setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> teclas[KEY_UP] = false;
                case S -> teclas[KEY_DOWN] = false;
                case A -> teclas[KEY_LEFT] = false;
                case D -> teclas[KEY_RIGHT] = false;
                case R -> teclas[KEY_R] = false;
            }
        });
        
        // leer las pulsaciones de mouse
        setOnMousePressed(event -> {
            switch (event.getButton()) {
                case PRIMARY -> teclas[KEY_CLICL] = true;
                case SECONDARY -> teclas[KEY_CLICR] = true;
            }
        });
        setOnMouseReleased(event -> {
            switch (event.getButton()) {
                case PRIMARY -> teclas[KEY_CLICL] = false;
                case SECONDARY -> teclas[KEY_CLICR] = false;
            }
        });
        
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
        // dos bases azules a la izquierda
        float[] pos0 = {radioMundo * 0.3f, radioMundo * 0.8f};
        Base b0 = (Base)newObjeto(Base.class, pos0);
        b0.setGrupo(true);
        float[] pos1 = {radioMundo * 0.3f, radioMundo * 1.2f};
        Base b1 = (Base)newObjeto(Base.class, pos1);
        b1.setGrupo(true);
        // dos bases rojas a la derecha
        float[] pos2 = {radioMundo * 1.7f, radioMundo * 0.8f};
        Base b2 = (Base)newObjeto(Base.class, pos2);
        b2.setGrupo(false);
        float[] pos3 = {radioMundo * 1.7f, radioMundo * 1.2f};
        Base b3 = (Base)newObjeto(Base.class, pos3);
        b3.setGrupo(false);
    }
    
    private void CreaElementosRandom(float densidad, Class<?> claseNew,
            float radioNew, float propSeparacion, Class<?> claseColi) {
        // halla el area del circulo del mundo
        float densiMundo = (float)Math.PI * (float)Math.pow(radioMundo, 2);
        // halla el area del circulo de un elemento, luego halla la relacion
        densiMundo /= (float)Math.PI * (float)Math.pow(radioNew, 2);
        // la relacion es el max de elementos que caben, tot una proposcion de
        int total = (int)(densiMundo * (densidad * 0.1f));
        // creara muchos elementos
        float[] pos;
        for (int i = 0; i < total; i++) {
            pos = new float[2];
            do {
                // primero elige un punto al azar en el cuadrado
                pos[0] = Adsobalin.DADO.nextFloat(radioMundo * 2f);
                pos[1] = Adsobalin.DADO.nextFloat(radioMundo * 2f);
                // verifica que el punto este dentro del circulo gigante
                if (Tools.vecDistancia(centroMundo, pos) >
                        radioMundo * 0.95f) {
                    pos[0] = 0f;
                }
            }
            // repetira el ciclo mientras no colisione con otro elemento
            while (colsionObject(pos, radioNew * propSeparacion,
                    claseColi, null) != null || pos[0] == 0);
            // finalmente crea un elemento
            newObjeto(claseNew, pos);
        }
    }
    
    public static Object colsionObject(float[] posicion,
            float radio, Class<?> claseMascara, Object excepcion) {
        // retorna el objeto o null si no colisiono
        Object obj;
        Objeto aux;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (obj == excepcion) {
                continue;
            }
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
    
    public static Object colsionGrupo(float[] posicion,
            float radio, int grupoNoColi, Object excepcion) {
        // retorna el objeto o null si no colisiono
        Object obj;
        Movil aux;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (obj == excepcion) {
                continue;
            }
            if (Movil.class.isInstance(obj)) {
                aux = (Movil)obj;
                if (aux.grupo == grupoNoColi) {
                    continue;
                }
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
        // ejecuta toda la logica del juego:
        // obtener y procesar la posicion del mouse
        getMouse();
        
        // ejecuta todas las acciones de los objetos
        Objeto obj;
        for (int i = 0; i <= Objeto.OBJ_BASE; i++) {
            if (i == Objeto.OBJ_BALDOZA || i == Objeto.OBJ_ARBOL) {
                continue;
            }
            for (int n = 0; n < pool.size(); n++) {
                obj = (Objeto)pool.get(n);
                if (obj.myTipo == i) {
                    obj.step(delta);
                }
            }
        }
    }
    
    private void draw() {
        // dibuja todo un frame del juego:
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
        // dibujar el mouse
        float[] mPos = Tools.vecResta(mousePos, camaraPos);
        gc.drawImage(mouseImg, mPos[0] - mouseImg.getWidth() / 2f,
                mPos[1] - mouseImg.getHeight() / 2f);
    }
    
    // obtiener y procesar la posicion del mouse
    private void getMouse() {
        float wReal = (float)raiz.getWidth();
        float hReal = (float)(raiz.getHeight() - Adsobalin.HEADER);
        float[] mouse = {
            (float)(adquisidor.getMousePosition().getX() -
                raiz.getX()) / wReal,
            (float)(adquisidor.getMousePosition().getY() -
                raiz.getY()) / hReal
        };
        if (Adsobalin.isWidth) { // vertical
            mouse[0] *= Adsobalin.WIDTH;
            float h = wReal / (float)Adsobalin.RATIOWH;
            float desf = (hReal - h) / 2f;
            float esc = (float)Adsobalin.HEIGHT / h;
            mouse[1] = (mouse[1] * hReal - desf) * esc;
        }
        else { // horizontal
            mouse[1] *= Adsobalin.HEIGHT;
            float w = hReal * (float)Adsobalin.RATIOWH;
            float desf = (wReal - w) / 2f;
            float esc = (float)Adsobalin.WIDTH / w;
            mouse[0] = (mouse[0] * wReal - desf) * esc;
        }
        mousePos = Tools.vecSuma(camaraPos, mouse);
    }
}
