package com.example.aeroblitz;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.*;

// defines the state and components of the board and the game

public class Board {


    @FXML
    private AnchorPane pane;

    private final int GAME_WIDTH = 800; //adjust the size of the board
    private final int GAME_HEIGHT = 600;
    private final int BALL_DIAMETER = 20; //adjust the size of the ball

    private final Set<KeyCode> pressedKeys = new HashSet<>(); // for keyboard movements in striker2

    private  double STRIKER_SPEED = 2; // speed of the striker2
    private double goalpos = 230;

    private Striker striker1;
    private Striker striker2;
    private Ball ball;
    private int strikerSize = 100; // adjust the size of the striker, indicates the diameter
    private double strokeWidth = 0;

    private int MAX_BOUNCE_SPEED = 2; // determines the speed of the ball when the striker hits it


    private double startX; // initial position of the mouse when it is pressed
    private double startY;
    private double startTime; // initial time of the mouse when it is pressed

    private Canvas canvas;
    private GraphicsContext gc, gc1;
    private Score score;

    private ttimer __timer;

//    private final ScaleTransition blastTransition = new ScaleTransition(Duration.seconds(0.3));

    private double blastoffset = 5;


    //for motiontrail on the ball

    private final List<Line> motionTrail = new ArrayList<>();
    private final int maxTrailLength = 20; // Adjust the maximum length of the motion trail as needed


    // score label
    @FXML
    private Label winText;

    private boolean gameEnded = false;



    private final AnimationTimer gameLoop = new AnimationTimer() { //game loop for running updates in the board
        @Override
        public void handle(long now) {

            drawTrail();

            // Update the position of the ball
            ball.move();
            ball.draw();

            // Update motion trail for the ball
            updateMotionTrail(ball);

            // Check for collisions
            checkCollision();

            System.out.println("time "+score.getTime());

            if(score.getTime() == 0)
            {
                gameEnded = true;
                gameLoop.stop();
                score.pauseTimer();

                if(score.player1 > score.player2) {
                    winText.setText(String.format("Player 1 wins!!"));
                    winText.setVisible(true); // Hide the win text
                }
                else if(score.player2 > score.player1) {
                    winText.setText(String.format("Player 2 wins!!"));
                    winText.setVisible(true); // Hide the win text
                }
                else
                {
                    winText.setText(String.format("Its a Draw!!"));
                    winText.setVisible(true); // Hide the win text
                }

//                score.setTime(30);
//                resetPositions();
//                draw_scoreboard();
                System.out.println(score.player1);
                System.out.println(score.player2);

            }



        }
    };

    @FXML
    public void initialize() {

        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT); // Adjust the size as needed

        // Initialize blast effect transition
//        blastTransition.setToX(0);
//        blastTransition.setToY(0);


        gc = canvas.getGraphicsContext2D();
        gc1 = canvas.getGraphicsContext2D();


        pane.getChildren().add(canvas);

//        __timer = new ttimer(30, this); // Initialize ttimer with 30 seconds


        score = new Score(GAME_WIDTH, GAME_HEIGHT); // Pass the dimensions of your game window
        score.setPlayerScores(0, 0); // Set initial scores

        draw_scoreboard();

        // Initialize the strikers
        striker1 = new Striker(); // Initialize with appropriate values
        striker2 = new Striker(); // Initialize with appropriate values


        // Initialize the board
        newStriker(100, 300, Color.RED, 1, striker1); // initial position of the strikers in the board
        newStriker(700, 300, Color.GREEN,2, striker2);
        newBall(); // initial position of the ball, it will start from any random position in the middle line of the board

