package com.example.aeroblitz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HighScoreController
{

    @FXML
    private VBox scoresVBox;

    private Database_Connection db;

    @FXML
    public void initialize() {
        db = new Database_Connection();
        loadHighScores();
    }

    private void loadHighScores()
    {
        String query = "SELECT player_name, score FROM high_scores ORDER BY score DESC LIMIT 5";



        try (Connection conn = db.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next())
            {
                HBox box = new HBox();

                Label player_nameLabel = new Label();
                Label scoreLabel = new Label();

                String playerName = rs.getString("player_name");
                int score = rs.getInt("score");

//                Label scoreLabel = new Label(playerName + ": " + score);
                scoreLabel.setText(""+score);
                player_nameLabel.setText(playerName);

//                player_nameLabel.getStyleClass().add("label-style");
//                scoreLabel.getStyleClass().add("label-style");

                scoreLabel.setStyle("-fx-text-fill: #F6DCAC; -fx-font-family: 'Berlin Sans FB'; -fx-font-size: 18;-fx-border-color: #FFFFFF;\n" +
                        "    -fx-border-width: 1px;\n" +
                        "    -fx-border-style: solid;    -fx-background-color: #4F4D64;\n" +
                        "     -fx-border-radius: 5px;\n" +
                        "     -fx-padding: 10px;\n" +
                        "     -fx-background-radius: 5px;");

                player_nameLabel.setStyle("-fx-text-fill: #F6DCAC; -fx-font-family: 'Berlin Sans FB'; -fx-font-size: 18;-fx-border-color: #FFFFFF;\n" +
                        "    -fx-border-width: 1px;\n" +
                        "    -fx-border-style: solid;    -fx-background-color: #4F4D64;\n" +
                        "     -fx-border-radius: 5px;\n" +
                        "     -fx-padding: 10px;\n" +
                        "     -fx-background-radius: 5px;");


                // Ensure labels take up all available space within the HBox
                player_nameLabel.setMaxWidth(Double.MAX_VALUE);
                scoreLabel.setMaxWidth(Double.MAX_VALUE);

                // Center-align text in labels
                player_nameLabel.setAlignment(Pos.CENTER);
                scoreLabel.setAlignment(Pos.CENTER);

                // Ensure the HBox distributes space equally
                HBox.setHgrow(player_nameLabel, Priority.ALWAYS);
                HBox.setHgrow(scoreLabel, Priority.ALWAYS);

                // Set equal widths for labels
                player_nameLabel.setPrefWidth(200); // Set preferred width for each label
                scoreLabel.setPrefWidth(200);

                // Add labels to the HBox
                box.getChildren().addAll(player_nameLabel, scoreLabel);

                // Set the HBox to fill the width of the VBox
                box.setMaxWidth(Double.MAX_VALUE);
                box.setPrefWidth(400); // Adjust according to the VBox width
                HBox.setHgrow(box, Priority.ALWAYS);

                // Set alignment of HBox within VBox
                scoresVBox.setAlignment(Pos.CENTER_LEFT); // Align HBox to the left within VBox
//                VBox.setMargin(box, new Insets(5)); // Add margin for spacing

                // Add the HBox to the VBox
                scoresVBox.getChildren().add(box);


                // Debugging: print to verify
                System.out.println("Added: " + playerName + " - " + score);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButtonClick()
    {
        System.out.println("Back button clicked");

        try {
            Parent root = FXMLLoader.load(getClass().getResource("startscene.fxml"));

            // Create a new stage
            Stage primaryStage = new Stage();
            primaryStage.setTitle("AeroBlitz");
            primaryStage.setScene(new Scene(root, 800, 600)); // Adjust the size as needed
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load startscene.fxml.");
        }
    }

}
