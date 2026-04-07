package se.gritacademy.tetris;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TetrisApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/main-view.fxml"));

        Parent root = fxmlLoader.load();

        TetrisController controller = fxmlLoader.getController();

        Scene scene = new Scene(root, 400, 600);

        controller.initInput(scene);

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
