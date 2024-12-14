package logic.interfaz;
// clase principal que llama a todas las demas

import java.util.Arrays;
import logic.sincronia.Conector;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javax.swing.JOptionPane;
import java.util.Random;
import javafx.application.Platform;
import logic.abstractos.Movil;
import logic.sincronia.Envios;

public class Adsobalin extends Application {
    
    // para poder conectarse solo con instancias de misma version
    public static final int VERSION = 1;
    
    // sistema aleatorio para todo el software
    public static Random DADO = new Random(System.currentTimeMillis());
    
    // la talla de todos los componentes visuales, todo es escalable
    public static final double WIDTH = 720d;
    public static final double HEIGHT = 480d;
    public static final double RATIOWH = WIDTH / HEIGHT;
    public static final double HEADER = 40d;
    // es true si llena el largo de la ventana principal, sino, llena el alto
    public static boolean isWidth = true;
    
    // la fuente de texto usada en todo el software
    public static Font letricas = new Font("Verdana", 14);
    public static Font letras = new Font("Verdana", 18);
    public static Font letrimedias = new Font("Verdana", 27);
    public static Font letrotas = new Font("Verdana", 36);
    
    // indices para grupos
    public static final int GRU_AZUL = 0;
    public static final int GRU_ROJO = 1;
    
    // indices para indicar estado del juego
    public static final int EST_MENU = 0;
    public static final int EST_LOBBY = 1;
    public static final int EST_JUEGO = 2;
    public static final int EST_FINAL = 3;
    public static int estado = EST_MENU;
    // dice si el software esta en modo servidor o cliente
    public static boolean isServer = false;
    // si permite que se conecten en pleno juego
    public static boolean isEncursable = true;
    // puntos que ha ganado cada grupo
    public static int[] gruPoints = {0, 0};
    
    // otras configuraciones
    public static final int NAME_LEN = 4;
    
    // instancia un objeto que administra la comunicacion
    private static final int PUERTO = 44362;
    public static Conector conector;
    
    // configuracion elegida por el usuario
    public static String nombre = "";
    public static int grupo = GRU_AZUL;
    public static int estilo = 0;
    public static int indice = -1;
    
    // informacion de usuarios conectados si es servidor
    // el ind de usuario es el indice en el arreglo
    // el grupo depende de si es ind < 9 o no
    // cuando el nombre es vacio, se supone es un bot
    // las IP se conservan durante la partida para esperar reconexion
    public static String[] userIP = new String[18];
    public static String[] userName = new String[18];
    public static int[] userStyle = new int[18];
    public static float[] userPing = new float[18];
    public static int [] userPoints = new int[18];
    
    // objeto que manejara todos los sonidos
    public static Sonidos masterSound = new Sonidos();
    
    // donde guarda el archivo de configuracion
    public static final String DATAPATH = "src/config/config.properties";
    
