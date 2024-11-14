package es.juliogtrenard.veterinariaejs.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VeterinariaController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}