package com.comp2042;

import com.comp2042.controller.GameController;
import com.comp2042.view.GuiController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    // Window dimensions
    /** Width of the application window in pixels */
    private static final int WINDOW_WIDTH = 300;
    
    /** Height of the application window in pixels */
    private static final int WINDOW_HEIGHT = 510;
    
    // Application title
    /** Title displayed in the application window */
    private static final String APP_TITLE = "TetrisJFX";

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle(APP_TITLE);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
        new GameController(c);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
