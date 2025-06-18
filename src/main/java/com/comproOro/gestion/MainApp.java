package com.comproOro.gestion;


import com.comproOro.gestion.controller.Inicio;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showMainLayout();
    }

    public void showMainLayout() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inicio.fxml"));
        Parent root = loader.load();
        Inicio controller = loader.getController();
        controller.setMainApp(this);

        Scene scene = new Scene(root);
        primaryStage.setTitle("Main Application");
        primaryStage.setScene(scene);
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.setMaximized(true);
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }



}