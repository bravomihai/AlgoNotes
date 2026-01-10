package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AppBootstrap.init();

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/ui/main.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("AlgoNotes");

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();

        stage.setMinWidth(screen.getWidth() * 0.85);
        stage.setMinHeight(screen.getHeight() * 0.6);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
