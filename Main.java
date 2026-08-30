package hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hangman/views/game.fxml"));
        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(getClass().getResource("/hangman/views/style.css").toExternalForm());

        // Allow keyboard input on the scene
        GameController controller = loader.getController();
        scene.setOnKeyPressed(event -> {
            String key = event.getText().toUpperCase();
            if (key.length() == 1 && key.charAt(0) >= 'A' && key.charAt(0) <= 'Z') {
                controller.handleKeyPress(key.charAt(0));
            }
        });

        primaryStage.setTitle("Hangman");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