        // Start the game loop
        gameLoop.start();
    }


    //motion trail is turned off for now

    private void drawTrail() {
//        gc1.setStroke(Color.rgb(255, 255, 255, 0.5)); // Set the color and transparency of the motion trail
//        gc1.setLineWidth(2); // Set the width of the motion trail lines
//
//        for (Line segment : motionTrail) {
//            gc1.strokeLine(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
//        }
    }

    private void updateMotionTrail(Ball ball) {

//        double trailStartX = ball.getX() + ball.getradius(); // Calculate the x-coordinate of the trail start
//        double trailStartY = ball.getY() + ball.getradius(); // Calculate the y-coordinate of the trail start
//
//        // Calculate the end point of the trail segment
//        double trailEndX = ball.getX() - ball.getXVelocity() + ball.getradius();
//        double trailEndY = ball.getY() - ball.getYVelocity() + ball.getradius();
//
//        Line trailSegment = new Line(trailStartX, trailStartY, trailEndX, trailEndY);
//        trailSegment.setStroke(Color.CRIMSON);
//
//        // Set initial opacity of the trail segment
//        trailSegment.setOpacity(1.0);
//
//        // Fade away the trail segment over time
//        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), trailSegment);
//        fadeOut.setToValue(0.0); // Fade to fully transparent
//        // Set the action to be performed when the fade-out animation completes
//        fadeOut.setOnFinished(event -> {
//            // Remove the trail segment from the scene after fading out completely
//            pane.getChildren().remove(trailSegment);
//            // Remove the trail segment from the motion trail list
//            motionTrail.remove(trailSegment);
//        });
//
//        fadeOut.play();
//
//        motionTrail.add(trailSegment);
//
//        // Remove oldest segment if the trail is too long
//        if (motionTrail.size() > maxTrailLength) {
//            // Remove the oldest trail segment from the scene
//            pane.getChildren().remove(motionTrail.remove(0));
//        }
    }

    private void draw_scoreboard() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        score.draw(gc); // Draw the score
    }

    public void newBall() {

        Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
        c.setFill(Color.YELLOW);
        c.setStroke(Color.WHITE);

        Random random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2), random.nextInt((GAME_HEIGHT) - (BALL_DIAMETER)), BALL_DIAMETER, c);
        pane.getChildren().add(c);
        ball.draw();

    }

    public void newStriker(int x, int y, Color color, int id, Striker striker) {
        Circle c = new Circle();
        Color neonColor = color.brighter().brighter().brighter();
        c.setFill(Color.WHITE);
        c.setStroke(neonColor);


        // Set the stroke width
        strokeWidth = 15.0; // Change this value as needed
        c.setStrokeWidth(strokeWidth);

        double radius = strikerSize / 3.0;

        striker.setID(id);
        striker.setX(x);
        striker.setY(y);
        striker.setColor(color);
        striker.setRadius(radius);
        striker.setCircle(c); // Assign the Circle object to the Striker
        pane.getChildren().add(c);

        striker.draw(); //draw the striker

//        if(striker.getID() == 2) {
        if(true) {

            // Setting mouse control
            c.setOnMousePressed(event -> pressed(event, striker));
            c.setOnMouseDragged(event -> dragged(event, striker));
            c.setOnMouseReleased(event -> released(event, striker));
        }
        else
        {

//            // Setting keyboard control
//            pane.setOnKeyPressed(event -> keyPressed(event, striker));
//            pane.requestFocus(); // Ensure the pane has focus to receive key events

//            striker.setOnKeyPressed(event ->
//            {
//                if(event.getCode() == )
//            });


        }
    }

//    private void keyPressed(KeyEvent event, Striker striker) {
//        pressedKeys.add(event.getCode());
//        moveStriker(striker);
//    }
//
//    private void moveStriker(Striker striker)
//    {
//
//        double deltaX = 0;
//        double deltaY = 0;
//
//        if (pressedKeys.contains(KeyCode.UP))
//        {
//            System.out.println("Up provoked");
//            deltaY -= 1;
//        }
//        if (pressedKeys.contains(KeyCode.LEFT)) deltaX -= 1;
//        if (pressedKeys.contains(KeyCode.DOWN)) deltaY += 1;
//        if (pressedKeys.contains(KeyCode.RIGHT)) deltaX += 1;
//
////        // Normalize diagonal movement
////        if (deltaX != 0 && deltaY != 0)
////        {
////            double diagonalFactor = Math.sqrt(3.5);
////            deltaX *= diagonalFactor;
////            deltaY *= diagonalFactor;
////        }
//
//        double newX = striker.getX() + deltaX * STRIKER_SPEED;
//        double newY = striker.getY() + deltaY * STRIKER_SPEED;
//
////        // Boundary checking
////        newX = Math.max(Math.min(newX, GAME_WIDTH - strikerSize / 2), strikerSize / 2);
////        newY = Math.max(Math.min(newY, GAME_HEIGHT - strikerSize / 2), strikerSize / 2);
//
//        System.out.println("newX " + deltaX);
//        System.out.println("newY " + deltaY);
//        striker.setX(newX);
//        striker.setY(newY);
//        striker.draw();
//    }

