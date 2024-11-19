package logic.interfaz;
// interfaz principal para inicio y configuracion del software

import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.shape.Circle;
import logic.sincronia.Envios;
import logic.sincronia.Conector;

public class Menu extends GUIs {
    
    // guarda todos los sprites de avatares
    private Image[] rojo = new Image[29];
    private Image[] azul = new Image[29];
    private ImageView sprAvatar = new ImageView();
    
    // cajones para poner texto
    private TextField fieldIP = new TextField("");
    private TextField fieldNombre = new TextField("");
    
    // objeto para guardar configuracion
    private SaveGame data = new SaveGame();
    
    
    public Menu(Stage raiz) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_MENU;
        Adsobalin.isServer = false;
        Conector.myServer = "";
        Adsobalin.userClean();
        
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
        float ww = (float)Adsobalin.WIDTH;
        float hh = (float)Adsobalin.HEIGHT;
        float esc = (float)Adsobalin.ESCALA;
        
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
        setLabel("versión\n   " + Adsobalin.VERSION, ww * 0.05f, hh * 0.05f);
        
        // colocar una caja de escritura al lado izquierdo, para nombre
        setLabel("Nombre", ww * 0.4f - 100f * esc + 10f * esc,
                323f * 0.5f * esc + hh * 0.2f - 32f * esc);
        TextField fNombre = fieldNombre;
        fNombre.setFont(Adsobalin.letras);
        fNombre.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > Adsobalin.NAME_LEN) {
                fNombre.setText(oldV);
            }
        });
        fNombre.setLayoutX(ww * 0.4f - 100f * esc);
        fNombre.setLayoutY(323f * 0.5f * esc + hh * 0.2f);
        fNombre.setPrefWidth(100f * esc);
        gui.getChildren().add(fNombre);
        
        // colocar una caja de escritura al lado derecho, para IP
        setLabel("IP de server o vacío", ww * 0.5f + 10f * esc,
                323f * 0.5f * esc + hh * 0.2f - 32f * esc);
        TextField fIP = fieldIP;
        fIP.setFont(Adsobalin.letras);
        fIP.textProperty().addListener((obs, oldV, newV) -> {
            if (newV.length() > 15) {
                fIP.setText(oldV);
            }
        });
        fIP.setLayoutX(ww * 0.5);
        fIP.setLayoutY(323f * 0.5f * esc + hh * 0.2f);
        fIP.setPrefWidth(200f * esc);
        gui.getChildren().add(fIP);
        
        // colocar el gran boton de play abajo a la derecha
        Button play = setButton("assets/interfaz/play",
                ww * 0.82f, hh * 0.8f, 138f, true);
        play.setOnAction(event -> ejecutar());
        
        // colocar un boton de cambio de estilo a la izquierda
        Button previous = setButton("assets/interfaz/left",
                ww * 0.37f, hh * 0.75f, 72f, false);
        previous.setOnAction(event -> cambiarEstilo(-1));
        
        // colocar un boton de cambio de estilo a la derecha
        Button next = setButton("assets/interfaz/right",
                ww * 0.63f, hh * 0.75f, 72f, true);
        next.setOnAction(event -> cambiarEstilo(1));
        
        // colocar el boton con la forma del avatar
        Button avatar = setAvatar(ww / 2f, hh * 0.75f);
        avatar.setOnAction(event -> cambiarGrupo());
    }
    
    private void ejecutar() {
        // al pulsar el boton grande de play
        Adsobalin.nombre = filtroTexto(fieldNombre.getText(),
                Adsobalin.NAME_LEN);
        if (!Adsobalin.nombre.equals(fieldNombre.getText())) {
            fieldNombre.setText(Adsobalin.nombre);
            setMensaje("escriba un nombre válido", false);
        }
        else if (Adsobalin.nombre.isEmpty()) {
            setMensaje("escriba un nombre", false);
        }
        else {
            String ip = fieldIP.getText();
            if (ip.isEmpty()) {
                // moverse al lobby para ser servidor
                guardarDatos();
                Adsobalin.isServer = true;
                raiz.setScene(new Lobby(raiz));
            }
            else if (!isIPv4(ip)) {
                setMensaje("escriba una IP válida", false);
            }
            else {
                Envios.sendHola(Adsobalin.nombre, ip,
                        Adsobalin.estilo, Adsobalin.grupo);
                setMensaje("solicitud enviada", true);
            }
        }
    }
    
    private void cambiarEstilo(int direccion) {
        // al pulsar alguno de los botones de cambio de estilo
        Adsobalin.estilo += direccion;
        if (Adsobalin.estilo <= 0) {
            Adsobalin.estilo = 28;
        }
        else if (Adsobalin.estilo > 28) {
            Adsobalin.estilo = 1;
        }
        drawAvatar(false);
    }
    
    private void cambiarGrupo() {
        // al pulsar el avatar cambia de grupo (color)
        if (Adsobalin.grupo == Adsobalin.GRU_AZUL) {
            Adsobalin.grupo = Adsobalin.GRU_ROJO;
        }
        else {
            Adsobalin.grupo = Adsobalin.GRU_AZUL;
        }
        drawAvatar(false);
    }
    
    private Button setAvatar(float posX, float posY) {
        // primero se obtienen las imagenes de estado del boton
        float lado = 120f * 0.75f * (float)Adsobalin.ESCALA;
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
        drawAvatar(false);
        
        // se crea la mascara de colision con forma circular
        Circle circulo = new Circle(lado / 2f);
        circulo.setCenterX(lado / 2f + 7f * Adsobalin.ESCALA);
        circulo.setCenterY(lado / 2f + 2f * Adsobalin.ESCALA);
        boton.setClip(circulo);
        
        // se establece el comportamiento del mouse para cambiar estados
        boton.setOnMouseEntered(event -> drawAvatar(true));
        boton.setOnMouseExited(event -> drawAvatar(false));
        boton.setOnMousePressed(event -> drawAvatar(true));
        boton.setOnMouseReleased(event -> drawAvatar(false));
        
        // coloca el boton en la interfaz en la posicion x,y
        boton.setLayoutX(posX - lado / 2f);
        boton.setLayoutY(posY - lado / 2f);
        gui.getChildren().add(boton);
        return boton;
    }
    
    private void drawAvatar(boolean isReversed) {
        // pone como tal la imagen correspondiente al boton
        if (isReversed) {
            // la coloca con color invertido
            if (Adsobalin.grupo == Adsobalin.GRU_AZUL) {
                sprAvatar.setImage(rojo[Adsobalin.estilo]);
            }
            else {
                sprAvatar.setImage(azul[Adsobalin.estilo]);
            }
        }
        else {
            // o con el color que realmente debe tener
            if (Adsobalin.grupo == Adsobalin.GRU_AZUL) {
                sprAvatar.setImage(azul[Adsobalin.estilo]);
            }
            else {
                sprAvatar.setImage(rojo[Adsobalin.estilo]);
            }
        }
    }
    
    private void leerDatos() {
        Adsobalin.grupo = 1 + Adsobalin.DADO.nextInt(2); // 1 a 2
        Adsobalin.estilo = 1 + Adsobalin.DADO.nextInt(28); // 1 a 28
        if (data.cargarData(Adsobalin.DATAPATH)) {
            if (data.getData("inicial", "0").equals("1")) {
                Adsobalin.grupo = Integer.parseInt(
                    data.getData("grupo",
                            String.valueOf(Adsobalin.grupo)));
                Adsobalin.estilo = Integer.parseInt(
                    data.getData("estilo",
                            String.valueOf(Adsobalin.estilo)));
                fieldNombre.setText(data.getData("nombre", ""));
                fieldIP.setText(data.getData("ip", ""));
            }
        }
    }
    
    private void guardarDatos() {
        data.setData("inicial", "1");
        data.setData("grupo", String.valueOf(Adsobalin.grupo));
        data.setData("estilo", String.valueOf(Adsobalin.estilo));
        data.setData("nombre", fieldNombre.getText());
        data.setData("ip", fieldIP.getText());
        data.guardarData(Adsobalin.DATAPATH);
    }
    
    private boolean isIPv4(String ip) {
        try {
            String[] tramo = ip.split("\\.");
            if (tramo.length != 4) {
                return false;
            }
            for (int i = 0; i < 4; i++) {
                int num = Integer.parseInt(tramo[i]);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
        }
        catch (Exception ex) {
            return false;
        }
        return true;
    }
    
    private String filtroTexto(String texto, int limite) {
        // el limite puede ser 0 o -1 para no imponer un limite
        String f = "abcdefghijklmnopqrstuvwxyz0123456789" +
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//(){}[]:;-_?=,.+/*#$%&!|<>";
        String txt = "";
        int lim = 0;
        char c;
        for (int i = 0; i < texto.length(); i++) {
            c = texto.charAt(i);
            if (f.indexOf(c) != -1) {
                txt += c;
                lim++;
                if (lim == limite) {
                    break;
                }
            }
        }
        return txt;
    }
}
