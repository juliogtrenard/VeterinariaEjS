package es.juliogtrenard.veterinariaejs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VeterinariaApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VeterinariaApplication.class.getResource("/es/juliogtrenard/veterinariaejs/fxml/veterinaria.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Veterinaria Gasteiz");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}