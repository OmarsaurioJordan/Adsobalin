package logic.interfaz;
// interfaz de espera para comenzar y configurar la partida

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import logic.sincronia.Conector;

public class Lobby extends GUIs {
    
    // guarda los nombres de usuarios conectados
    ArrayList<Label> nombres = new ArrayList<>();
    
    public Lobby(Stage raiz, boolean isServer) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_LOBBY;
        Adsobalin.isServer = isServer;
        
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
            }
        }
        
        // poner el boton superior izquierdo para retornar al menu
        Button volver = setButton("assets/interfaz/left",
                ww * 0.05f, hh * 0.07f, 72f, false);
        volver.setOnAction(event -> volverAlMenu());
        
        // las cosas que solo el servidor vera
        if (isServer) {
            
            // agregar sus datos automaticamente
            Adsobalin.userAdd("127.0.0.1", Adsobalin.nombre,
                    Adsobalin.estilo, Adsobalin.grupo);
            reDibujar();
            
            // colocar el gran boton de play abajo a la derecha
            Button play = setButton("assets/interfaz/play",
                    ww * 0.82f, hh * 0.8f, 138f, true);
            play.setOnAction(event -> ejecutar());
        }
    }
    
    public void reDibujar() {
        for (int i = 0; i < 18; i++) {
            if (Conector.userName[i].isEmpty()) {
                if (i < 9) {
                    nombres.get(i).setText("*A" + (i + 1) + "*");
                }
                else {
                    nombres.get(i).setText("*B" + (i - 8) + "*");
                }
            }
            else if (Conector.userName[i].equals(Adsobalin.nombre)) {
                nombres.get(i).setText("(" + Conector.userName[i] + ")");
            }
            else {
                nombres.get(i).setText(Conector.userName[i]);
            }
        }
    }
    
    public void ejecutar() {
        // poner en marcha el juego, solo el servidor puede hacerlo
    }
}

/*
- poner checkbox de agregar luego
- poner 3 reguladores de creacion de mundo
- poner boton de regresar al menu principal
- poner boton de cambio de grupo
*/
