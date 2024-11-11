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

public class Adsobalin extends Application {
    
    // para poder conectarse solo con instancias de misma version
    public static final int VERSION = 1;
    
    // sistema aleatorio para todo el software
    public static Random DADO = new Random(System.currentTimeMillis());
    
    // la talla de todos los componentes visuales, todo es escalable
    public static final double WIDTH = 720d; // 720
    public static final double HEIGHT = 480d; // 480
    public static final double RATIOWH = WIDTH / HEIGHT;
    public static final double ESCALA = WIDTH / 720d; // no cambiar
    private static final double HEADER = 40d;
    
    // la fuente de texto usada en todo el software
    public static Font letras = new Font("Verdana", 18 * ESCALA);
    
    // indices para grupos
    public static final int GRU_LIBRE = 0;
    public static final int GRU_AZUL = 1;
    public static final int GRU_ROJO = 2;
    
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
    
    // otras configuraciones
    public static final int NAME_LEN = 4;
    
    // instancia un objeto que administra la comunicacion
    private static final int PUERTO = 44362;
    public static Conector conector;
    
    // configuracion elegida por el usuario
    public static String nombre = "";
    public static int grupo = GRU_LIBRE;
    public static int estilo = 0;
    public static int indice = -1;
    
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
        if (idealW > raiz.getWidth()) {
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
    
    public static boolean userContNombre(String nombre) {
        return Arrays.asList(Conector.userName).contains(nombre);
    }
    
    public static boolean userContEstilo(int estilo) {
        return Arrays.stream(Conector.userStyle).anyMatch(n -> n == estilo);
    }
    
    public static int userHayCupo() {
        int ind = -1;
        for (int i = 0; i < 18; i++) {
            if (Conector.userIP[i].isEmpty()) {
                ind = i;
                break;
            }
        }
        return ind;
    }
    
    public static int userGetInd(String ip) {
        for (int i = 0; i < 18; i++) {
            if (Conector.userIP[i].equals(ip)) {
                return i;
            }
        }
        return -1;
    }
    
    public static int userGetGrupo(String ip) {
        int ind = userGetInd(ip);
        return userGetGrupo(ind);
    }
    
    public static int userGetGrupo(int ind) {
        if (ind == -1) {
            return GRU_LIBRE;
        }
        else if (ind < 9) {
            return GRU_AZUL;
        }
        return GRU_ROJO;
    }
    
    public static int userCupoGrupo(int indGrupo) {
        int ind = -1;
        if (indGrupo == GRU_AZUL) {
            for (int i = 0; i < 9; i++) {
                if (Conector.userIP[i].isEmpty()) {
                    ind = i;
                    break;
                }
            }
        }
        else if (indGrupo == GRU_ROJO) {
            for (int i = 9; i < 18; i++) {
                if (Conector.userIP[i].isEmpty()) {
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
            Conector.userIP[ind] = ip;
            Conector.userName[ind] = nombre;
            Conector.userStyle[ind] = estilo;
            Conector.userPing[ind] = Conector.PING;
        }
        return ind;
    }
    
    public static void userClean(int ind) {
        Conector.userIP[ind] = "";
        Conector.userName[ind] = "";
        Conector.userStyle[ind] = 0;
        Conector.userPing[ind] = 0f;
    }
    
    public static void userClean() {
        for (var i = 0; i < 18; i++) {
            userClean(i);
        }
    }
}
