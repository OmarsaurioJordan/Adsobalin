package logic.interfaz;
// interfaz que muestra las estadisticas de final de partida

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Resultado extends GUIs {
    
    private String[] nombres = new String[18];
    private String[] puntos = new String[18];
    private String[] posiciones = new String[18];
    
    public Resultado(Stage raiz) {
        super(raiz);
        Adsobalin.estado = Adsobalin.EST_FINAL;
        
        // variables compactas para escritura eficiente
        float ww = (float)Adsobalin.WIDTH;
        float hh = (float)Adsobalin.HEIGHT;
        float esc = (float)Adsobalin.ESCALA;
        
        // pintar el fondo de la interfaz
        pintarFondo();
        
        // poner titulo de la interfaz
        Label titulo = setLabel("Resultados", ww * 0.35f, hh * 0.1f);
        fontSize(titulo, 28);
        
        // colocar las dos columnas con los nombres
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
            }
        }
        
        // poner el boton superior izquierdo para retornar al menu
        setLabel("volver", ww * 0.03f, hh * 0.15f);
        Button volver = setButton("assets/interfaz/left",
                ww * 0.05f, hh * 0.07f, 72f, false);
        volver.setOnAction(event -> {
            raiz.setScene(new Menu(raiz));
        });
        
        // las cosas que solo el servidor vera
        if (Adsobalin.isServer) {
            
            // colocar el gran boton de play abajo a la derecha
            Button play = setButton("assets/interfaz/play",
                    ww * 0.82f, hh * 0.8f, 138f, true);
            play.setOnAction(event -> ejecutar());
        }
    }
    
    private void ejecutar() {
        // regresara al lobby para iniciar otra partida
        
    }
}
