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
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Resultado extends GUIs {
    
    // las estructuras para mostrar los datos
    private Label[] nombres = new Label[18];
    private Label[] puntos = new Label[18];
    private Label[] punGrupos = new Label[2];
    // toda la información empaquetada para envio a clientes
    private String allData = "";
    
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
        int n = 0;
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 9; y++) {
                // espacio para nombres
                nombres[n] = setLabel("*" + letra[x] + (y + 1) + "*",
                        ww * 0.25f - 100f * esc + x * (ww * 0.3f),
                        hh * 0.28f + y * (32f * esc));
                nombres[n].setPrefWidth(90f * Adsobalin.ESCALA);
                nombres[n].setAlignment(Pos.CENTER);
                nombres[n].setBackground(bck[x]);
                // espacio para puntajes
                puntos[n] = setLabel("0",
                        ww * 0.25f + x * (ww * 0.3f),
                        hh * 0.28f + y * (32f * esc));
                puntos[n].setPrefWidth(90f * Adsobalin.ESCALA);
                puntos[n].setAlignment(Pos.CENTER);
                puntos[n].setBackground(bck[x]);
                n++;
            }
        }
        
        // colocar los dos espacios para puntos de grupos
        for (int y = 0; y < 2; y++) {
            punGrupos[y] = setLabel("0", ww * 0.73f,
                    hh * 0.32f + y * hh * 0.15f);
            punGrupos[y].setPrefWidth(160f * Adsobalin.ESCALA);
            punGrupos[y].setAlignment(Pos.CENTER);
            punGrupos[y].setFont(Adsobalin.letrotas);
            punGrupos[y].setBackground(bck[y]);
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
            
            // ordenar los datos de jugadores azules
            List<Map.Entry<String, Integer>> lis = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                lis.add(getMovilName(i));
            }
            lis.sort((a, b) -> b.getValue() - a.getValue());
            for (int i = 0; i < 9; i++) {
                nombres[i].setText(lis.get(i).getKey());
                puntos[i].setText("" + lis.get(i).getValue());
            }
            // rojos
            lis = new ArrayList<>();
            for (int i = 9; i < 18; i++) {
                lis.add(getMovilName(i));
            }
            lis.sort((a, b) -> b.getValue() - a.getValue());
            for (int i = 9; i < 18; i++) {
                nombres[i].setText(lis.get(i - 9).getKey());
                puntos[i].setText("" + lis.get(i - 9).getValue());
            }
            
            // cargar datos de grupos
            for (int i = 0; i < 2; i++) {
                punGrupos[i].setText("" + Adsobalin.gruPoints[i]);
            }
            
            // guardar todos los datos para posterior envio
            allData = punGrupos[0].getText() + "|" + punGrupos[1].getText();
            for (int i = 0; i < 18; i++) {
                allData += "|" + nombres[i].getText() +
                        "|" + puntos[i].getText();
            }
        }
    }
    
    private Map.Entry<String, Integer> getMovilName(int indMovil) {
        if (Adsobalin.userIsNPC(indMovil)) {
            if (Mundo.isNPCinGame(indMovil)) {
                return Map.entry(nombres[indMovil].getText(),
                        Adsobalin.userPoints[indMovil]);
            }
            else {
                return Map.entry("****",
                        Adsobalin.userPoints[indMovil]);
            }
        }
        else if (Adsobalin.userSelf(indMovil)) {
            return Map.entry("(" + Adsobalin.userName[indMovil] + ")",
                    Adsobalin.userPoints[indMovil]);
        }
        else {
            return Map.entry(Adsobalin.userName[indMovil],
                    Adsobalin.userPoints[indMovil]);
        }
    }
    
    private void ejecutar() {
        // regresara al lobby para iniciar otra partida
        
    }
}
