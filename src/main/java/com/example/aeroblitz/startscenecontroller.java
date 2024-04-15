package com.example.aeroblitz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.layout.AnchorPane;


public class startscenecontroller {

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane; // Assuming you have AnchorPane in your FXML

    public void initialize() {
        // Load the image
        Image image = new Image(getClass().getResourceAsStream("/hock.png"));

        // Set the loaded image to the ImageView
        if (image != null) {
            imageView.setImage(image);
        } else {
            System.err.println("Failed to load image.");
        }
    }

    @FXML
    private void handleNewGameButtonClick() {
        // Add your logic for handling the "New Game" button click here
        System.out.println("New Game button clicked");
        // Load the FXML file
        try {
            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));

            // Set up the primary stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("AeroBlitz"); // Set the title of the window
            primaryStage.setScene(new Scene(root, 800, 600)); // Set the scene with a specific width and height
            primaryStage.show(); // Display the window
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load startscene.fxml.");
        }
    }

    @FXML
    private void handleQuitButtonClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quit Game");
        alert.setHeaderText("Are you sure you want to quit the game?");
        alert.setContentText("Press OK to quit, or Cancel to continue playing.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Close the window and exit the application
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.close();
        } else {
            // User clicked Cancel, do nothing
        }
    }
}
