package logic.interfaz;
// clase principal que llama a todas las demas

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.text.Font;

public class Adsobalin extends Application {
    
    // para poder conectarse solo con instancias de misma version
    public static int version = 1;
    
    // la talla de todos los componentes visuales
    public static double width = 720d; // 720
    public static double height = 480d; // 480
    public static double ratioWH = width / height;
    public static double escala = width / 720d; // no cambiar
    
    // la fuente de texto usada en todo el software
    public static Font letras = new Font("Verdana", 18 * escala);
    
    // indices para grupos
    public static int LIBRE = 0;
    public static int AZUL = 1;
    public static int ROJO = 2;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage raiz) {
        raiz.setTitle("Adsobalin");
        raiz.setScene(new Menu(raiz));
        /*
        raiz.widthProperty().addListener((obs, oldW, newW) -> {
            double esc = (double)newW / width;
            raiz.getScene().getRoot().setScaleX(esc);
            raiz.getScene().getRoot().setLayoutX((esc - 1d) * width / 2d);
            //raiz.setHeight((double)newW / ratoWH);
        });
        
        raiz.heightProperty().addListener((obs, oldH, newH) -> {
            double esc = (double)newH / height;
            raiz.getScene().getRoot().setScaleY(esc);
            raiz.getScene().getRoot().setLayoutY((esc - 1d) * height / 2d);
            //raiz.setWidth((double)newH * ratioWH);
        });
        */
        //raiz.setMaximized(true);
        //raiz.setFullScreen(true);
        raiz.show();
    }
}
