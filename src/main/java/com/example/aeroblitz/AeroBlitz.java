package com.example.aeroblitz;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AeroBlitz extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Load the FXML file
        //Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("startscene.fxml"));

        try {
            root.getStylesheets().add(getClass().getResource("/buttons.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("Error loading CSS file: " + e.getMessage());
            e.printStackTrace();
        }


        // Set up the primary stage
        primaryStage.setTitle("AeroBlitz"); // Set the title of the window
        primaryStage.setScene(new Scene(root, 800, 600)); // Set the scene with  a specific width and height
        primaryStage.show(); // Display the window
    }


    public static void main(String[] args) {
        launch(args);
    }
}