    public static void main(String[] args) {
        conector = new Conector(PUERTO);
        if (conector.isSocketOk()) {
            launch(args);
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "no se pudo abrir el socket en: " + PUERTO);
            System.exit(0);
        }
    }
    
    @Override
    public void start(Stage raiz) {
        conector.setEscuchar(raiz);
        raiz.setTitle("Adsobalin");
        raiz.setScene(new Menu(raiz));
        
        raiz.widthProperty().addListener((obs, oldW, newW) -> {
            reEscalar(raiz, (double)newW, raiz.getHeight() - HEADER);
        });
        
        raiz.heightProperty().addListener((obs, oldH, newH) -> {
            reEscalar(raiz, raiz.getWidth(), (double)newH - HEADER);
        });
        
        //raiz.setMaximized(true);
        raiz.show();
    }
    
    private void reEscalar(Stage raiz, double width, double height) {
        double idealW = height * RATIOWH;
        double sc, newW, newH;
        isWidth = idealW > raiz.getWidth();
        if (isWidth) {
            sc = width / this.WIDTH;
            newW = width;
            newH = width / RATIOWH;
        }
        else {
            sc = height / this.HEIGHT;
            newW = idealW;
            newH = height;
        }
        Scale esc = new Scale(sc, sc);
        esc.setPivotX(0);
        esc.setPivotY(0);
        Translate trans = new Translate(((raiz.getWidth() - newW) / 2d) / sc,
                (((raiz.getHeight() - HEADER) - newH) / 2d) / sc);
        raiz.getScene().getRoot().getTransforms().setAll(esc, trans);
    }
    
    public static void addPoints(boolean isKill, int indWin, int indLos) {
        if (isServer) {
            int damage = 1;
            if (isKill) {
                damage = Movil.VIDA_MAX;
                if (!userIsNPC(indLos)) {
                    damage = (int)(damage * 1.5f);
                }
            }
            userPoints[indWin] += damage;
            gruPoints[userGetGrupo(indWin)] += damage;
        }
    }
    
    public static int otroGrupo(int grupo) {
        int otr = 0;
        if (grupo == 0) {
            otr = 1;
        }
        return otr;
    }
    
    public static boolean userContNombre(String nombre) {
        return Arrays.asList(userName).contains(nombre);
    }
    
    public static boolean userContEstilo(int estilo) {
        return Arrays.stream(userStyle).anyMatch(n -> n == estilo);
    }
    
    public static int userBestPoints() {
        // retorna ind con usuario que tiene mejores puntos, o -1 si empate
        int best = -1;
        int pts = 0;
        for (int i = 0; i < 18; i++) {
            if (userPoints[i] > pts) {
                pts = userPoints[i];
                best = i;
            }
            else if (userPoints[i] == pts) {
                best = -1;
            }
        }
        return best;
    }
    
    public static int userHayCupo() {
        int ind = -1;
        for (int i = 0; i < 18; i++) {
            if (userIP[i].isEmpty()) {
                ind = i;
                break;
            }
        }
        return ind;
    }
    
    public static int userGetInd(String ip) {
        for (int i = 0; i < 18; i++) {
            if (userIP[i].equals(ip)) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean userIsNPC(int ind) {
        return userName[ind].isEmpty();
    }
    
    public static boolean userSelf(int ind) {
        return userName[ind].equals(nombre);
    }
    
    public static int userGetGrupo(String ip) {
        int ind = userGetInd(ip);
        return userGetGrupo(ind);
    }
    
    public static void userForceName(int ind, String nombre) {
        userName[ind] = nombre;
    }
    
    public static int userGetGrupo(int ind) {
        if (ind < 9) {
            return GRU_AZUL;
        }
        return GRU_ROJO;
    }
    
    public static int userCupoGrupo(int indGrupo) {
        int ind = -1;
        if (indGrupo == GRU_AZUL) {
            for (int i = 0; i < 9; i++) {
                if (userIP[i].isEmpty()) {
                    ind = i;
                    break;
                }
            }
        }
        else if (indGrupo == GRU_ROJO) {
            for (int i = 9; i < 18; i++) {
                if (userIP[i].isEmpty()) {
                    ind = i;
                    break;
                }
            }
        }
        return ind;
    }
    
    public static int userAdd(String ip, String nombre,
            int estilo, int grupo) {
        // primero debe buscar un espacio disponible, segun grupo deseado
        int ind = userCupoGrupo(grupo);
        // ignorar el grupo deseado y buscar en todo el espacio
        if (ind == -1) {
            ind = userHayCupo();
        }
        // en caso de hallarlo, ingresara los datos
        if (ind != -1) {
            userIP[ind] = ip;
            userName[ind] = nombre;
            userStyle[ind] = estilo;
            userPing[ind] = Conector.PING;
            if (estado != EST_JUEGO) {
                userPoints[ind] = 0;
            }
        }
        return ind;
    }
    
    public static void userClean(int ind, boolean forcePts, boolean forceIP) {
        userName[ind] = "";
        userStyle[ind] = 0;
        userPing[ind] = 0f;
        if (estado != EST_JUEGO || forcePts) {
            userPoints[ind] = 0;
        }
        if (estado != EST_JUEGO || forceIP) {
            userIP[ind] = "";
        }
    }
    
    public static void userClean() {
        gruPoints[0] = 0;
        gruPoints[1] = 0;
        for (var i = 0; i < 18; i++) {
            userClean(i, true, true);
        }
    }
    
    public static void userReLobby() {
        gruPoints[0] = 0;
        gruPoints[1] = 0;
        for (var i = 0; i < 18; i++) {
            if (userName[i].isEmpty()) {
                userClean(i, true, true);
            }
            else {
                userPoints[i] = 0;
            }
        }
    }
    
    public static void userPingStep(Stage raiz, float delta) {
        for (int i = 0; i < 18; i++) {
            if (i == Adsobalin.indice) {
                continue;
            }
            if (!userName[i].isEmpty()) {
                userPing[i] -= delta;
                if (userPing[i] <= 0) {
                    // hacer desconexion, el avatar morira atomaticamente
                    userClean(i, false, false);
                    // poner NPC si esta activo, activa respawn
                    Mundo.setRespawnNPC(i);
                    // actualizar lobby
                    try {
                        if (estado == EST_LOBBY) {
                            Lobby gui = (Lobby)raiz.getScene();
                            Platform.runLater(() -> {
                                gui.reDibujar();
                            });
                        }
                    }
                    catch (Exception e) {}
                    // notificar desconexion
                    Envios.sendConex(i, false);
                    notificaConex(i, false);
                }
            }
        }
    }
    
    public static void notificaKill(int golpeador, int golpeado) {
        Mundo mun = null;
        try {
            mun = (Mundo)conector.getRaiz().getScene();
        }
        catch (Exception e) {
            mun = null;
        }
        // luego de verificar que esta en modo juego
        if (mun != null) {
            // solo se muestran notificaciones cuando muere un jugador, no NPC
            if (!userIsNPC(golpeado)) {
                String txt = userName[golpeado] + " < ";
                if (!userIsNPC(golpeador)) {
                    txt += userName[golpeador];
                }
                else {
                    txt += "***";
                }
                boolean isAzul = userGetGrupo(golpeado) == GRU_AZUL;
                mun.setNotificacion(txt, isAzul);
            }
        }
    }
    
    public static void notificaConex(int ind, boolean isConex) {
        Mundo mun = null;
        try {
            mun = (Mundo)conector.getRaiz().getScene();
        }
        catch (Exception e) {
            mun = null;
        }
        // luego de verificar que esta en modo juego
        if (mun != null) {
            String txt = userName[ind];
            if (!txt.isEmpty()) {
                if (isConex) {
                    txt += " (Hi)";
                }
                else {
                    txt += " (X)";
                }
                boolean isAzul = userGetGrupo(ind) == GRU_AZUL;
                mun.setNotificacion(txt, isAzul);
            }
        }
    }
}
