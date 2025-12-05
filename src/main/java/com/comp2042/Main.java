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
    /** Width of the main menu window in pixels */
    private static final int MENU_WIDTH = 500;
    
    /** Height of the main menu window in pixels */
    private static final int MENU_HEIGHT = 510;
    
    // Application title
    /** Title displayed in the application window */
    private static final String APP_TITLE = "TetrisJFX";

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main menu
        URL location = getClass().getClassLoader().getResource("mainMenuLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        com.comp2042.view.MainMenuController menuController = fxmlLoader.getController();
        menuController.setPrimaryStage(primaryStage);

        primaryStage.setTitle(APP_TITLE);
        Scene scene = new Scene(root, MENU_WIDTH, MENU_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
