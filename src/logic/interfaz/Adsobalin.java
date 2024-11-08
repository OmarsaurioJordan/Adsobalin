package logic.interfaz;
// clase principal que llama a todas las demas

import logic.sincronia.Conector;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javax.swing.JOptionPane;

public class Adsobalin extends Application {
    
    // para poder conectarse solo con instancias de misma version
    public static final int VERSION = 1;
    
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
    public static int grupo = GRU_LIBRE;
    public static int estilo = 0;
    
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
        GUIs gui = (GUIs)raiz.getScene();
        gui.setFondo(sc);
    }
}
