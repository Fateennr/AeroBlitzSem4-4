package com.example.aeroblitz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutUsController {


    @FXML
    private AnchorPane anchorPane;

    @FXML
    void HandleHomePageClick(MouseEvent event)
    {

        try {

//            mediaPlayer.stop();
            Parent root = FXMLLoader.load(getClass().getResource("startscene.fxml"));

            // Set up the primary stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("AeroBlitz"); // Set the title of the window
            primaryStage.setScene(new Scene(root)); // Set the scene with a specific width and height
            primaryStage.show(); // Display the window

            Stage currentStage = (Stage) anchorPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load startscene.fxml.");
        }

    }

}
