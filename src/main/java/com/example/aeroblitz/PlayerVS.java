package com.example.aeroblitz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerVS
{

    @FXML
    void versus1Click(ActionEvent event)
    {

        // Add your logic for handling the "New Game" button click here
        System.out.println("New Game button clicked");
        // Load the FXML file
        try
        {

            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Set up the primary stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("AeroBlitz"); // Set the title of the window
            primaryStage.setScene(new Scene(root)); // Set the scene with a specific width and height
            primaryStage.show(); // Display the window
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load startscene.fxml.");
        }

    }

    @FXML
    void versus2Click(ActionEvent event)
    {

        // Add your logic for handling the "New Game" button click here
        System.out.println("New Game button clicked");
        // Load the FXML file
        try
        {

            Parent root = FXMLLoader.load(getClass().getResource("hello-view2.fxml"));

            // Set up the primary stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("AeroBlitz"); // Set the title of the window
            primaryStage.setScene(new Scene(root)); // Set the scene with a specific width and height
            primaryStage.show(); // Display the window
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load startscene.fxml.");
        }

    }

}