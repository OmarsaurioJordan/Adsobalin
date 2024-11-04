package logic.interfaz;
// interfaz de espera para comenzar y configurar la partida

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Lobby extends Scene {
    
    private Stage raiz;
    
    public Lobby(Stage raiz) {
        super(new StackPane(), 720, 480);
        this.raiz = raiz;
        
        Label label = new Label("Lobby");
        Button button = new Button("To Menu");
        
        StackPane layout = (StackPane)this.getRoot();
        layout.getChildren().addAll(label, button);
        
        button.setOnAction(e -> raiz.setScene(new Menu(raiz)));
    }
}