//    private void moveStriker(Striker striker, double deltaX, double deltaY) {
//        int speed = 10;
//        double newX = striker.getX() + deltaX*speed;
//        double newY = striker.getY() + deltaY*speed;
//        // Update the position of the striker
//        striker.setX(newX);
//        striker.setY(newY);
//        // Redraw the striker at its new position
//        striker.draw();
//    }

    //Collision physics

    public void checkCollision() {


        int diameter = strikerSize/2;
        int ball_radius = BALL_DIAMETER/2;


        // left side Boundary checking for striker1
        if (striker1.getX() <= diameter) {
            striker1.setX(diameter);
            striker1.draw();
        }

        if (striker1.getX() >= (GAME_WIDTH - (diameter))) {
            striker1.setX(GAME_WIDTH - (diameter));
            striker1.draw();
        }

        if (striker1.getY() <= diameter) {
            striker1.setY(diameter);
            striker1.draw();
        }

        if (striker1.getY() >= (GAME_HEIGHT - (diameter))) {
            striker1.setY(GAME_HEIGHT - (diameter));
            striker1.draw();
        }


        // Boundary checking for striker2
        if (striker2.getX() <= diameter) {
            striker2.setX(diameter);
            striker2.draw();
        }

        if (striker2.getX() >= (GAME_WIDTH - (diameter))) {
            striker2.setX(GAME_WIDTH - (diameter));
            striker2.draw();
        }

        if (striker2.getY() <= diameter) {
            striker2.setY(diameter);
            striker2.draw();
        }

        if (striker2.getY() >= (GAME_HEIGHT - (diameter))) {
            striker2.setY(GAME_HEIGHT - (diameter));
            striker2.draw();
        }

        // Ball bounce off the top and bottom edges
        if (ball.getY() <= 0 || ball.getY() >= (GAME_HEIGHT - BALL_DIAMETER)) {
            ball.setYDirection(-ball.getYVelocity());
        }

        // Ball bounce off the left and right edges  & setting the goalpost
        if (ball.getX() <= 0 && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER)) {

            score.player2++;

//                if(score.player2 > score.player1)
//                if(score.getTime() == 0)
//                {
//                    gameEnded = true;
//                    gameLoop.stop();
//                    winText.setText(String.format("Player 1 wins!!"));
//                    winText.setVisible(true); // Hide the win text
//
//                    // I want to stop the game loop here and show that player 2 won
//                }

            resetPositions();
            draw_scoreboard();
            System.out.println(score.player1);

        }
        else if (ball.getX() >= (GAME_WIDTH - BALL_DIAMETER) && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER)) {

            score.player1++;

////            if(score.player1 == 2)
//            if(score.getTime() == 0)
//            {
//                gameEnded = true;
//                gameLoop.stop();
//                winText.setText(String.format("Player 2 wins!!"));
//                winText.setVisible(true); // Hide the win text
//
//                // I want to stop the game loop here and show that player 1 won
//            }

            resetPositions();
            draw_scoreboard();
            System.out.println(score.player2);

        }else if (ball.getX() <= 0 || ball.getX() >= (GAME_WIDTH - BALL_DIAMETER)) {
            ball.setXDirection(-ball.getXVelocity());
        }



        //collision between two strikers
        if(intersects(striker1 , striker2))
        {
            double fDistance = Math.sqrt(getDistance(striker1.getX(),striker1.getY(),striker2.getX(),striker2.getY()));

            double fOverlap = 0.5 * (fDistance-strikerSize);

//            System.out.println("striker striker collision");
            System.out.println(fDistance);
            System.out.println(fOverlap);

//
            //setting striker1 (displacing towards neg direction
            double finalx = fOverlap * (striker1.getX()-striker2.getX()) / fDistance;
            striker1.setX(striker1.getX()-finalx);


            double finaly = fOverlap * (striker1.getY()-striker2.getY()) / fDistance;
            striker1.setY(striker1.getY()-finaly);

            striker1.draw();

//
            //setting striker2 (displacing towards positive direction
            double finalx2 = fOverlap * (striker1.getX()-striker2.getX()) / fDistance;
            striker2.setX(striker2.getX()+finalx2);


            double finaly2 = fOverlap * (striker1.getY()-striker2.getY()) / fDistance;
            striker2.setY(striker2.getY()+finaly2);


            striker2.draw();

        }

        // Collisions with the strikers to the ball
