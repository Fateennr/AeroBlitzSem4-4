package com.example.aeroblitz;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Game {

    private Rectangle leftPaddle;
    private double leftPaddleY = 260;

    private Rectangle rightPaddle;
    private double rightPaddleY = 260;

    private double leftPaddleDY;
    private double rightPaddleDY;
    private AnimationTimer timer = new AnimationTimer() {

        @Override
        public void handle(long now) {
            // update paddle positions
            leftPaddleY += leftPaddleDY;
            rightPaddleY += rightPaddleDY;
            if (leftPaddleY < 0) {
                leftPaddleY = 0;
            }
            if (rightPaddleY < 0) {
                rightPaddleY = 0;
            }

            leftPaddle.setY(leftPaddleY);
            rightPaddle.setY(rightPaddleY);
        }

    };

    public void createGame(Group gameDisplay) {

        //creates background
        Rectangle background = new Rectangle(0, 0, 800, 600);
        background.getStyleClass().add("background");

        //draws field lines
        Canvas game = new Canvas(800, 600);
        GraphicsContext gc = game.getGraphicsContext2D();
        gc.setStroke(Paint.valueOf("WHITE"));
        gc.setLineWidth(5);
        gc.strokeLine(400, 0, 400, 600);
        gc.strokeOval(300, 200, 200, 200);
        gc.strokeRect(0, 150, 100, 300);
        gc.strokeRect(700, 150, 100, 300);
        gc.setStroke(Paint.valueOf("BLACK"));
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, 800, 600);

        //creates red paddle
        leftPaddle = new Rectangle(30, leftPaddleY, 20, 70);
        leftPaddle.setFill(Color.RED);

        //creates blue paddle
        rightPaddle = new Rectangle(750, rightPaddleY, 20, 70);
        rightPaddle.setFill(Color.BLUE);

        // register event handlers to Canvas
        game.setFocusTraversable(true);
        game.setOnKeyPressed(keyPressed);
        game.setOnKeyReleased(keyReleased);

//        gameDisplay.getStylesheets().add(getClass().getResource("GameDisplay.css").toExternalForm());
        gameDisplay.getChildren().addAll(background, game, leftPaddle, rightPaddle);
        // start updates of paddle positions
        timer.start();
    }

    private EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent event) {
            // set movement to 0, if the released key was responsible for the paddle
            switch (event.getCode()) {
                case W:
                case S:
                    leftPaddleDY = 0;
                    break;
                case UP:
                case DOWN:
                    rightPaddleDY = 0;
                    break;
            }
        }

    };

    private EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent event) {
            // start movement according to key pressed
            switch (event.getCode()) {
                case W:
                    leftPaddleDY = -6;
                    break;
                case S:
                    leftPaddleDY = 6;
                    break;
                case UP:
                    rightPaddleDY = -6;
                    break;
                case DOWN:
                    rightPaddleDY = 6;
                    break;
            }

        }
    };
}