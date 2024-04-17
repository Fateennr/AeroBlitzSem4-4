package com.example.aeroblitz;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create a Group to hold the game elements
        Group root = new Group();

        // Create an instance of the Game class
        Game game = new Game();

        // Call the createGame method to set up the game
        game.createGame(root);

        // Create the scene and set it on the stage
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("AeroBlitz Game");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}