//        if (intersects(ball, striker1) || intersects(ball, striker2)) {

        // Collisions with the strikers
        if (intersects(ball, striker1) || intersects(ball, striker2)) {


            // Create a blast effect at the collision point
            BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//                BlastEffect blastEffect2 = new BlastEffect(ball.getX(), ball.getY()-blastoffset, Color.YELLOW);
//                BlastEffect blastEffect3 = new BlastEffect(ball.getX()+blastoffset, ball.getY(), Color.GOLDENROD);
//                BlastEffect blastEffect4 = new BlastEffect(ball.getX()+blastoffset, ball.getY()-blastoffset, Color.GOLDENROD);
//                BlastEffect blastEffect5 = new BlastEffect(ball.getX()+blastoffset-blastoffset, ball.getY()+blastoffset, Color.GOLDENROD);

            // Add the blast effect to the scene
            pane.getChildren().add(blastEffect1);
//                pane.getChildren().add(blastEffect2);
//                pane.getChildren().add(blastEffect3);
//                pane.getChildren().add(blastEffect4);
//                pane.getChildren().add(blastEffect5);


            double relativeCollisionX = ball.getX() - striker1.getX();
            double relativeCollisionY = ball.getY() - striker1.getY();

            // Calculate the angle of collision
            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);

            // Determine the direction of the striker's movement
            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

            // Calculate the angle between the striker's movement direction and the collision angle
            double angleDifference = collisionAngle - strikerMovementDirection;

            // Adjust the direction of the ball based on the angle difference
            double newBallXVelocity = Math.cos(angleDifference) * ball.getXVelocity() - Math.sin(angleDifference) * ball.getYVelocity();
            double newBallYVelocity = Math.sin(angleDifference) * ball.getXVelocity() + Math.cos(angleDifference) * ball.getYVelocity();

            // Update the ball's velocity
            ball.setXDirection(newBallXVelocity);
            ball.setYDirection(newBallYVelocity);
        }
//        }


    }

    // game restart after winning 1
    @FXML
    private void restartGame(MouseEvent event) {
        if (gameEnded) {
//             Reset the game state
            score.setPlayerScores(0, 0); // Reset scores
            resetPositions(); // Reset positions of the ball and strikers
            draw_scoreboard();
            winText.setVisible(false); // Hide the win text
            score.setPlayerScores(0,0);
            score.setTime(30);



            // Restart the game loop
            gameLoop.start();
            score.startTimer(gc);

            // Reset the game ended flag
            gameEnded = false;
        }
    }


    private void resetPositions() {
        // Remove previously drawn ball and strikers from the pane
        pane.getChildren().remove(ball.getCircle());
        pane.getChildren().remove(striker1.getCircle());
        pane.getChildren().remove(striker2.getCircle());

        // Reset positions for striker1 and striker2
        newStriker(100, 300, Color.RED,1, striker1); // initial position of the strikers in the board
        newStriker(700, 300, Color.GREEN,2, striker2);

        // Reset ball position
        newBall();
    }

    private boolean intersects(Ball ball, Striker striker) {

        Circle ballCircle = ball.getCircle();
        Circle strikerCircle = striker.getCircle();
//
//        System.out.println(getDistance(striker.getX(),striker.getY(),ball.getX(),ball.getY()));
//        System.out.println(Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2));

        return getDistance(striker.getX(),striker.getY(),ball.getX(),ball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2);

//        // checks that if both bounds collide or not
//        return strikerCircle.getBoundsInParent().intersects(ballCircle.getBoundsInParent()) || ballCircle.getBoundsInParent().intersects(strikerCircle.getBoundsInParent());
    }

    private boolean intersects(Striker striker1, Striker striker2) {

//        Circle ballCircle = ball.getCircle();
//        Circle strikerCircle = striker.getCircle();

//        System.out.println(getDistance(striker1.getX(),striker1.getY(),striker2.getX(),striker2.getY()));
//        System.out.println(Math.pow(striker1.getradius() + striker2.getradius(),2));

        return getDistance(striker1.getX(),striker1.getY(),striker2.getX(),striker2.getY()) <= Math.pow(striker1.getradius() + striker2.getradius(),2);

//        // checks that if both bounds collide or not
//        return strikerCircle.getBoundsInParent().intersects(ballCircle.getBoundsInParent()) || ballCircle.getBoundsInParent().intersects(strikerCircle.getBoundsInParent());
    }


    private double getDistance(double x1,double y1,double x2,double y2)
    {
        double xDis = x2-x1;
        double yDis = y2-y1;

        return xDis * xDis + yDis * yDis;

    }


    private void released(MouseEvent event, Striker striker) {

        long endTime = System.nanoTime();
        double timeElapsed = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        double distanceX = event.getSceneX() - startX;
        double distanceY = event.getSceneY() - startY;
        double speedX = distanceX / timeElapsed; // Speed in pixels per second
        double speedY = distanceY / timeElapsed; // Speed in pixels per second

        System.out.println("Mouse Dragging Speed (X): " + speedX);
        System.out.println("Mouse Dragging Speed (Y): " + speedY);
    }

    private void dragged(MouseEvent event, Striker striker) {

        // determines the tasks when striker is dragged
        double gridx = striker.getX() + event.getX();
        double gridy = striker.getY() + event.getY();

        System.out.println("x "+ gridx);
        System.out.println("y "+ gridy);

        striker.setX(gridx);
        striker.setY(gridy);
        striker.draw();
    }

    private void pressed(MouseEvent event, Striker striker) {
        startX = event.getSceneX();
        startY = event.getSceneY();
        startTime = System.nanoTime();
    }
}
