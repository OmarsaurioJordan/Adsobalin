package logic.interfaz;
// interfaz de espera para comenzar y configurar la partida

import java.util.ArrayList;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import logic.sincronia.Conector;
import logic.sincronia.Envios;

public class Lobby extends GUIs {
    
    // densidad de obstaculos maxima permitida
    public static final int DENSI_OBST_MAX = 6;
    
    // guarda los nombres de usuarios conectados
    private ArrayList<Label> nombres = new ArrayList<>();
    private ArrayList<CheckBox> activaNPCs = new ArrayList<>();
    // las configuraciones del mapa para jugar
    private ArrayList<Label> configMap = new ArrayList<>();
    // para ver si pueden conectarse clientes durante el juego
    private CheckBox chkConex;
    
    // objeto para guardar configuracion
    private SaveGame data = new SaveGame();
    
    public Lobby(Stage raiz) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_LOBBY;
        
        // permite guardar la informacion de la interfaz cuando esta cierra
        raiz.sceneProperty().addListener((obs, oldScn, newScn) -> {
            if (oldScn == this) {
                guardarDatos();
            }
        });
        raiz.setOnCloseRequest(event -> {
            guardarDatos();
        });
        
        // variables compactas para escritura eficiente
        float ww = (float)Adsobalin.WIDTH;
        float hh = (float)Adsobalin.HEIGHT;
        float esc = (float)Adsobalin.ESCALA;
        
        // pintar el fondo de la interfaz
        pintarFondo();
        
        // poner titulo de la interfaz
        Label titulo = setLabel("Lobby", ww * 0.35f, hh * 0.1f);
        fontSize(titulo, 28);
        
