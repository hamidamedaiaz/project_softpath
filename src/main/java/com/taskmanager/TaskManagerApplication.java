package com.taskmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaskManagerApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = fxmlLoader.load();

            Scene mainScene = new Scene(root, 1200, 800);

            String cssPath = getClass().getResource("/css/styles.css").toExternalForm();
            mainScene.getStylesheets().add(cssPath);

            primaryStage.setTitle("Gestionnaire de Tâches");
            primaryStage.setScene(mainScene);
            primaryStage.setMinWidth(800);

            primaryStage.setMinHeight(600);

            primaryStage.show();

        } catch (Exception e) {



            System.err.println("Erreur lors du chargement de l'interface: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}