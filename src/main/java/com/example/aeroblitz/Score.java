//package com.example.aeroblitz;
//
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//
//public class Score {
//
//    private static int GAME_WIDTH;
//    private static int GAME_HEIGHT;
//    int player1;
//    int player2;
//
//    public Score(int GAME_WIDTH, int GAME_HEIGHT) {
//        Score.GAME_WIDTH = GAME_WIDTH;
//        Score.GAME_HEIGHT = GAME_HEIGHT;
//    }
//
//    public void draw(GraphicsContext gc) {
//        gc.setFill(Color.RED);
//        gc.setStroke(Color.DIMGREY);
//        gc.setFont(Font.font("Consolas", 60));
//        gc.strokeLine(GAME_WIDTH / 2, 0, GAME_WIDTH / 2, GAME_HEIGHT);
//        gc.strokeText(String.valueOf(player1 / 10) + String.valueOf(player1 % 10), (GAME_WIDTH / 2) - 85, 50);
//        gc.strokeText((player2 / 10) + String.valueOf(player2 % 10), (GAME_WIDTH / 2) + 20, 50);
//    }
//
//    public void setPlayerScores(int player1Score, int player2Score) {
//        this.player1 = player1Score;
//        this.player2 = player2Score;
//    }
//}

package com.example.aeroblitz;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Score {

    private static int GAME_WIDTH;
    private static int GAME_HEIGHT;
    protected int player1;
    protected int player2;
    private ttimer timer; // Custom ttimer instance

    public Score(int GAME_WIDTH, int GAME_HEIGHT) {
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
        this.timer = new ttimer(30, this); // Initialize ttimer with 30 seconds
    }

    public void draw(GraphicsContext gc) {
        // Draw player 1 score on the left side
        gc.setFill(Color.RED);
        gc.setStroke(Color.DIMGREY);
        gc.setFont(Font.font("Consolas", 60));
        gc.strokeText(String.valueOf(player1 / 10) + String.valueOf(player1 % 10), 50, 85);

        // Draw player 2 score on the right side
        gc.strokeText((player2 / 10) + String.valueOf(player2 % 10), GAME_WIDTH - 120, 80);

        // Draw the timer
        drawTimer(gc, timer.getTimeSeconds());
        startTimer(gc);

    }

    // Method to draw the timer
    private void drawTimer(GraphicsContext gc, int timeSeconds) {
        // Draw a background for the timer with a darker maroon color
        gc.setFill(Color.rgb(0, 0, 0)); // Darker maroon color
        gc.fillRect((GAME_WIDTH / 2) - 50, 550, 100, 85);

        // Set the color and font for the timer text
        gc.setFill(Color.rgb(50, 160, 168, 1)); // Bright white color with some opacity
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 60)); // Make the timer text bold

        // Draw the remaining time from the ttimer instance
        gc.strokeText(String.format("%02d", timeSeconds), (GAME_WIDTH / 2) - 30, 600);
    }

    // Method to set player scores
    public void setPlayerScores(int player1Score, int player2Score) {
        this.player1 = player1Score;
        this.player2 = player2Score;
    }

    // Method to start the timer
    public void startTimer(GraphicsContext gc) {
        timer.startCountdown(gc);
    }

    // Method to pause the timer
    public void pauseTimer() {
        timer.pauseCountdown();
    }

    // Method to update the timer
    public void updateTimer(GraphicsContext gc, int timeSeconds) {
        drawTimer(gc, timeSeconds);
    }

    public int getTime()
    { return timer.getTimeSeconds(); }

    public void setTime(int time )
    { timer.setTimeSeconds(time); }


}