        // colocar los nombres en dos columnas
        BackgroundFill[] bcol = {
            new BackgroundFill(
                Color.color(200f / 255f, 220f / 255f, 250f / 255f),
                    CornerRadii.EMPTY, null),
            new BackgroundFill(
                Color.color(250f / 255f, 200f / 255f, 200f / 255f),
                    CornerRadii.EMPTY, null)
        };
        Background[] bck = {
            new Background(bcol[0]),
            new Background(bcol[1])
        };
        String[] letra = {"A", "B"};
        CheckBox auxChk;
        Label auxLabel;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 9; y++) {
                auxLabel = setLabel(
                        "*" + letra[x] + (y + 1) + "*",
                        ww * 0.4f - 100f * esc + x * (ww * 0.2f),
                        hh * 0.28f + y * (32f * esc)
                );
                auxLabel.setPrefWidth(100f * Adsobalin.ESCALA);
                auxLabel.setAlignment(Pos.CENTER);
                auxLabel.setBackground(bck[x]);
                nombres.add(auxLabel);
                // poner tambien un checkbox para el NPC
                auxChk = setCheckBox("",
                    ww * 0.4f - 130f * esc + x * (ww * 0.2f),
                    hh * 0.28f + y * (32f * esc));
                auxChk.setSelected(true);
                auxChk.setOnAction(event -> reDibujar());
                activaNPCs.add(auxChk);
            }
        }
        
        // poner el boton superior izquierdo para retornar al menu
        setLabel("volver", ww * 0.03f, hh * 0.15f);
        Button volver = setButton("assets/interfaz/left",
                ww * 0.05f, hh * 0.07f, 72f, false);
        volver.setOnAction(event -> {
            guardarDatos();
            raiz.setScene(new Menu(raiz));
        });
        
        // fondos para caracteristicas de mapa
        BackgroundFill backcar = new BackgroundFill(
                Color.color(0.8f, 1f, 0.8f), CornerRadii.EMPTY, null);
        Background backcarf = new Background(backcar);
        
        // colocar las caracteristicas de tamanno del mapa
        setLabel("Tamaño", ww * 0.75f, hh * 0.25f);
        configMap.add(setLabel("2", ww * 0.75f, hh * 0.3f));
        configMap.get(0).setBackground(backcarf);
        configMap.get(0).setPrefWidth(ww * 0.1f);
        configMap.get(0).setAlignment(Pos.CENTER);
        
        // colocar las caracteristicas de densidad obstaculos del mapa
        setLabel("Árboles", ww * 0.75f, hh * 0.4f);
        configMap.add(setLabel("3", ww * 0.75f, hh * 0.45f));
        configMap.get(1).setBackground(backcarf);
        configMap.get(1).setPrefWidth(ww * 0.1f);
        configMap.get(1).setAlignment(Pos.CENTER);
        
        // colocar las caracteristicas de duracion de partida
        setLabel("Minutos", ww * 0.75f, hh * 0.55f);
        configMap.add(setLabel("5", ww * 0.75f, hh * 0.6f));
        configMap.get(2).setBackground(backcarf);
        configMap.get(2).setPrefWidth(ww * 0.1f);
        configMap.get(2).setAlignment(Pos.CENTER);
        
        // poner checkbox para permitir conectarse durante el juego
        setLabel("conexión\nen juego", ww * 0.633f, hh * 0.7f);
        chkConex = setCheckBox("", ww * 0.666f, hh * 0.82f);
        chkConex.setSelected(true);
        
        // poner el boton de cambio de grupo abajo a la izquierda
        Label txt = setLabel("cambiar\nde grupo", ww * 0.03f, hh * 0.75f);
        txt.setPrefWidth(100f * Adsobalin.ESCALA);
        Button cmbGrupo = setButton("assets/interfaz/right",
                ww * 0.05f, hh * 0.9f, 72f, true);
        cmbGrupo.setOnAction(event -> cambioGrupo());
        
        // las cosas que solo el servidor vera
        if (Adsobalin.isServer) {
            
            // colocar el gran boton de play abajo a la derecha
            Button play = setButton("assets/interfaz/play",
                    ww * 0.82f, hh * 0.8f, 138f, true);
            play.setOnAction(event -> ejecutar());
            
            // poner los selectores de tamanno del mapa
            Button sel;
            sel = setButton("assets/interfaz/left",
                    ww * 0.7f, hh * 0.3f, 72f, false);
            sel.setOnAction(event -> setTalla(-1));
            sel = setButton("assets/interfaz/right",
                    ww * 0.88f, hh * 0.3f, 72f, true);
            sel.setOnAction(event -> setTalla(1));
            
            // poner los selectores de densidad de obstaculos del mapa
            sel = setButton("assets/interfaz/left",
                    ww * 0.7f, hh * 0.45f, 72f, false);
            sel.setOnAction(event -> setObstaculos(-1));
            sel = setButton("assets/interfaz/right",
                    ww * 0.88f, hh * 0.45f, 72f, true);
            sel.setOnAction(event -> setObstaculos(1));
            
            // poner los selectores de densidad de duracion de partida
            sel = setButton("assets/interfaz/left",
                    ww * 0.7f, hh * 0.6f, 72f, false);
            sel.setOnAction(event -> setDuracion(-1));
            sel = setButton("assets/interfaz/right",
                    ww * 0.88f, hh * 0.6f, 72f, true);
            sel.setOnAction(event -> setDuracion(1));
            
            // cargar datos del usuario previo
            leerDatos();
            
            // agregar sus datos automaticamente
            autoAsignar();
        }
        
        // algunas cosas funcionan diferente en el cliente
        else {
            
            // poner los checkbox desactivados para el cliente
            for (int i = 0; i < 18; i++) {
                activaNPCs.get(i).setDisable(true);
            }
            chkConex.setDisable(true);
        }
    }
    
    public void reDibujar() {
        for (int i = 0; i < 18; i++) {
            if (Adsobalin.userIsNPC(i)) {
                if (!activaNPCs.get(i).isSelected()) {
                    nombres.get(i).setText("****");
                }
                else if (i < 9) {
                    nombres.get(i).setText("*A" + (i + 1) + "*");
                }
                else {
                    nombres.get(i).setText("*B" + (i - 8) + "*");
                }
            }
            else if (Adsobalin.userSelf(i)) {
                nombres.get(i).setText("(" + Adsobalin.userName[i] + ")");
            }
            else {
                nombres.get(i).setText(Adsobalin.userName[i]);
            }
        }
    }
    
    private void autoAsignar() {
        int ind = Adsobalin.userAdd("127.0.0.1", Adsobalin.nombre,
                Adsobalin.estilo, Adsobalin.grupo);
        Adsobalin.indice = ind;
        reDibujar();
    }
    
    public void ejecutar() {
        Adsobalin.isEncursable = chkConex.isSelected();
        boolean[] npcok = new boolean[18];
        for (int i = 0; i < 18; i++) {
            npcok[i] = activaNPCs.get(i).isSelected();
        }
        guardarDatos();
        raiz.setScene(new Mundo(raiz, npcok,
                Integer.parseInt(configMap.get(0).getText()),
                Integer.parseInt(configMap.get(1).getText()),
                Integer.parseInt(configMap.get(2).getText())));
    }
    
    private void cambioGrupo() {
        // buscar el grupo contrario
        int grupo = Adsobalin.GRU_AZUL;
        if (Adsobalin.grupo == Adsobalin.GRU_AZUL) {
            grupo = Adsobalin.GRU_ROJO;
        }
        // el servidor puede intentar hacer el cambio inmediatamente
        if (Adsobalin.isServer) {
            Adsobalin.userClean(Adsobalin.indice);
            Adsobalin.grupo = grupo;
            autoAsignar();
        }
        // el clinete simula que vuelve al menu (se desconecta) y envia un
        // nuevo hola, con su grupo invertido, para que el servidor lo reasigne
        // notar que, el servidor eliminara su viejo registro si hay nuevo
        else {
            Envios.sendHola(Adsobalin.nombre, Conector.myServer,
                    Adsobalin.estilo, grupo);
            guardarDatos();
            raiz.setScene(new Menu(raiz));
        }
    }
    
    private void setTalla(int direccion) {
        setConfigDir(configMap.get(0), direccion, 1, 3);
    }
    
    private void setObstaculos(int direccion) {
        setConfigDir(configMap.get(1), direccion, 0, DENSI_OBST_MAX);
    }
    
    private void setDuracion(int direccion) {
        setConfigDir(configMap.get(2), direccion, 1, 9);
    }
    
    private void setConfigDir(Label txt, int direccion,
            int limiteInf, int limiteSup) {
        int val = Integer.parseInt(txt.getText());
        val = Math.min(limiteSup, Math.max(limiteInf, val + direccion));
        txt.setText(String.valueOf(val));
    }
    
    private void leerDatos() {
        if (data.cargarData(Adsobalin.DATAPATH)) {
            if (data.getData("lobini", "0").equals("1")) {
                configMap.get(0).setText(
                    data.getData("talla", configMap.get(0).getText()));
                configMap.get(1).setText(
                    data.getData("obstaculos", configMap.get(1).getText()));
                configMap.get(2).setText(
                    data.getData("tiempo", configMap.get(2).getText()));
                chkConex.setSelected(Boolean.parseBoolean(
                    data.getData("encursable",
                            String.valueOf(chkConex.isSelected()))));
                int flag = Integer.parseInt(data.getData("NPCs", "0"));
                int subi;
                for (int i = 0; i < 18; i++) {
                    subi = flag & (1 << i);
                    activaNPCs.get(i).setSelected(subi != 0);
                }
            }
        }
    }
    
    private void guardarDatos() {
        data.setData("lobini", "1");
        data.setData("talla", configMap.get(0).getText());
        data.setData("obstaculos", configMap.get(1).getText());
        data.setData("tiempo", configMap.get(2).getText());
        data.setData("encursable", String.valueOf(chkConex.isSelected()));
        int flag = 0;
        for (int i = 0; i < 18; i++) {
            if (activaNPCs.get(i).isSelected()) {
                flag |= 1 << i;
            }
        }
        data.setData("NPCs", String.valueOf(flag));
        data.guardarData(Adsobalin.DATAPATH);
    }
}
