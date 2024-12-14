package logic.interfaz;
// interfaz con main loop y lista de objetos, donde se ejecuta el juego

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.scene.robot.Robot;
import javafx.scene.image.Image;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import logic.abstractos.*;
import logic.objetos.*;
import logic.sincronia.*;

public class Mundo extends GUIs {
    
    // constantes globales de la simulacion
    public static final double FPS = 60d;
    public static final int RADIO = 720;
    public static int radioMundo;
    public static float[] centroMundo = new float[2];
    public static final float TEMP_RESPAWN_MAX = 7f;
    public static final float RAD_RESPAWN = 100f;
    public static final float NOTIFI_TIME = 7f;
    public static final float NOTIFI_HEIGTH = (float)Adsobalin.HEIGHT * 0.72f;
    public static final float NOTIFI_SEPARACION = 32f;
    
    // guardara todos los objetos que existen instanciados en el juego
    public static ArrayList<Object> pool = new ArrayList<>();
    
    // duracion de la partida
    public static float tiempoRestante;
    // si deben invocarse NPCs, -1 significa desactivado, 0 existe, >0 respawn
    private static float[] npcRespawn = new float[18];
    // tiene el objeto jugador actual, o ninguno si ha muerto
    private static Player myPlayer = null;
    // temporizador de respawn de player
    public static float tempRespawnPlayer = TEMP_RESPAWN_MAX +
            Adsobalin.DADO.nextFloat();
    
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
    // centro de la camara en el mundo, para calcular sonidos
    public static float[] camaraCen = {0f, 0f};
    // posicion del mouse
    public static float[] mousePos = {0f, 0f};
    // sistema para obtener coordenadas de mouse
    private static Robot adquisidor = new Robot();
    
    // temporizador para obtener la informacion del mapa del servidor
    public float temp_get_mapa = 1f;
    
    // imagen del mouse
    private Image mouseImg = new Image("assets/entorno/mouse.png",
        72f * 0.75f, 72f * 0.75f, false, false);
    // difuminado de la camara
    private Image difuminado = new Image("assets/entorno/camara.png",
        Adsobalin.WIDTH, Adsobalin.HEIGHT, false, false);
    // fondo del mundo
    private Image fondo;
    
    // imagen de vida
    private Image vidaImg = new Image("assets/entorno/vida.png",
        72f * 0.4f, 72f * 0.4f, false, false);
    // imagen de municion
    private Image municionImg = new Image("assets/entorno/municion.png",
        110f * 0.75f, 110f * 0.75f, false, false);
    // imagen de notificacion
    private Image notifiImgAzul = new Image("assets/interfaz/notifyAzul.png",
        180f * 0.75f, 40f * 0.75f, false, false);
    private Image notifiImgRojo = new Image("assets/interfaz/notifyRojo.png",
        180f * 0.75f, 40f * 0.75f, false, false);
    
    // hilo para todo el main loop del juego
    private AnimationTimer aniLoop;
    
    // estructuras para las notificaciones
    private ArrayList<Float> notifiReloj = new ArrayList<>();
    private ArrayList<String> notifiMsj = new ArrayList<>();
    private ArrayList<Boolean> notifiAzul = new ArrayList<>();
    
