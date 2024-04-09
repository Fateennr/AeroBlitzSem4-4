package com.example.aeroblitz;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Score {

    private static int GAME_WIDTH;
    private static int GAME_HEIGHT;
    int player1;
    int player2;

    public Score(int GAME_WIDTH, int GAME_HEIGHT) {
        Score.GAME_WIDTH = GAME_WIDTH;
        Score.GAME_HEIGHT = GAME_HEIGHT;
    }

    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setFont(Font.font("Consolas", 60));
        gc.strokeLine(GAME_WIDTH / 2, 0, GAME_WIDTH / 2, GAME_HEIGHT);
        gc.strokeText(String.valueOf(player1 / 10) + String.valueOf(player1 % 10), (GAME_WIDTH / 2) - 85, 50);
        gc.strokeText((player2 / 10) + String.valueOf(player2 % 10), (GAME_WIDTH / 2) + 20, 50);
    }

    public void setPlayerScores(int player1Score, int player2Score) {
        this.player1 = player1Score;
        this.player2 = player2Score;
    }
}
