package com.example.aeroblitz;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class Board {


    @FXML
    private AnchorPane pane;

    @FXML
    private Rectangle player1goalps,player2goalps;

    @FXML
    private Button slowOpponentButton;
    private boolean isOpponentStrikerFrozen=false;

    private final int GAME_WIDTH = 800; //adjust the size of the board
    private final int GAME_HEIGHT = 600;
    private final int BALL_DIAMETER = 20; //adjust the size of the ball

    private final Set<KeyCode> pressedKeys = new HashSet<>(); // for keyboard movements in striker2

    private  double STRIKER_SPEED = 2; // speed of the striker2
    private double goalpos = 230;

    private Striker striker1;
    private Striker striker2;
    private Ball ball;
    private slow_ball slowball;

    private int ballVelocity = 5; // Initial ball velocity
    private int slowVelocity = 2;
    private boolean isSlowed = false;
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

    private boolean goalpostbarrier=false;



    private final AnimationTimer gameLoop = new AnimationTimer()
    {
        private static final int FPS = 120; // Desired frames per second
        private static final long ONE_SECOND_NANOS = 1_000_000_000L;
        private static final long FRAME_INTERVAL_NANOS = ONE_SECOND_NANOS / FPS;
        private long lastUpdateTime = 0;

        @Override
        public void handle(long now)
        {
            if (lastUpdateTime == 0) {
                lastUpdateTime = now;
                return;
            }

            long elapsedNanos = now - lastUpdateTime;
            if (elapsedNanos >= FRAME_INTERVAL_NANOS)
            {
                lastUpdateTime = now;
                updateGame();
            }
        }

        private void updateGame()
        {
            drawTrail();

            if (ballVelocity == 2)
            {
                slowball.move();
                slowball.draw();
                updateMotionTrail(ball);
                checkCollisionslow();

                System.out.println("time " + score.getTime());

                if (score.getTime() == 0)
                {
                    endGame();
                }

            }
            else
            {
                ball.getCircle().setVisible(true);
                ball.move();
                ball.draw();
                updateMotionTrail(ball);
                checkCollision();

                System.out.println("time " + score.getTime());

                if (score.getTime() == 0)
                {
                    endGame();
                }
            }
        }

        private void endGame()
        {
            gameEnded = true;
            gameLoop.stop();
            score.pauseTimer();

            if (score.player1 > score.player2) {
                winText.setText("Player 1 wins!!");
                winText.setVisible(true);
            } else if (score.player2 > score.player1) {
                winText.setText("Player 2 wins!!");
                winText.setVisible(true);
            } else {
                winText.setText("It's a Draw!!");
                winText.setVisible(true);
            }

            System.out.println(score.player1);
            System.out.println(score.player2);
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
        newBall();
        newslowBall();// initial position of the ball, it will start from any random position in the middle line of the board

        // Start the game loop
        gameLoop.start();
    }


    //motion trail is turned off for now

    private void drawTrail() {

    }

    private void updateMotionTrail(Ball ball) {


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

    public void newslowBall() {

        Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
        c.setFill(Color.YELLOW);
        c.setStroke(Color.WHITE);

        Random random = new Random();
        slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
        pane.getChildren().add(c);
        //slowball.draw();

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

        if(striker.getID() == 2) {
//        if(true) {

//            // Setting mouse control
//            c.setOnMousePressed(event -> pressed(event, striker));
//            c.setOnMouseDragged(event -> dragged(event, striker));
//            c.setOnMouseReleased(event -> released(event, striker));

//            MouseControl m1 = new MouseControl(c, striker);
            MouseControl m1 = new MouseControl(striker2, c);

            m1.start();
        }
        else
        {

            KeyboardControl k1 = new KeyboardControl(striker, pane);

//            // Setting keyboard control
//            pane.setOnKeyPressed(event -> keyPressed(event, striker));
//            pane.requestFocus(); // Ensure the pane has focus to receive key events

//            KeyboardControl k1 = new KeyboardControl();

            k1.start();

        }
    }

    //Collision physics

    public void checkCollisionslow() {


        int diameter = strikerSize/2;
        int ball_radius = BALL_DIAMETER/2;

        int rectWidth = 50;
        int rectHeight = 150;
        int rectX = 0; // Positioned on the left wall
        int rectY = (GAME_HEIGHT - rectHeight) / 2;


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
        if (slowball.getY() <= 0 || slowball.getY() >= (GAME_HEIGHT - BALL_DIAMETER)) {
            slowball.setYDirection(-slowball.getYVelocity());
        }

        // Ball bounce off the left and right edges  & setting the goalpost
        if (slowball.getX() <= 0 && slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER)) {

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
        else if (slowball.getX() >= (GAME_WIDTH - BALL_DIAMETER) && slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER)) {

            score.player1++;

            resetPositions();
            draw_scoreboard();
            System.out.println(score.player2);

        }else if (slowball.getX() <= 0 || slowball.getX() >= (GAME_WIDTH - BALL_DIAMETER)) {
            slowball.setXDirection(-slowball.getXVelocity());
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
        if (intersectsslow(slowball, striker1) || intersectsslow(slowball, striker2)) {


            // Create a blast effect at the collision point
            BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
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


            double relativeCollisionX = slowball.getX() - slowball.getX();
            double relativeCollisionY = slowball.getY() - slowball.getY();

            // Calculate the angle of collision
            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);

            // Determine the direction of the striker's movement
            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

            // Calculate the angle between the striker's movement direction and the collision angle
            double angleDifference = collisionAngle - strikerMovementDirection;

            // Adjust the direction of the ball based on the angle difference
            double newBallXVelocity = Math.cos(angleDifference) * slowball.getXVelocity() - Math.sin(angleDifference) * slowball.getYVelocity();
            double newBallYVelocity = Math.sin(angleDifference) * slowball.getXVelocity() + Math.cos(angleDifference) * slowball.getYVelocity();

            // Update the ball's velocity
            slowball.setXDirection(newBallXVelocity);
            slowball.setYDirection(newBallYVelocity);


        }
//        }


    }

    public void checkCollision() {


        int diameter = strikerSize/2;
        int ball_radius = BALL_DIAMETER/2;
        int rectWidth = 50;
        int rectHeight = 150;
        int rectX = 0; // Positioned on the left wall
        int rectY = (GAME_HEIGHT - rectHeight) / 2;


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
       if (ball.getX() <= 0 && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier) {

            score.player2++;

            resetPositions();
            draw_scoreboard();
            System.out.println(score.player1);

        }

        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier) {
            // Check collision with the top side of the rectangle
            if (ball.getY() - ball_radius <= rectY) {
                ball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if (ball.getY() + ball_radius >= rectY + rectHeight) {
                ball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the right side of the rectangle
            if (ball.getX() - ball_radius <= rectX + rectWidth) {
                ball.setX(rectX + rectWidth + ball_radius); // Prevent the ball from entering the rectangle
                ball.setXDirection(-ball.getXVelocity());
            }
        }

        else if (ball.getX() >= (GAME_WIDTH - BALL_DIAMETER) && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER)) {

            score.player1++;


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

    private boolean intersectsslow(slow_ball slowball, Striker striker) {

        Circle ballCircle = slowball.getCircle();
        Circle strikerCircle = striker.getCircle();
//
//        System.out.println(getDistance(striker.getX(),striker.getY(),ball.getX(),ball.getY()));
//        System.out.println(Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2));

        return getDistance(striker.getX(),striker.getY(),slowball.getX(),slowball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2);

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




    @FXML
    private void handleSlowOpponentButtonClick(ActionEvent actionEvent) {
        if (true) {
            isSlowed = true;
            ballVelocity=2;

            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
            c.setFill(Color.YELLOW);
            c.setStroke(Color.WHITE);

            Random random = new Random();
            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
            ball.getCircle().setVisible(false);


            pane.getChildren().add(c);



            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(5),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            ballVelocity = 5; // Reset the ball's velocity to its initial value

                            isSlowed = false;
                            slowball.getCircle().setVisible(false);
                            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
                            c.setFill(Color.YELLOW);
                            c.setStroke(Color.WHITE);
                            ball.setX(slowball.getX());
                            ball.setY(slowball.getY());

                            Random random = new Random();
                            ball.getCircle().setVisible(true);


                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    @FXML
    private void handlefreezeOpponentButtonClick(ActionEvent actionEvent) {
        // Check if the opponent striker is not already frozen
        if (!isOpponentStrikerFrozen) {
            // Set the flag to indicate that the opponent striker is frozen
            isOpponentStrikerFrozen = true;

            // Disable mouse events for the opponent striker
            striker2.getCircle().setDisable(true);

            // Start a timeline to unfreeze the opponent striker after 5 seconds
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(5),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // Reset the flag to indicate that the opponent striker is no longer frozen
                            isOpponentStrikerFrozen = false;

                            // Enable mouse events for the opponent striker
                            striker2.getCircle().setDisable(false);
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }
    @FXML
    private void handlegoalpostbarrierButtonClick(ActionEvent actionEvent) {

        if (!goalpostbarrier) {

            goalpostbarrier = true;
            player1goalps.setStroke(Color.RED);
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(5),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            goalpostbarrier = false;
                            player1goalps.setStroke(Color.WHITE);
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
        }
    }

    class MouseControl extends Thread {
        private Striker striker;
        private Circle circle;

        public MouseControl(Striker striker, Circle circle) {
            this.striker = striker;
            this.circle = circle;
        }

        @Override
        public void run() {
            circle.setOnMousePressed(event -> pressed(event, striker));
            circle.setOnMouseDragged(event -> dragged(event, striker));
            circle.setOnMouseReleased(event -> released(event, striker));
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

    public class KeyboardControl extends Thread {
        private Striker striker;
        private Pane pane;
        private Set<KeyCode> pressedKeys = new HashSet<>();
        private static final double STRIKER_SPEED = 30.0;

        public KeyboardControl(Striker striker, Pane pane) {
            this.striker = striker;
            this.pane = pane;
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                pane.setOnKeyPressed(this::keyPressed);
                pane.setOnKeyReleased(this::keyReleased);
                pane.requestFocus(); // Ensure the pane has focus to receive key events
            });
        }

        private void keyPressed(KeyEvent event) {
            pressedKeys.add(event.getCode());
            moveStriker();
        }

        private void keyReleased(KeyEvent event) {
            pressedKeys.remove(event.getCode());
            moveStriker();
        }

        private void moveStriker() {
            double deltaX = 0;
            double deltaY = 0;

            if (pressedKeys.contains(KeyCode.UP)) deltaY -= 1;
            if (pressedKeys.contains(KeyCode.LEFT)) deltaX -= 1;
            if (pressedKeys.contains(KeyCode.DOWN)) deltaY += 1;
            if (pressedKeys.contains(KeyCode.RIGHT)) deltaX += 1;

            // Normalize diagonal movement
            if (deltaX != 0 && deltaY != 0) {
                double diagonalFactor = Math.sqrt(2) / 2;
                deltaX *= diagonalFactor;
                deltaY *= diagonalFactor;
            }

            double newX = striker.getX() + deltaX * STRIKER_SPEED;
            double newY = striker.getY() + deltaY * STRIKER_SPEED;

            // Boundary checking can be added here if needed
            // newX = Math.max(Math.min(newX, GAME_WIDTH - strikerSize / 2), strikerSize / 2);
            // newY = Math.max(Math.min(newY, GAME_HEIGHT - strikerSize / 2), strikerSize / 2);

            striker.setX(newX);
            striker.setY(newY);
            striker.draw();
        }
    }

}





