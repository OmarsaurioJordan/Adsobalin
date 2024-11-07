package logic.interfaz;
// interfaz principal para inicio y configuracion del software

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.util.Random;

public class Menu extends GUIs {
    
    // configuracion elegida por el usuario
    private int grupo = Adsobalin.LIBRE;
    private int estilo = 0;
    
    // guarda todos los sprites de avatares
    private Image[] rojo = new Image[29];
    private Image[] azul = new Image[29];
    private ImageView sprAvatar = new ImageView();
    
    // cajones para poner texto
    private TextField fieldIP = new TextField("");
    private TextField fieldNombre = new TextField("");
    
    // objeto para guardar configuracion
    private SaveGame data = new SaveGame();
    private String datapath = "src/config/config.properties";
    
    public Menu(Stage raiz) {
        super(raiz);
        
        // permite guardar la informacion de la interfaz cuando esta cierra
        raiz.sceneProperty().addListener((obs, oldScn, newScn) -> {
            if (oldScn == this) {
                guardarDatos();
            }
        });
        raiz.setOnCloseRequest(event -> {
            guardarDatos();
        });
        
        // cargar datos del usuario previo
        leerDatos();
        
        // variables compactas para escritura eficiente
        float ww = (float)Adsobalin.width;
        float hh = (float)Adsobalin.height;
        float esc = (float)Adsobalin.escala;
        
        // pintar el fondo de la interfaz
        pintarFondo();
        
        // colocar el titulo principal arriba en el centro
        float[] wh = {489f * 0.5f * esc, 323f * 0.5f * esc};
        Image titulo = new Image("assets/interfaz/titulo.png",
            wh[0], wh[1], false, false);
        gc.drawImage(titulo, ww / 2f - wh[0] / 2f, hh * 0.05f);
        
        // colocar el simbolo de omwekiatl en la esquina inferior izquierda
        wh[0] = 175f * 0.6f * esc; wh[1] = 200f * 0.6f * esc;
        Image creditos = new Image("assets/interfaz/creditos.png",
            wh[0], wh[1], false, false);
        gc.drawImage(creditos, ww * 0.02f, hh * 0.98f - wh[1]);
        
        // colocar la version de compilacion arriba a la izquierda
        setLabel("v" + Adsobalin.version, ww * 0.05f, hh * 0.05f);
        
        // colocar una caja de escritura al lado izquierdo, para nombre
        setLabel("Nombre", ww * 0.4f - 100f * esc + 10f * esc,
                323f * 0.5f * esc + hh * 0.2f - 32f * esc);
        TextField fieldNombre = this.fieldNombre;
        fieldNombre.setFont(Adsobalin.letras);
        fieldNombre.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 4) {
                fieldNombre.setText(oldV);
            }
        });
        fieldNombre.setLayoutX(ww * 0.4f - 100f * esc);
        fieldNombre.setLayoutY(323f * 0.5f * esc + hh * 0.2f);
        fieldNombre.setPrefWidth(100f * esc);
        gui.getChildren().add(fieldNombre);
        
        // colocar una caja de escritura al lado derecho, para IP
        setLabel("IP de server o vacío", ww * 0.5f + 10f * esc,
                323f * 0.5f * esc + hh * 0.2f - 32f * esc);
        TextField fieldIP = this.fieldIP;
        fieldIP.setFont(Adsobalin.letras);
        fieldIP.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 15) {
                fieldIP.setText(oldV);
            }
        });
        fieldIP.setLayoutX(ww * 0.5);
        fieldIP.setLayoutY(323f * 0.5f * esc + hh * 0.2f);
        fieldIP.setPrefWidth(200f * esc);
        gui.getChildren().add(fieldIP);
        
        // colocar el gran boton de play abajo a la derecha
        Button play = setButton("assets/interfaz/play",
                ww * 0.82f, hh * 0.8f, 138f, true);
        play.setOnAction(event -> ejecutar());
        
        // colocar un boton de cambio de estilo a la derecha
        Button next = setButton("assets/interfaz/right",
                ww * 0.63f, hh * 0.75f, 72f, true);
        next.setOnAction(event -> cambiarEstilo(1));
        
        // colocar un boton de cambio de estilo a la izquierda
        Button previous = setButton("assets/interfaz/left",
                ww * 0.37f, hh * 0.75f, 72f, false);
        previous.setOnAction(event -> cambiarEstilo(-1));
        
        // colocar el boton con la forma del avatar
        Button avatar = setAvatar(ww / 2f, hh * 0.75f);
        avatar.setOnAction(event -> cambiarGrupo());
    }
    
    private void ejecutar() {
        // al pulsar el boton grande de play
        raiz.setScene(new Lobby(raiz));
    }
    
    private void cambiarEstilo(int direccion) {
        // al pulsar alguno de los botones de cambio de estilo
        estilo += direccion;
        if (estilo <= 0) {
            estilo = 28;
        }
        else if (estilo > 28) {
            estilo = 1;
        }
        setAvatar(false);
    }
    
    private void cambiarGrupo() {
        // al pulsar el avatar cambia de grupo (color)
        if (grupo == Adsobalin.AZUL) {
            grupo = Adsobalin.ROJO;
        }
        else {
            grupo = Adsobalin.AZUL;
        }
        setAvatar(false);
    }
    
    private Button setAvatar(float posX, float posY) {
        // primero se obtienen las imagenes de estado del boton
        float lado = 120f * 0.75f * (float)Adsobalin.escala;
        for (int i = 0; i < 29; i++) {
            rojo[i] = new Image("assets/rojos/rojo" + i + ".png",
                lado, lado, false, false);
        }
        for (int i = 0; i < 29; i++) {
            azul[i] = new Image("assets/azules/azul" + i + ".png",
                lado, lado, false, false);
        }
        
        // crea como tal el boton y le pone sus propiedades basicas
        Button boton = new Button();
        boton.setFocusTraversable(false);
        boton.setBackground(Background.EMPTY);
        boton.setGraphic(sprAvatar);
        setAvatar(false);
        
        // se crea la mascara de colision con forma circular
        Circle circulo = new Circle(lado / 2f);
        circulo.setCenterX(lado / 2f + 7f * Adsobalin.escala);
        circulo.setCenterY(lado / 2f + 2f * Adsobalin.escala);
        boton.setClip(circulo);
        
        // se establece el comportamiento del mouse para cambiar estados
        boton.setOnMouseEntered(event -> setAvatar(true));
        boton.setOnMouseExited(event -> setAvatar(false));
        boton.setOnMousePressed(event -> setAvatar(true));
        boton.setOnMouseReleased(event -> setAvatar(false));
        
        // coloca el boton en la interfaz en la posicion x,y
        boton.setLayoutX(posX - lado / 2f);
        boton.setLayoutY(posY - lado / 2f);
        gui.getChildren().add(boton);
        return boton;
    }
    
    private void setAvatar(boolean isReversed) {
        // pone como tal la imagen correspondiente al boton
        if (isReversed) {
            // la coloca con color invertido
            if (grupo == Adsobalin.AZUL) {
                sprAvatar.setImage(rojo[estilo]);
            }
            else {
                sprAvatar.setImage(azul[estilo]);
            }
        }
        else {
            // o con el color que realmente debe tener
            if (grupo == Adsobalin.AZUL) {
                sprAvatar.setImage(azul[estilo]);
            }
            else {
                sprAvatar.setImage(rojo[estilo]);
            }
        }
    }
    
    private void leerDatos() {
        Random rnd = new Random();
        grupo = 1 + rnd.nextInt(2); // 1 a 2
        estilo = 1 + rnd.nextInt(28); // 1 a 28
        if (data.cargarData(datapath)) {
            if (!data.getData("inicial", "1").equals("1")) {
                grupo = Integer.parseInt(
                    data.getData("grupo", String.valueOf(grupo)));
                estilo = Integer.parseInt(
                    data.getData("estilo", String.valueOf(estilo)));
                fieldNombre.setText(data.getData("nombre", ""));
                fieldIP.setText(data.getData("ip", ""));
            }
        }
    }
    
    private void guardarDatos() {
        data.setData("inicial", "0");
        data.setData("grupo", String.valueOf(grupo));
        data.setData("estilo", String.valueOf(estilo));
        data.setData("nombre", fieldNombre.getText());
        data.setData("ip", fieldIP.getText());
        data.guardarData(datapath);
    }
}