    public Mundo(Stage raiz, boolean[] npcok, int talla,
            int obstaculos, int tiempo) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_JUEGO;
        limpiarAll();
        if (talla > 10) {
            radioMundo = talla;
        }
        else {
            radioMundo = (int)((RADIO / 2) * (1 + talla));
        }
        centroMundo[0] = radioMundo;
        centroMundo[1] = radioMundo;
        tiempoRestante = tiempo * 60f + TEMP_RESPAWN_MAX;
        if (Adsobalin.isServer) {
            for (int i = 0; i < 18; i++) {
                if (npcok[i]) {
                    if (Adsobalin.userIsNPC(i)) {
                        npcRespawn[i] = TEMP_RESPAWN_MAX +
                                Adsobalin.DADO.nextFloat();
                    }
                    else {
                        npcRespawn[i] = 0f;
                    }
                }
                else {
                    npcRespawn[i] = -1f;
                }
            }
        }
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
        }
        // crea decorados
        CreaElementosRandom(0.5f, Baldoza.class,
                Decorado.RADIO, 2.5f, Baldoza.class);
        
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
        aniLoop = new AnimationTimer() {
            private long last = 0L;
            private double delta = 0d;
            @Override
            public void handle(long now) {
                double dlt = (now - last) / 1000000000.0d;
                delta = Math.min(1d, delta + dlt);
                if (delta > 1d / FPS) {
                    if (Adsobalin.indice != -1) {
                        step((float)delta);
                        draw();
                    }
                    delta = 0d;
                }
                last = now;
            }
        };
        aniLoop.start();
        
        // detener el hilo del loop para evitar errores
        raiz.sceneProperty().addListener((obs, oldScn, newScn) -> {
            if (oldScn == this) {
                aniLoop.stop();
            }
        });
        raiz.setOnCloseRequest(event -> {
            aniLoop.stop();
        });
    }
    
    public void limpiarAll() {
        myPlayer = null;
        pool.clear();
        tempRespawnPlayer = TEMP_RESPAWN_MAX + Adsobalin.DADO.nextFloat();
    }
    
    private void creaBases() {
        // dos bases azules a la izquierda
        float[] pos0 = {radioMundo * 0.3f, radioMundo * 0.8f};
        Base b0 = (Base)newObjeto(Base.class, pos0);
        b0.setGrupo(Adsobalin.GRU_AZUL);
        float[] pos1 = {radioMundo * 0.3f, radioMundo * 1.2f};
        Base b1 = (Base)newObjeto(Base.class, pos1);
        b1.setGrupo(Adsobalin.GRU_AZUL);
        // dos bases rojas a la derecha
        float[] pos2 = {radioMundo * 1.7f, radioMundo * 0.8f};
        Base b2 = (Base)newObjeto(Base.class, pos2);
        b2.setGrupo(Adsobalin.GRU_ROJO);
        float[] pos3 = {radioMundo * 1.7f, radioMundo * 1.2f};
        Base b3 = (Base)newObjeto(Base.class, pos3);
        b3.setGrupo(Adsobalin.GRU_ROJO);
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
    
    public static boolean colsionLine(float[] inicio, float[] llegada,
            float radio, Class<?> claseMascara) {
        // preparar las variables que haran el movimiento del circulo a pasos
        float tramo = Tools.vecDistancia(inicio, llegada);
        float angulo = Tools.vecDireccion(inicio, llegada);
        int pedazos = (int)Math.ceil(tramo / radio);
        float paso = tramo / Math.max(1, pedazos);
        float[] pos = inicio.clone();
        for (int p = 0; p < pedazos; p++) {
            // dar un pasito para verificar colisiones evitando traspasar cosas
            pos = Tools.vecMover(pos, paso, angulo);
            if (colsionObject(pos, radio, claseMascara, null) != null) {
                return true;
            }
        }
        return false;
    }
    
    public static Object getRandObject(Class<?> claseMascara,
            Object excepcion) {
        ArrayList<Object> candidatos = new ArrayList<>();
        Object obj;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (obj == excepcion) {
                continue;
            }
            if (claseMascara.isInstance(obj)) {
                candidatos.add(obj);
            }
        }
        if (!candidatos.isEmpty()) {
            return candidatos.get(Adsobalin.DADO.nextInt(
                candidatos.size()));
        }
        return null;
    }
    
    public static ArrayList<Movil> getCercanos(float[] posicion,
            float radio, Object excepcion, int grupoNoColi) {
        ArrayList<Movil> candidatos = new ArrayList<>();
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
                if (Tools.vecDistancia(posicion, aux.posicion) < radio) {
                    candidatos.add(aux);
                }
            }
        }
        return candidatos;
    }
    
    public static Object newObjeto(Class<?> clase, float[] posicion) {
        Object obj;
        try {
            obj = clase.getConstructor(float[].class).newInstance(posicion);
            pool.add(obj);
            if (Player.class.isAssignableFrom(clase)) {
                myPlayer = (Player)obj;
            }
        }
        catch (Exception ex) {
            obj = null;
        }
        return obj;
    }
    
    public static void deleteObjeto(Object obj) {
        if (pool.contains(obj)) {
            pool.remove(obj);
            if (Player.class.isInstance(obj)) {
                myPlayer = null;
            }
        }
    }
    
    public static boolean existObjeto(Object obj) {
        return pool.contains(obj);
    }
    
    public static Movil getMovil(int ind) {
        Object obj;
        Movil mov;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Movil.class.isInstance(obj)) {
                mov = (Movil)obj;
                if (mov.indice == ind) {
                    return mov;
                }
            }
        }
        return null;
    }
    
    public static void deleteProy(int llave) {
        Object obj;
        Proyectil pry;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Proyectil.class.isInstance(obj)) {
                pry = (Proyectil)obj;
                if (pry.llave == llave) {
                    deleteObjeto(pry);
                    break;
                }
            }
        }
    }
    
    public static float[] lugarRespawn(int grupo) {
        ArrayList<float[]> points = new ArrayList<>();
        Object obj;
        Base aux;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Base.class.isInstance(obj)) {
                aux = (Base)obj;
                if (aux.grupo == grupo) {
                    points.add(Tools.vecMover(aux.posicion,
                            RAD_RESPAWN, Adsobalin.DADO.nextFloat() *
                                    2f * (float)Math.PI));
                }
            }
        }
        if (points.isEmpty()) {
            points.add(centroMundo.clone());
        }
        return points.get(Adsobalin.DADO.nextInt(points.size()));
    }
    
    public static boolean isNPCinGame(int indNPC) {
        return npcRespawn[indNPC] != -1;
    }
    
    public static void setRespawnNPC(int indNPC) {
        if (isNPCinGame(indNPC)) {
            if (Adsobalin.userIsNPC(indNPC)) {
                npcRespawn[indNPC] = TEMP_RESPAWN_MAX +
                        Adsobalin.DADO.nextFloat();
            }
            else {
                Movil mov = getMovil(indNPC);
                if (mov != null) {
                    mov.morir();
                }
                npcRespawn[indNPC] = 0f;
            }
        }
    }
    
    public float[] getPlayer() {
        float[] res = new float[6];
        Object obj;
        Player ply;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Player.class.isInstance(obj)) {
                ply = (Player)obj;
                res[0] = ply.ubicacion[0];
                res[1] = ply.ubicacion[1];
                res[2] = ply.anguMira;
                if (ply.isHit()) {
                    res[3] = 1f;
                }
                if (ply.isInmune()) {
                    res[4] = 1f;
                }
                res[5] = (float)ply.estilo;
                break;
            }
        }
        return res;
    }
    
    public float[] getNPC(int ind) {
        float[] res = new float[6];
        if (!isNPCinGame(ind)) {
            res[0] = -1f;
        }
        else {
            Object obj;
            Automata aut;
            for (int n = 0; n < pool.size(); n++) {
                obj = pool.get(n);
                if (Automata.class.isInstance(obj)) {
                    aut = (Automata)obj;
                    if (aut.indice == ind) {
                        res[0] = aut.ubicacion[0];
                        res[1] = aut.ubicacion[1];
                        res[2] = aut.anguMira;
                        if (aut.isHit()) {
                            res[3] = 1f;
                        }
                        if (aut.isInmune()) {
                            res[4] = 1f;
                        }
                        res[5] = (float)aut.estilo;
                        break;
                    }
                }
            }
        }
        return res;
    }
    
    public void setNPC(int ind, float[] pos, float ang,
            byte hit, byte inmune, byte estilo, String name) {
        Object obj;
        Sombra aut;
        boolean okey = false;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Sombra.class.isInstance(obj)) {
                aut = (Sombra)obj;
                if (aut.indice == ind) {
                    if (pos[0] == 0 && pos[1] == 0) {
                        aut.morir();
                    }
                    else if (aut.estilo != estilo) {
                        aut.morir();
                        break;
                    }
                    else {
                        aut.myPing = Conector.PING;
                        aut.ubicacion = pos.clone();
                        aut.anguMira = ang;
                        aut.setTemps(hit, inmune);
                        aut.nombre = name;
                        // poner nombre en el listado general
                        if (!Adsobalin.isServer) {
                            Adsobalin.userForceName(ind, name);
                        }
                    }
                    okey = true;
                    break;
                }
            }
        }
        if (!okey && pos[0] != 0 && pos[1] != 0) {
            aut = (Sombra)newObjeto(Sombra.class, pos);
            aut.setAvatar(Adsobalin.userGetGrupo(ind), ind, estilo, name);
            aut.anguMira = ang;
            aut.setTemps(hit, inmune);
        }
    }
    
    public ArrayList<Float> getArboles() {
        ArrayList<Float> arboles = new ArrayList<>();
        Object obj;
        Arbol arb;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Arbol.class.isInstance(obj)) {
                arb = (Arbol)obj;
                arboles.add(arb.posicion[0]);
                arboles.add(arb.posicion[1]);
            }
        }
        return arboles;
    }
    
    public ArrayList<Float> getCadaveres() {
        ArrayList<Float> cdvrs = new ArrayList<>();
        Object obj;
        Cadaver cdv;
        for (int n = 0; n < pool.size(); n++) {
            obj = pool.get(n);
            if (Cadaver.class.isInstance(obj)) {
                cdv = (Cadaver)obj;
                cdvrs.add(cdv.posicion[0]);
                cdvrs.add(cdv.posicion[1]);
                cdvrs.add(cdv.angulo);
                cdvrs.add(cdv.getInfo());
            }
        }
        return cdvrs;
    }
    
    public void setNotificacion(String txt, boolean isAzul) {
        notifiMsj.add(txt);
        notifiAzul.add(isAzul);
        if (notifiReloj.isEmpty()) {
            notifiReloj.add(NOTIFI_TIME);
        }
        else {
            int tot = notifiReloj.size();
            float tMax = notifiReloj.get(tot - 1);
            float tPaso = NOTIFI_SEPARACION / (NOTIFI_HEIGTH / NOTIFI_TIME);
            notifiReloj.add(Math.max(NOTIFI_TIME, tMax + tPaso));
        }
    }
    
    private void step(float delta) {
        // ejecuta toda la logica del juego:
        // obtener y procesar la posicion del mouse
        getMouse();
        // obtener el centro de la camara
        camaraCen[0] = camaraPos[0] + (float)Adsobalin.WIDTH / 2f;
        camaraCen[1] = camaraPos[1] + (float)Adsobalin.HEIGHT / 2f;
        
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
        
        // hacer respawn del player
        if (tempRespawnPlayer != 0) {
            tempRespawnPlayer = Math.max(0f, tempRespawnPlayer - delta);
            if (tempRespawnPlayer == 0) {
                Player ply = (Player)newObjeto(Player.class,
                        lugarRespawn(Adsobalin.grupo));
                ply.setAvatar(Adsobalin.indice);
            }
        }
        
        // mover notificaciones
        float actT;
        for (int i = notifiReloj.size() - 1; i > -1; i--) {
            actT = notifiReloj.get(i) - delta;
            if (actT <= 0) {
                notifiReloj.remove(i);
                notifiMsj.remove(i);
                notifiAzul.remove(i);
            }
            else {
                notifiReloj.set(i, actT);
            }
        }
        
        // acciones que solo hara el servidor
        if (Adsobalin.isServer) {
            reaparecerNPCs(delta);
            finalizarPartida(delta);
        }
        // acciones que solo hara el cliente
        else {
            // obtener informacion del mapa
            if (temp_get_mapa != -1) {
                temp_get_mapa -= delta;
                if (temp_get_mapa <= 0) {
                    temp_get_mapa = 1f + Adsobalin.DADO.nextFloat();
                    Envios.sendPlano(Conector.myServer);
                }
            }
        }
    }
    
    private void reaparecerNPCs(float delta) {
        for (int i = 0; i < 18; i++) {
            if (npcRespawn[i] > 0) {
                npcRespawn[i] = Math.max(0f, npcRespawn[i] - delta);
                if (npcRespawn[i] == 0) {
                    Automata aut = (Automata)newObjeto(Automata.class,
                            lugarRespawn(Adsobalin.userGetGrupo(i)));
                    aut.setAvatar(Adsobalin.userGetGrupo(i), i);
                }
            }
        }
    }
    
    private void finalizarPartida(float delta) {
        tiempoRestante = Math.max(0f, tiempoRestante - delta);
        if (tiempoRestante == 0) {
            aniLoop.stop();
            pool.clear();
            
            raiz.setScene(new Resultado(raiz));
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
        
        // dibujar los nombres de los players
        Movil mvl;
        for (int n = 0; n < pool.size(); n++) {
            obj = (Objeto)pool.get(n);
            if (Movil.class.isInstance(obj)) {
                mvl = (Movil)obj;
                mvl.drawName(gc);
            }
        }
        
        // dibujar la mira difuminada de la camara sobre todo
        gc.drawImage(difuminado, 0f, 0f);
        
        // variables cortas para dibujado de interfaz
        float width = (float)Adsobalin.WIDTH;
        float height = (float)Adsobalin.HEIGHT;
        
        // escribir el contador de final de partida
        gc.setFont(Adsobalin.letrotas);
        gc.setFill(Color.SILVER);
        int min = (int)(tiempoRestante / 60f);
        int seg = (int)(tiempoRestante - min * 60);
        String time = min + ":";
        if (min < 10) {
            time = "0" + time;
        }
        if (seg < 10) {
            time += "0";
        }
        time += seg;
        gc.fillText(time, width * 0.8f, height * 0.1f);
        
        // poner las vidas del avatar y la municion
        if (myPlayer != null) {
            for (int i = 0; i < myPlayer.vida; i++) {
                gc.drawImage(vidaImg,
                        width * 0.95f - i * width * 0.03f,
                        height * 0.85f
                );
            }
            int i = 0;
            for (int h = 1; h >= 0; h--) {
                for (int w = 0; w < Movil.MUNICION_MAX / 2; w++) {
                    if (i < myPlayer.municion) {
                        gc.drawImage(municionImg,
                                width * 0.92f - w * width * 0.03f,
                                height * 0.85f + h * height * 0.04f);
                        i++;
                    }
                }
            }
        }
        // si no existe, poner el contador de respawn
        else {
            gc.setFont(Adsobalin.letrotas);
            gc.setFill(Color.SILVER);
            gc.fillText((int)Math.ceil(tempRespawnPlayer) + "s",
                    width * 0.85f, height * 0.9f);
        }
        
        // dibujar los puntajes de grupos
        int grp = Adsobalin.userGetGrupo(Adsobalin.indice);
        int utr = Adsobalin.otroGrupo(grp);
        gc.setFont(Adsobalin.letrimedias);
        if (Adsobalin.gruPoints[grp] > Adsobalin.gruPoints[utr]) {
            gc.setFill(Color.YELLOW);
        }
        else {
            gc.setFill(Color.SILVER);
        }
        gc.fillText("G: " + Adsobalin.gruPoints[grp],
                width * 0.01f, height * 0.07f);
        gc.setFont(Adsobalin.letras);
        gc.setFill(Color.SILVER);
        gc.fillText("G: " + Adsobalin.gruPoints[utr],
                width * 0.02f, height * 0.12f);
        
        // dibujar los puntajes de jugadores
        utr = Adsobalin.userBestPoints();
        gc.setFont(Adsobalin.letrimedias);
        if (Adsobalin.indice == utr) {
            gc.setFill(Color.YELLOW);
        }
        else {
            gc.setFill(Color.SILVER);
        }
        gc.fillText("P: " + Adsobalin.userPoints[Adsobalin.indice],
                width * 0.01f, height * 0.20f);
        gc.setFont(Adsobalin.letras);
        gc.setFill(Color.SILVER);
        if (utr == -1) {
            gc.fillText("B: ???",
                    width * 0.02f, height * 0.25f);
        }
        else {
            gc.fillText("B: " + Adsobalin.userPoints[utr],
                    width * 0.02f, height * 0.25f);
            gc.fillText(Adsobalin.userName[utr],
                    width * 0.02f, height * 0.3f);
        }
        
        // dibujar las notificaciones
        drawNotificaciones(gc, width, height);
        
        // dibujar el mouse
        float[] mPos = Tools.vecResta(mousePos, camaraPos);
        gc.drawImage(mouseImg, mPos[0] - mouseImg.getWidth() / 2f,
                mPos[1] - mouseImg.getHeight() / 2f);
    }
    
    private void drawNotificaciones(GraphicsContext gc,
            float width, float height) {
        gc.setFont(Adsobalin.letricas);
        gc.setFill(Color.BLACK);
        float speed = NOTIFI_HEIGTH / NOTIFI_TIME;
        float y;
        for (int i = 0; i < notifiReloj.size(); i++) {
            y = height - NOTIFI_HEIGTH + notifiReloj.get(i) * speed;
            if (notifiAzul.get(i)) {
                gc.drawImage(notifiImgAzul, width * 0.005f, y);
            }
            else {
                gc.drawImage(notifiImgRojo, width * 0.005f, y);
            }
            gc.fillText(notifiMsj.get(i), width * 0.005f + 8f, y + 20f);
        }
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
