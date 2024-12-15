package logic.interfaz;
// ofrece funcionalidades para todas las interfacez

import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;

public abstract class GUIs extends Scene {
    
    // estructuras persistentes usadas por todas las interfacez
    protected Stage raiz;
    protected GraphicsContext gc;
    protected Group gui;
    
    public GUIs(Stage raiz) {
        super(new Group(), Adsobalin.WIDTH, Adsobalin.HEIGHT, Color.BLACK);
        this.raiz = raiz;
        gui = (Group)getRoot();
        
        // crea la clase que permite el dibujado de imagenes
        Canvas lienzo = new Canvas(Adsobalin.WIDTH, Adsobalin.HEIGHT);
        gui.getChildren().add(lienzo);
        gc = lienzo.getGraphicsContext2D();
    }
    
    protected Label setLabel(String texto, float posX, float posY) {
        // creacion de texto estatico posicionado en x,y
        Label txt = new Label(texto);
        txt.setFont(Adsobalin.letras);
        txt.setLayoutX(posX);
        txt.setLayoutY(posY);
        txt.setPrefWidth(200f);
        gui.getChildren().add(txt);
        return txt;
    }
    
    protected Button setButton(String pathMocho,
            float posX, float posY, float talla, boolean isRight) {
        // crear boton triangular posicionado en la interfaz x,y
        // primero se obtienen las 3 imagenes de estado del boton
        float lado = talla * 0.75f;
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
        double desf = 5f;
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
        gui.getChildren().add(boton);
        return boton;
    }
    
    protected CheckBox setCheckBox(String texto, float posX, float posY) {
        // creacion de cajon de seleccion posicionado en x,y
        CheckBox chk = new CheckBox(texto);
        chk.setFont(Adsobalin.letras);
        chk.setLayoutX(posX);
        chk.setLayoutY(posY);
        gui.getChildren().add(chk);
        return chk;
    }
    
    protected void pintarFondo() {
        gc.setFill(Color.rgb(
                (179 + 220) / 2,
                (167 + 220) / 2,
                (125 + 220) / 2
        ));
        gc.fillRect(0, 0, Adsobalin.WIDTH, Adsobalin.HEIGHT);
    }
    
    protected void fontSize(Label label, double newSize) {
        Font actual = label.getFont();
        label.setFont(new Font(actual.getName(), newSize));
    }
    
    public void setMensaje(String texto, boolean isOk) {
        // obtener la talla de la interfaz
        float ww = (float)Adsobalin.WIDTH;
        float hh = (float)Adsobalin.HEIGHT;
        
        // crear el label como tal asignando sus propiedades
        Label msj = setLabel(texto, ww * 0.3f, hh * 0.91f);
        msj.setPrefWidth(ww * 0.4f);
        msj.setAlignment(Pos.CENTER);
        
        // pintar el fondo dependiendo del tipo de mensaje
        Color col;
        if (isOk) {
            col = Color.color(200f / 255f, 220f / 255f, 250f / 255f);
        }
        else {
            col = Color.color(250f / 255f, 200f / 255f, 200f / 255f);
        }
        BackgroundFill bcol = new BackgroundFill(
                col, CornerRadii.EMPTY, null);
        msj.setBackground(new Background(bcol));
        
        // configurar la animacion
        FadeTransition fadeOut = new FadeTransition(
                Duration.seconds(3), msj);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(1));
        fadeOut.setOnFinished(event -> gui.getChildren().remove(msj));

        // iniciar la animacion y sonar
        fadeOut.play();
        if (!isOk) {
            Sonidos.sonarUno(Sonidos.UNO_ERROR);
        }
    }
}
