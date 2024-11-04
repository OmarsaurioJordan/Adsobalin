package logic.interfaz;
// ofrece funcionalidades para todas las interfacez

import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.shape.Polygon;

public abstract class GUIs extends Scene {
    
    // estructuras persistentes usadas por todas las interfacez
    protected Stage raiz;
    protected GraphicsContext gc;
    protected Group gui;
    
    public GUIs(Stage raiz) {
        super(new Group(), Adsobalin.width, Adsobalin.height);
        this.raiz = raiz;
        
        // crea la clase que permite el dibujado de imagenes
        Canvas lienzo = new Canvas(Adsobalin.width, Adsobalin.height);
        this.gui = (Group)this.getRoot();
        gui.getChildren().add(lienzo);
        this.gc = lienzo.getGraphicsContext2D();
    }
    
    protected Label setLabel(String texto, float posX, float posY) {
        // creacion de textos estaticos posicionados en x,y
        Label txt = new Label(texto);
        txt.setFont(Adsobalin.letras);
        txt.setLayoutX(posX);
        txt.setLayoutY(posY);
        txt.setPrefWidth(200f * Adsobalin.escala);
        this.gui.getChildren().add(txt);
        return txt;
    }
    
    protected Button setButton(String pathMocho,
            float posX, float posY, float talla, boolean isRight) {
        // crear boton triangular posicionado en la interfaz x,y
        // primero se obtienen las 3 imagenes de estado del boton
        float lado = talla * 0.75f * (float)Adsobalin.escala;
        Image normal = new Image(pathMocho + "0.png",
            lado, lado, false, false);
        Image sobre = new Image(pathMocho + "1.png",
            lado, lado, false, false);
        Image pulsado = new Image(pathMocho + "2.png",
            lado, lado, false, false);
        
        // crea como tal el boton y le pone sus propiedades basicas
        Button boton = new Button();
        boton.setFocusTraversable(false);
        boton.setBackground(Background.EMPTY);
        ImageView spr = new ImageView(normal);
        boton.setGraphic(spr);
        
        // se crea la mascara de colision con forma triangular
        Polygon triangulo = new Polygon();
        double desf = 5f * Adsobalin.escala;
        if (isRight) {
            triangulo.getPoints().addAll(
                    desf * 2f, desf,
                    desf * 2f + (double)lado, desf + (double)lado / 2d,
                    desf * 2f, desf + (double)lado
            );
        }
        else {
            triangulo.getPoints().addAll(
                    desf + (double)lado, desf,
                    desf, desf + (double)lado / 2d,
                    desf + (double)lado, desf + (double)lado
            );
        }
        boton.setClip(triangulo);
        
        // se establece el comportamiento del mouse para cambiar estados
        boton.setOnMouseEntered(event -> spr.setImage(sobre));
        boton.setOnMouseExited(event -> spr.setImage(normal));
        boton.setOnMousePressed(event -> spr.setImage(pulsado));
        boton.setOnMouseReleased(event -> spr.setImage(sobre));
        
        // coloca el boton en la interfaz en la posicion x,y
        boton.setLayoutX(posX - lado / 2f);
        boton.setLayoutY(posY - lado / 2f);
        this.gui.getChildren().add(boton);
        return boton;
    }
}
