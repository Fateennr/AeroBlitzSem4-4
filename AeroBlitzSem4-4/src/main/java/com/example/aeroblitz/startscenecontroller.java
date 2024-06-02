package com.example.aeroblitz;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;


public class startscenecontroller {

    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane anchorPane; // Assuming you have AnchorPane in your FXML

    @FXML
    private Button turnOnSoundButton;

    @FXML
    private Button turnOffSoundButton;

    private MediaPlayer mediaPlayer;

    private Boolean soundon;

    @FXML
    private Label titleLabel;

    private Media sound;
    public void initialize() {

        // Create a timeline to animate the glow effect
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.effectProperty(), titleLabel.getEffect())),
                new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.effectProperty(), null))
        );
        timeline.setAutoReverse(true); // Add a flicker effect by reversing the animation
        timeline.setCycleCount(Animation.INDEFINITE); // Repeat the animation indefinitely
        timeline.play();

        // Load the image
        Image image = new Image(getClass().getResourceAsStream("/hock.png"));

        // Set the loaded image to the ImageView
        if (image != null) {
            imageView.setImage(image);
        } else {
            System.err.println("Failed to load image.");
        }


        soundon = true;
        // Load and play the sound
        sound = new Media(getClass().getResource("/intro.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setOnEndOfMedia(() -> {
            // When media ends, restart it
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });
        mediaPlayer.play();

        // Initially, hide the turn on sound button
        turnOffSoundButton.setVisible(true);
    }



    @FXML
    private void handleTurnOffSoundButtonClick() {

        if(soundon) {
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.stop();
//                turnOnSoundButton.setVisible(true); // Show the turn on sound button
//                turnOffSoundButton.setVisible(false); // Hide the turn off sound button

                soundon = false;
                anchorPane.lookup("#circular-button").setStyle("-fx-background-color: #ff0000"); // Set red color
            }
        }
        else
        {
            if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                mediaPlayer.play();
//                turnOnSoundButton.setVisible(false); // Hide the turn on sound button
//                turnOffSoundButton.setVisible(true); // Show the turn off sound button
                soundon = true;
                anchorPane.lookup("#circular-button").setStyle("-fx-background-color: #00ff00"); // Set green color
            }

        }
    }
    @FXML
    private void handleShowHighScoreButtonClick() {
        System.out.println("Show High Scores button clicked");


        mediaPlayer.stop();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("highscoreshow.fxml"));
            Stage primaryStage = new Stage();
            primaryStage.setTitle("High Scores");
            primaryStage.setScene(new Scene(root, 600, 400)); // Adjust the size as needed
            primaryStage.show();

            Stage currentStage = (Stage) anchorPane.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load highscoreshow.fxml.");
        }
    }
    @FXML
    private void handleTurnOnSoundButtonClick() {
        if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            mediaPlayer.play();
            turnOnSoundButton.setVisible(false); // Hide the turn on sound button
            turnOffSoundButton.setVisible(true); // Show the turn off sound button
        }
    }

    @FXML
    private void handleNewGameButtonClick()
    {
        // Add your logic for handling the "New Game" button click here
        System.out.println("New Game button clicked");
        // Load the FXML file
        try {

            mediaPlayer.stop();
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
    private void handleAboutUsButtonClick() {
        try {
            // Load the About_Us.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("About_Us.fxml"));
            Parent aboutUsRoot = loader.load();

            // Get the current stage
            Stage stage = (Stage) anchorPane.getScene().getWindow();

            // Create a new scene with the loaded FXML file
            Scene scene = new Scene(aboutUsRoot, 800, 600); // Set the width to 800 and height to 600

            // Set the new scene to the stage
            stage.setScene(scene);

            // Optionally, set the title for the About Us window
            stage.setTitle("About Us");

            // Show the new scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, show an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load About Us page");
            alert.setContentText("An error occurred while trying to load the About Us page.");
            alert.showAndWait();
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








//package com.example.aeroblitz;
//
//import javafx.animation.Animation;
//import javafx.animation.KeyFrame;
//import javafx.animation.KeyValue;
//import javafx.animation.Timeline;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.Label;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.stage.Stage;
//
//import java.io.IOException;
//import java.util.Optional;
//import javafx.scene.layout.AnchorPane;
//import javafx.util.Duration;
//
//
//public class startscenecontroller {
//
//    @FXML
//    private ImageView imageView;
//
//    @FXML
//    private AnchorPane anchorPane; // Assuming you have AnchorPane in your FXML
//
//    @FXML
//    private Button turnOnSoundButton;
//
//    @FXML
//    private Button turnOffSoundButton;
//
//    private MediaPlayer mediaPlayer;
//
//    private Boolean soundon;
//
//    @FXML
//    private Label titleLabel;
//
//    private Media sound;
//    public void initialize() {
//
//        // Create a timeline to animate the glow effect
//        Timeline timeline = new Timeline(
//                new KeyFrame(Duration.ZERO, new KeyValue(titleLabel.effectProperty(), titleLabel.getEffect())),
//                new KeyFrame(Duration.seconds(1), new KeyValue(titleLabel.effectProperty(), null))
//        );
//        timeline.setAutoReverse(true); // Add a flicker effect by reversing the animation
//        timeline.setCycleCount(Animation.INDEFINITE); // Repeat the animation indefinitely
//        timeline.play();
//
//        // Load the image
//        Image image = new Image(getClass().getResourceAsStream("/hock.png"));
//
//        // Set the loaded image to the ImageView
//        if (image != null) {
//            imageView.setImage(image);
//        } else {
//            System.err.println("Failed to load image.");
//        }
//
//
//        soundon = true;
//        // Load and play the sound
//        sound = new Media(getClass().getResource("/intro.mp3").toExternalForm());
//        mediaPlayer = new MediaPlayer(sound);
//        mediaPlayer.setOnEndOfMedia(() -> {
//            // When media ends, restart it
//            mediaPlayer.seek(Duration.ZERO);
//            mediaPlayer.play();
//        });
//        mediaPlayer.play();
//
//        // Initially, hide the turn on sound button
//        turnOffSoundButton.setVisible(true);
//    }
//
//    @FXML
//    private void handleTurnOffSoundButtonClick() {
//
//        if(soundon) {
//            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
//                mediaPlayer.stop();
////                turnOnSoundButton.setVisible(true); // Show the turn on sound button
////                turnOffSoundButton.setVisible(false); // Hide the turn off sound button
//
//                soundon = false;
//                anchorPane.lookup("#circular-button").setStyle("-fx-background-color: #ff0000"); // Set red color
//            }
//        }
//        else
//        {
//            if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
//                mediaPlayer.play();
////                turnOnSoundButton.setVisible(false); // Hide the turn on sound button
////                turnOffSoundButton.setVisible(true); // Show the turn off sound button
//                soundon = true;
//                anchorPane.lookup("#circular-button").setStyle("-fx-background-color: #00ff00"); // Set green color
//            }
//
//        }
//    }
//
//    @FXML
//    private void handleTurnOnSoundButtonClick() {
//        if (mediaPlayer != null && mediaPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
//            mediaPlayer.play();
//            turnOnSoundButton.setVisible(false); // Hide the turn on sound button
//            turnOffSoundButton.setVisible(true); // Show the turn off sound button
//        }
//    }
//
//    @FXML
//    private void handleNewGameButtonClick() {
//        // Add your logic for handling the "New Game" button click here
//        System.out.println("New Game button clicked");
//        // Load the FXML file
//        try {
//
//            mediaPlayer.stop();
//            Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
//
//            // Set up the primary stage
//            Stage primaryStage = new Stage();
//            primaryStage.setTitle("AeroBlitz"); // Set the title of the window
//            primaryStage.setScene(new Scene(root)); // Set the scene with a specific width and height
//            primaryStage.show(); // Display the window
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Failed to load startscene.fxml.");
//        }
//    }
//
//    @FXML
//    private void handleQuitButtonClick() {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Quit Game");
//        alert.setHeaderText("Are you sure you want to quit the game?");
//        alert.setContentText("Press OK to quit, or Cancel to continue playing.");
//
//        Optional<ButtonType> result = alert.showAndWait();
//        if (result.isPresent() && result.get() == ButtonType.OK) {
//            // Close the window and exit the application
//            Stage stage = (Stage) anchorPane.getScene().getWindow();
//            stage.close();
//        } else {
//            // User clicked Cancel, do nothing
//        }
//    }
//}
