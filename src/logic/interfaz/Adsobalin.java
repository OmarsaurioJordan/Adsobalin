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
    public static int version = 1;
    
    // la talla de todos los componentes visuales, todo es escalable
    public static double width = 720d; // 720
    public static double height = 480d; // 480
    public static double ratioWH = width / height;
    public static double escala = width / 720d; // no cambiar
    private static double tallaHeader = 40d;
    
    // la fuente de texto usada en todo el software
    public static Font letras = new Font("Verdana", 18 * escala);
    
    // indices para grupos
    public static int LIBRE = 0;
    public static int AZUL = 1;
    public static int ROJO = 2;
    
    // otras configuraciones
    public static int nameLength = 4;
    
    // instancia un objeto que administra la comunicacion
    private static int puerto = 44362;
    public static Conector conector;
    
    public static void main(String[] args) {
        conector = new Conector(puerto);
        if (conector.isSocketOk()) {
            launch(args);
        }
        else {
            JOptionPane.showMessageDialog(null,
                    "no se pudo abrir el socket en: " + puerto);
            System.exit(0);
        }
    }
    
    @Override
    public void start(Stage raiz) {
        conector.setEscuchar(raiz);
        raiz.setTitle("Adsobalin");
        raiz.setScene(new Menu(raiz));
        
        raiz.widthProperty().addListener((obs, oldW, newW) -> {
            reEscalar(raiz, (double)newW, raiz.getHeight() - tallaHeader);
        });
        
        raiz.heightProperty().addListener((obs, oldH, newH) -> {
            reEscalar(raiz, raiz.getWidth(), (double)newH - tallaHeader);
        });
        
        //raiz.setMaximized(true);
        raiz.show();
    }
    
    private void reEscalar(Stage raiz, double width, double height) {
        double idealW = height * ratioWH;
        double sc, newW, newH;
        if (idealW > raiz.getWidth()) {
            sc = width / this.width;
            newW = width;
            newH = width / ratioWH;
        }
        else {
            sc = height / this.height;
            newW = idealW;
            newH = height;
        }
        Scale esc = new Scale(sc, sc);
        esc.setPivotX(0);
        esc.setPivotY(0);
        Translate trans = new Translate(((raiz.getWidth() - newW) / 2d) / sc,
                (((raiz.getHeight() - tallaHeader) - newH) / 2d) / sc);
        raiz.getScene().getRoot().getTransforms().setAll(esc, trans);
        GUIs gui = (GUIs)raiz.getScene();
        gui.setFondo(sc);
    }
}
