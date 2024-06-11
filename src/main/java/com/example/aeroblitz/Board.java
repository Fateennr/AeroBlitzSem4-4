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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.scene.image.Image;

import java.util.*;

public class Board {


    @FXML
    private AnchorPane pane;

    @FXML
    private Rectangle boardneww,sidebar1;



    private double prevxs1,prevys1,prevxs2,prevys2;
    private boolean frozen=true,slowballbool=true,goalpost=true;

    private boolean frozenplay2=true,slowballboolplay2=true,goalpostplay2=true;

    @FXML
    private Rectangle player1goalps,player2goalps;

    @FXML
    private Button slowOpponentButton;
    @FXML
    private Button freeze1s;

    @FXML
    private Button freeze2s;

    @FXML
    private Button slow2s;
    @FXML
    private Button goal1s,goal2s;
    private boolean isOpponentStrikerFrozen=false;


    private boolean blastEffectActive = false;

    private long lastCollisionTime = 0;

    private long lastUpdateTime=0;
    private static final long COLLISION_COOLDOWN = 500;

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

    private double ballVelocity ; // Initial ball velocity
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

    @FXML

    private boolean goalpostbarrier=false;
    private  boolean goalpostbarrierplay2=false;



    private final AnimationTimer gameLoop = new AnimationTimer() { //game loop for running updates in the board
        @Override
        public void handle(long now) {

            drawTrail();


                moveAIStriker();


            if (ballVelocity == slowball.getInitialSpeed()) {

                slowball.move();
                slowball.draw();

                checkCollisionslow();

                System.out.println("time " + score.getTime());



                if (score.getTime() == 0) {
                    gameEnded = true;

                    frozen=true;
                    frozenplay2=true;
                    slowballbool=true;
                    slowballboolplay2=true;

                    goalpost=true;
                    goalpostplay2=true;
                    goalpostbarrier=false;
                    isSlowed=false;
                    isOpponentStrikerFrozen=false;
                    player1goalps.setStroke(Color.WHITE);
                    slowball.getCircle().setVisible(false);

                    ball.setX(slowball.getX());
                    ball.setY(slowball.getY());

                    ball.getCircle().setVisible(true);
                    gameLoop.stop();
                    score.pauseTimer();

                    if (score.player1 > score.player2) {
                        winText.setText(String.format("Player 1 wins!!"));
                        winText.setVisible(true); // Hide the win text
                    } else if (score.player2 > score.player1) {
                        winText.setText(String.format("Player 2 wins!!"));
                        winText.setVisible(true); // Hide the win text
                    } else {
                        winText.setText(String.format("Its a Draw!!"));
                        winText.setVisible(true); // Hide the win text
                    }


                    System.out.println(score.player1);
                    System.out.println(score.player2);

                }
            } else {

                ball.getCircle().setVisible(true);
                // Update the position of the ball
                ball.move();
                ball.draw();

                // Update motion trail for the ball
                updateMotionTrail(ball);

                // Check for collisions
                checkCollision();

                System.out.println("time " + score.getTime());

                if (score.getTime() == 0) {
                    gameEnded = true;
                    goalpostbarrier=false;
                    isSlowed=false;
                    isOpponentStrikerFrozen=false;
                    striker2.getCircle().setDisable(false);
                    player1goalps.setStroke(Color.WHITE);
                    slowball.getCircle().setVisible(false);

                    ball.setX(slowball.getX());
                    ball.setY(slowball.getY());

                    ball.getCircle().setVisible(true);
                    gameLoop.stop();
                    score.pauseTimer();

                    if (score.player1 > score.player2) {
                        winText.setText(String.format("Player 1 wins!!"));
                        winText.setVisible(true); // Hide the win text
                    } else if (score.player2 > score.player1) {
                        winText.setText(String.format("Player 2 wins!!"));
                        winText.setVisible(true); // Hide the win text
                    } else {
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


        }
    };

    @FXML
    public void initialize() {

        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT); // Adjust the size as needed

        lastUpdateTime = System.nanoTime();
        Image ballImage = new Image(getClass().getResourceAsStream("/board.png"));
        ImagePattern ballPattern = new ImagePattern(ballImage);
        boardneww.setFill(ballPattern);
        Image ballImage2 = new Image(getClass().getResourceAsStream("/oneside.jpg"));
        ImagePattern ballPattern2 = new ImagePattern(ballImage2);

        sidebar1.setFill(ballPattern2);



        gc = canvas.getGraphicsContext2D();
        gc1 = canvas.getGraphicsContext2D();

        pane.getChildren().add(canvas);

        score = new Score(GAME_WIDTH, GAME_HEIGHT); // Pass the dimensions of your game window
        score.setPlayerScores(0, 0); // Set initial scores

        draw_scoreboard();

        // Initialize the strikers
        striker1 = new Striker(); // Initialize with appropriate values
        striker2 = new Striker(); // Initialize with appropriate values


        // Initialize the board
        newStriker(100, 300, Color.RED, 1, striker1); // initial position of the strikers in the board
        newStriker2(700, 300, Color.GREEN,2, striker2);
        prevxs1=100;
        prevys1=300;
        prevxs2=700;
        prevys2=300;

        newBall();
        newslowBall();// initial position of the ball, it will start from any random position in the middle line of the board
        ballVelocity=ball.getspeed();

        // Start the game loop
        gameLoop.start();
    }

    private void moveAIStriker()
    {
        double predictedY, predictedX;

        // Move the AI striker towards the predicted position
        double xSpeed = 4; // Adjust speed as necessary
        double ySpeed = 4; // Adjust speed as necessary

        double ballY = ball.getY();
        double strikerY = striker2.getY();

        // Calculate the distance between the ball and the AI-controlled striker
        double distance = Math.abs(ballY - strikerY);

        if(distance <= 50)
        {
            xSpeed = 8;
            ySpeed = 8;
        }
        else
        {
            xSpeed = 4;
            ySpeed = 4;
        }

        // Calculate the distance and direction of the ball from the striker
        double ballDistanceX = ball.getX() - striker2.getX();
        double ballDistanceY = ball.getY() - striker2.getY();

        // Defensive logic
        if (ball.getXVelocity() > 0)
        {
            // Ball is moving towards the AI's goal
            // Predict the ball's future position
            predictedY = ball.getY() + (ball.getYVelocity() / ball.getXVelocity()) * ballDistanceX;
            predictedX = striker2.getX(); // Keep x position for blocking

        }
        else
        {
            // Offensive logic
            // Move towards a better attacking position
            predictedY = ball.getY() + 50; // Add some randomness for Y
            predictedX = ball.getX() + 50; // Position the striker ahead of the ball
        }

        // Ensure the target position is within bounds
        predictedY = Math.max(0, Math.min(predictedY, GAME_HEIGHT - strikerSize));
        predictedX = Math.max(0, Math.min(predictedX, GAME_WIDTH - strikerSize));



        if (predictedY < striker2.getY())
        {
            striker2.setY(striker2.getY() - ySpeed);

        }
        else if (predictedY > striker2.getY())
        {
            striker2.setY(striker2.getY() + ySpeed);
        }

        striker2.draw();

        if (predictedX < striker2.getX()) {
            striker2.setX(striker2.getX() - xSpeed);
        }
        else if (predictedX > striker2.getX())
        {
            striker2.setX(striker2.getX() + xSpeed);
        }

        striker2.draw();
    }


    //motion trail is turned off for now

    private void drawTrail() {

    }

    private void perturbDirection(Ball ball) {
        double speed = Math.sqrt(ball.getXVelocity() * ball.getXVelocity() + ball.getYVelocity() * ball.getYVelocity());
        double angle = Math.atan2(ball.getYVelocity(), ball.getXVelocity());
        double perturbationAngle = (Math.random() - 0.5) * 0.1; // Small perturbation angle

        angle += perturbationAngle;

        double newBallXVelocity = Math.cos(angle) * speed;
        double newBallYVelocity = Math.sin(angle) * speed;

        ball.setXDirection(newBallXVelocity);
        ball.setYDirection(newBallYVelocity);
    }

    private void perturbDirectionslow(slow_ball ball) {
        double speed = Math.sqrt(ball.getXVelocity() * ball.getXVelocity() + ball.getYVelocity() * ball.getYVelocity());
        double angle = Math.atan2(ball.getYVelocity(), ball.getXVelocity());
        double perturbationAngle = (Math.random() - 0.5) * 0.1; // Small perturbation angle

        angle += perturbationAngle;

        double newBallXVelocity = Math.cos(angle) * speed;
        double newBallYVelocity = Math.sin(angle) * speed;

        ball.setXDirection(newBallXVelocity);
        ball.setYDirection(newBallYVelocity);
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
        c.setStroke(Color.BLACK);
        //  Image ballImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("src/main/resources/com/example/aeroblitz/ball.jpg")));
        // Image ballImage = new Image("src/main/resources/com/example/aeroblitz/ball.jpg"); // Update the path to your image file
        Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
        ImagePattern ballPattern = new ImagePattern(ballImage);
        c.setFill(ballPattern);

        Random random = new Random();
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2),(GAME_HEIGHT/2) - (BALL_DIAMETER/2)-16, BALL_DIAMETER, c);
        //  ball=new Ball(GAME_WIDTH,GAME_HEIGHT-60,BALL_DIAMETER,c);
        pane.getChildren().add(c);
        ball.draw();

    }

    public void update() {
        long now = System.nanoTime();
        double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
        lastUpdateTime = now;

        ball.update(deltaTime);
     /*   striker1.updatePosition(deltaTime);
        striker2.updatePosition(deltaTime);*/

        checkCollision();



    }

    public void newslowBall() {

        Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle

        c.setStroke(Color.BLACK);
        Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
        ImagePattern ballPattern = new ImagePattern(ballImage);
        c.setFill(ballPattern);

        Random random = new Random();
        slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
        pane.getChildren().add(c);


    }

    public void newStriker(int x, int y, Color color, int id, Striker striker) {
        Circle c = new Circle();
        Color neonColor = color.brighter().brighter().brighter();
        c.setFill(Color.WHITE);
        //c.setStroke(neonColor);


        // Set the stroke width
        strokeWidth = 15.0; // Change this value as needed
        Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
        ImagePattern ballPattern = new ImagePattern(ballImage);
        c.setFill(ballPattern);
        // c.setStrokeWidth(strokeWidth);



        // double radius = strikerSize / 3.0;
        //  double radius=strikerSize/3.0+15;
        double radius=48.33;


        striker.setID(id);
        striker.setX(x);
        striker.setY(y);
        striker.setColor(color);
        striker.setRadius(radius);
        striker.setCircle(c); // Assign the Circle object to the Striker
        pane.getChildren().add(c);

        striker.draw(); //draw the striker

        if (striker.getID() == 2)
        {
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
            MouseControl k1 = new MouseControl(striker, c);

//            // Setting keyboard control
//            pane.setOnKeyPressed(event -> keyPressed(event, striker));
//            pane.requestFocus(); // Ensure the pane has focus to receive key events

//            KeyboardControl k1 = new KeyboardControl();

            k1.start();

        }
    }
    public void newStriker2(int x, int y, Color color, int id, Striker striker) {
        Circle c = new Circle();
        Color neonColor = color.brighter().brighter().brighter();
        c.setFill(Color.WHITE);
        // c.setStroke(neonColor);


        // Set the stroke width
        strokeWidth = 15.0; // Change this value as needed
        Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
        ImagePattern ballPattern = new ImagePattern(ballImage);
        c.setFill(ballPattern);
        //  c.setStrokeWidth(strokeWidth);

        // double radius = strikerSize / 3.0;

        double radius=48.33;

        striker.setID(id);
        striker.setX(x);
        striker.setY(y);
        striker.setColor(color);
        striker.setRadius(radius);
        striker.setCircle(c); // Assign the Circle object to the Striker
        pane.getChildren().add(c);

        striker.draw(); //draw the striker

        if (striker.getID() == 2)
        {
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
        int rectx2=rectX+GAME_WIDTH;
        int rectY = (GAME_HEIGHT - rectHeight) / 2;
        long currentTime = System.currentTimeMillis();


        // left side Boundary checking for striker1
        if (striker1.getX() <= diameter) {
            striker1.setX(diameter);
            striker1.draw();
        }

        if (striker1.getX() >= (GAME_WIDTH/2 - (diameter))) {
            striker1.setX(GAME_WIDTH/2 - (diameter));
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
        if (striker2.getX() <= GAME_WIDTH/2+diameter) {
            striker2.setX(GAME_WIDTH/2+diameter);
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
        if (slowball.getX() - ball_radius <= rectX + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrier ) {
            // Check collision with the top side of the rectangle
            if (slowball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();

                slowball.setYDirection(-slowball.getYVelocity());
                slowball.setY(rectY - ball_radius );
            }
            // Check collision with the bottom side of the rectangle
            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setYDirection(-slowball.getYVelocity());
                slowball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle

            }
        }

        else if (slowball.getX() - ball_radius >= rectx2 - rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrierplay2 ) {
            // Check collision with the top side of the rectangle
            if (slowball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();

                slowball.setYDirection(-slowball.getYVelocity());
                slowball.setY(rectY - ball_radius );
            }
            // Check collision with the bottom side of the rectangle
            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setYDirection(-slowball.getYVelocity());
                slowball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle

            }
        }
        else if (slowball.getX() >= (GAME_WIDTH - 36-30) && slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrierplay2) {

            score.player1++;
            resetPositions();
            draw_scoreboard();
            gameLoop.stop();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            gameLoop.start();
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println(score.player2);

        }

        else if (slowball.getX() <= 36+30 &&  slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier && true) {

            score.player2++;
            resetPositions();
            draw_scoreboard();
            gameLoop.stop();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            gameLoop.start();
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println(score.player1);



        }


        if (slowball.getX() - ball_radius <= rectX + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier ) {
            // Check collision with the top side of the rectangle
            if (slowball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
                slowball.setYDirection(-slowball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
                slowball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the right side of the rectangle
            if (slowball.getX() - ball_radius <= rectX + rectWidth) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                slowball.setX(rectX + rectWidth + ball_radius); // Prevent the ball from entering the rectangle
                slowball.setXDirection(-slowball.getXVelocity());
            }
        }



        if (slowball.getX() - ball_radius >= rectx2 + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrierplay2 ) {
            // Check collision with the top side of the rectangle
            if (slowball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
                slowball.setYDirection(-slowball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                slowball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
                slowball.setYDirection(-slowball.getYVelocity());
            }
            // Check collision with the right side of the rectangle
            if (slowball.getX() - ball_radius >= rectx2 - rectWidth) {
                BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                slowball.setX(rectx2 - rectWidth - ball_radius); // Prevent the ball from entering the rectangle
                slowball.setXDirection(-slowball.getXVelocity());
            }
        }


        if (slowball.getY() <= 32 || slowball.getY() >= (GAME_HEIGHT - 50)) {
            slowball.setYDirection(-slowball.getYVelocity());
            perturbDirectionslow(slowball);
            //  ball.setXDirection(ball.getXVelocity() + (Math.random() - 0.5) * 0.1);
            if(slowball.getYVelocity()==0 || slowball.getXVelocity()==0 )
            {
                Random random = new Random();
                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
                slowball.setXDirection(randomXDirection * slowball.getInitialSpeed());
                slowball.setYDirection(randomYDirection * slowball.getInitialSpeed());
                perturbDirectionslow(slowball);
            }

        }
        if (slowball.getX() <= 36 || slowball.getX() >= (GAME_WIDTH - 36)) {
            slowball.setXDirection(-slowball.getXVelocity());
            perturbDirectionslow(slowball);
            //  ball.setYDirection(ball.getYVelocity() + (Math.random() - 0.5) * 0.1);

            if(slowball.getYVelocity()==0 || slowball.getXVelocity()==0 )
            {
                Random random = new Random();
                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
                slowball.setXDirection(randomXDirection * slowball.getInitialSpeed());
                slowball.setYDirection(randomYDirection * slowball.getInitialSpeed());
                perturbDirectionslow(slowball);
            }
        }

        // Collisions with the strikers
        else if (intersectsslow(slowball, striker1) || intersectsslow(slowball, striker2) ) {

            BlastEffect blastEffect1 = new BlastEffect(slowball.getX()-blastoffset, slowball.getY()+blastoffset, Color.rgb(104, 110, 148));

            pane.getChildren().add(blastEffect1);
            blastEffectActive = true;

            // Reset the flag after a certain duration
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                blastEffectActive = false;
                pane.getChildren().remove(blastEffect1);
            }));
            timeline.setCycleCount(1);
            timeline.play();


            double relativeCollisionX = slowball.getX() - striker1.getX();
            double relativeCollisionY = slowball.getY() - striker1.getY();

            // Calculate the angle of collision
            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);

            // Determine the direction of the striker's movement
            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

            // Calculate the angle between the striker's movement direction and the collision angle
            double angleDifference = collisionAngle - strikerMovementDirection;
            double randomAngleOffset = (Math.random() - 0.5) * 0.1;
            angleDifference=(angleDifference+randomAngleOffset);

            // Adjust the direction of the ball based on the angle difference
            double newBallXVelocity = Math.cos(angleDifference) * slowball.getXVelocity() - Math.sin(angleDifference) * slowball.getYVelocity();
            double newBallYVelocity = Math.sin(angleDifference) * slowball.getXVelocity() + Math.cos(angleDifference) * slowball.getYVelocity();

            double newBallX = slowball.getX() + newBallXVelocity;
            double newBallY = slowball.getY() + newBallYVelocity;
            Random random = new Random();
            int i=random.nextInt(5);


            //top edge or left edge
            if(newBallX<=36 || newBallY<=32)
            {
                if(newBallX<=36 && newBallY>32)
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {
                        //newBallX -= strikerMovementDirection*5;
                        newBallX -= 2 * 50;
                    }
                } else if (newBallX>36 && newBallY<=32) {
                    /*newBallY += newBallYVelocity;*/
                    newBallY+=50;
                    while (newBallY<=32)
                    {
                        newBallY -=2*050;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX <= 36 && newBallY <= 32) {
                        newBallX -= 2* 50;
                    }
                    // newBallY += newBallYVelocity;
                    newBallY+=50;
                    while (newBallY <= 32) {
                        newBallY -= 2 * 50;
                    }
                }



            }
            else if(newBallX<=36 || newBallY>=(GAME_HEIGHT-50))

            {
                if(newBallX<=36 && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {
                        newBallX -=2*50;
                    }
                } else if (newBallX>36 && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if (newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -=2*50;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX <= 36 && newBallY >= (GAME_HEIGHT - 50)) {
                        newBallX -= 50 * 2;
                    }
                    newBallY += 50;
                    if (newBallY >= (GAME_HEIGHT - 50)) {
                        newBallY -= 50 * 2;
                    }
                }



            }
            else if(newBallX>=(GAME_WIDTH-36) || newBallY<=32)
            {
                if(newBallX>=(GAME_WIDTH-36) && newBallY>32)
                {
                    newBallX += 50;
                    if(newBallX>=(GAME_WIDTH-36))
                    {
                        newBallX -=2*50;
                    }
                } else if (newBallX<(GAME_WIDTH-36) && newBallY<=32) {
                    newBallY += 50;
                    if (newBallY<=32)
                    {
                        newBallY -= 2*50;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX >= (GAME_WIDTH - 36) && newBallY <= 32) {
                        newBallX -= 2 * 50;
                    }
                    newBallY += 50;
                    if (newBallY <= 32) {
                        newBallY -= 2 * 50;
                    }
                }



            }

            else if(newBallX>=(GAME_WIDTH-36) || newBallY>=(GAME_HEIGHT-50))
            {
                if(newBallX>=(GAME_WIDTH-36) && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if(newBallX<=(GAME_WIDTH-36))
                    {
                        newBallX -= 2*50;
                    }
                } else if (newBallX<(GAME_WIDTH-36) && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if(newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -= 2*50;
                    }

                }
                else {
                    newBallX += 50;
                    if (newBallX <= (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
                        newBallX -= 2 * 50;
                    }
                    newBallY += 50;
                    if (newBallY >= (GAME_HEIGHT - 50)) {
                        newBallY -= 2 * 50;
                    }
                }



            }


            double perturbation = 0.1;  // Adjust this value as necessary
            newBallXVelocity += (Math.random() - 0.5) * perturbation;
            newBallYVelocity += (Math.random() - 0.5) * perturbation;


            slowball.setX(newBallX);
            slowball.setY(newBallY);
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
        int rectx2=rectX+GAME_WIDTH;
        int rectY = (GAME_HEIGHT - rectHeight) / 2;
        long currentTime = System.currentTimeMillis();


        // left side Boundary checking for striker1
        if (striker1.getX() <= diameter) {
            striker1.setX(diameter);
            striker1.draw();
        }

        if (striker1.getX() >= (GAME_WIDTH/2 - (diameter))) {
            striker1.setX(GAME_WIDTH/2 - (diameter));
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
        if (striker2.getX() <= GAME_WIDTH/2+diameter) {
            striker2.setX(GAME_WIDTH/2+diameter);
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
        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrier ) {
            // Check collision with the top side of the rectangle
            if (ball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();

                ball.setYDirection(-ball.getYVelocity());
                ball.setY(rectY - ball_radius );
            }
            // Check collision with the bottom side of the rectangle
            if (ball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setYDirection(-ball.getYVelocity());
                ball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle

            }
        }

        else if (ball.getX() - ball_radius >= rectx2 - rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrierplay2 ) {
            // Check collision with the top side of the rectangle
            if (ball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();

                ball.setYDirection(-ball.getYVelocity());
                ball.setY(rectY - ball_radius );
            }
            // Check collision with the bottom side of the rectangle
            if (ball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setYDirection(-ball.getYVelocity());
                ball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle

            }
        }
        else if (ball.getX() >= (GAME_WIDTH - 36-30) && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrierplay2) {

            score.player1++;

            resetPositions();
            draw_scoreboard();
            gameLoop.stop();

            ball.getCircle().setVisible(false);

            // Display "GOAL" label
            winText.setVisible(true);
            winText.setText("GOAL!!");



            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // Hide "GOAL" label
                            winText.setVisible(false);

                            ball.getCircle().setVisible(true);
                            gameLoop.start();
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println(score.player1);

//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player2);

        }

        if (ball.getX() <= 36+30 && /*ball.getY() >= (220+ BALL_DIAMETER) && ball.getY() <= (360) &&*/ ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier && true) {


            score.player2++;


            resetPositions();
            draw_scoreboard();
            gameLoop.stop();

            ball.getCircle().setVisible(false);

            // Display "GOAL" label
            winText.setVisible(true);



            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(2),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // Hide "GOAL" label
                            winText.setVisible(false);

                            ball.getCircle().setVisible(true);
                            gameLoop.start();
                        }
                    }
            ));
            timeline.setCycleCount(1);
            timeline.play();
            System.out.println(score.player1);


//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player1);



        }


        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier ) {
            // Check collision with the top side of the rectangle
            if (ball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if (ball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the right side of the rectangle
            if (ball.getX() - ball_radius <= rectX + rectWidth) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                ball.setX(rectX + rectWidth + ball_radius); // Prevent the ball from entering the rectangle
                ball.setXDirection(-ball.getXVelocity());
            }
        }



        if (ball.getX() - ball_radius >= rectx2 + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrierplay2 ) {
            // Check collision with the top side of the rectangle
            if (ball.getY() - ball_radius <= rectY) {
                lastCollisionTime = currentTime;
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if (ball.getY() + ball_radius >= rectY + rectHeight) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                blastEffectActive = true;

                // Reset the flag after a certain duration
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                    blastEffectActive = false;
                    pane.getChildren().remove(blastEffect1);
                }));
                timeline.setCycleCount(1);
                timeline.play();
                ball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the right side of the rectangle
            if (ball.getX() - ball_radius >= rectx2 - rectWidth) {
                BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//
                pane.getChildren().add(blastEffect1);
                ball.setX(rectx2 - rectWidth - ball_radius); // Prevent the ball from entering the rectangle
                ball.setXDirection(-ball.getXVelocity());
            }
        }

        if (ball.getY() <= 32 || ball.getY() >= (GAME_HEIGHT - 50)) {
            ball.setYDirection(-ball.getYVelocity());
            perturbDirection(ball);
            //  ball.setXDirection(ball.getXVelocity() + (Math.random() - 0.5) * 0.1);
            if(ball.getYVelocity()==0 || ball.getXVelocity()==0 )
            {
                Random random = new Random();
                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
                ball.setXDirection(randomXDirection * ball.getspeed());
                ball.setYDirection(randomYDirection * ball.getspeed());
            }

        }
        if (ball.getX() <= 36 || ball.getX() >= (GAME_WIDTH - 36)) {
            ball.setXDirection(-ball.getXVelocity());
            perturbDirection(ball);
            //  ball.setYDirection(ball.getYVelocity() + (Math.random() - 0.5) * 0.1);

            if(ball.getYVelocity()==0 || ball.getXVelocity()==0 )
            {
                Random random = new Random();
                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
                ball.setXDirection(randomXDirection * ball.getspeed());
                ball.setYDirection(randomYDirection * ball.getspeed());
            }
        }



        // Collisions with the strikers
        else if (intersects(ball, striker1) || intersectss2(ball, striker2) ) {




            BlastEffect blastEffect1 = new BlastEffect(ball.getX()-blastoffset, ball.getY()+blastoffset, Color.rgb(104, 110, 148));
//


            pane.getChildren().add(blastEffect1);
            blastEffectActive = true;

            // Reset the flag after a certain duration
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                blastEffectActive = false;
                pane.getChildren().remove(blastEffect1);
            }));
            timeline.setCycleCount(1);
            timeline.play();


            double relativeCollisionX = ball.getX() - striker1.getX();
            double relativeCollisionY = ball.getY() - striker1.getY();

            // Calculate the angle of collision
            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);

            // Determine the direction of the striker's movement
            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

            // Calculate the angle between the striker's movement direction and the collision angle
            double angleDifference = collisionAngle - strikerMovementDirection;
            double randomAngleOffset = (Math.random() - 0.5) * 0.1;
            angleDifference=(angleDifference+randomAngleOffset);

            // Adjust the direction of the ball based on the angle difference
            double newBallXVelocity = Math.cos(angleDifference) * ball.getXVelocity() - Math.sin(angleDifference) * ball.getYVelocity();
            double newBallYVelocity = Math.sin(angleDifference) * ball.getXVelocity() + Math.cos(angleDifference) * ball.getYVelocity();

            double newBallX = ball.getX() + newBallXVelocity;
            double newBallY = ball.getY() + newBallYVelocity;
            Random random = new Random();
            int i=random.nextInt(5);


            //top edge or left edge
            if(newBallX<=36 || newBallY<=32)
            {
                if(newBallX<=36 && newBallY>32)
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {
                        //newBallX -= strikerMovementDirection*5;
                        newBallX -= 2 * 50;
                    }
                } else if (newBallX>36 && newBallY<=32) {
                    /*newBallY += newBallYVelocity;*/
                    newBallY+=50;
                    while (newBallY<=32)
                    {
                        newBallY -=2*050;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX <= 36 && newBallY <= 32) {
                        newBallX -= 2* 50;
                    }
                    // newBallY += newBallYVelocity;
                    newBallY+=50;
                    while (newBallY <= 32) {
                        newBallY -= 2 * 50;
                    }
                }



            }
            else if(newBallX<=36 || newBallY>=(GAME_HEIGHT-50))

            {
                if(newBallX<=36 && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {
                        newBallX -=2*50;
                    }
                } else if (newBallX>36 && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if (newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -=2*50;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX <= 36 && newBallY >= (GAME_HEIGHT - 50)) {
                        newBallX -= 50 * 2;
                    }
                    newBallY += 50;
                    if (newBallY >= (GAME_HEIGHT - 50)) {
                        newBallY -= 50 * 2;
                    }
                }



            }
            else if(newBallX>=(GAME_WIDTH-36) || newBallY<=32)
            {
                if(newBallX>=(GAME_WIDTH-36) && newBallY>32)
                {
                    newBallX += 50;
                    if(newBallX>=(GAME_WIDTH-36))
                    {
                        newBallX -=2*50;
                    }
                } else if (newBallX<(GAME_WIDTH-36) && newBallY<=32) {
                    newBallY += 50;
                    if (newBallY<=32)
                    {
                        newBallY -= 2*50;
                    }
                }
                else {
                    newBallX += 50;
                    if (newBallX >= (GAME_WIDTH - 36) && newBallY <= 32) {
                        newBallX -= 2 * 50;
                    }
                    newBallY += 50;
                    if (newBallY <= 32) {
                        newBallY -= 2 * 50;
                    }
                }



            }

            else if(newBallX>=(GAME_WIDTH-36) || newBallY>=(GAME_HEIGHT-50))
            {
                if(newBallX>=(GAME_WIDTH-36) && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if(newBallX<=(GAME_WIDTH-36))
                    {
                        newBallX -= 2*50;
                    }
                } else if (newBallX<(GAME_WIDTH-36) && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if(newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -= 2*50;
                    }

                }
                else {
                    newBallX += 50;
                    if (newBallX <= (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
                        newBallX -= 2 * 50;
                    }
                    newBallY += 50;
                    if (newBallY >= (GAME_HEIGHT - 50)) {
                        newBallY -= 2 * 50;
                    }
                }



            }


            double perturbation = 0.1;  // Adjust this value as necessary
            newBallXVelocity += (Math.random() - 0.5) * perturbation;
            newBallYVelocity += (Math.random() - 0.5) * perturbation;


            ball.setX(newBallX);
            ball.setY(newBallY);
            // Update the ball's velocity
            ball.setXDirection(newBallXVelocity);
            ball.setYDirection(newBallYVelocity);


        }



    }




    // game restart after winning 1
    @FXML
    private void restartGame(MouseEvent event) {
        if (gameEnded) {
//             Reset the game state
            goalpostbarrier=false;
            isSlowed=false;
            isOpponentStrikerFrozen=false;
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
        newStriker2(700, 300, Color.GREEN,2, striker2);

        // Reset ball position
        newBall();
    }

    private boolean intersects(Ball ball, Striker striker) {

        Circle ballCircle = ball.getCircle();
        Circle strikerCircle = striker.getCircle();
        boolean result;
        result= getDistance(striker.getX(),striker.getY(),ball.getX(),ball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2);
        double gridx = striker.getX();
        double gridy = striker.getY();

        double disx=ball.getX()-gridx;
        double disy=ball.getY()-gridy;
        double sqaure=disx*disx+disy*disy;

        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
        double relativeCollisionX = ball.getX() - striker1.getX();
        double relativeCollisionY = ball.getY() - striker1.getY();
        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
        if (sqaure < sumOfRadiiSquared && prevxs1!=gridx && prevys1!=gridy) {
            double movediff=Math.pow((sumOfRadiiSquared-sqaure),0.5);
            striker.setX(gridx-movediff*Math.cos(strikerMovementDirection));
            striker.setY(gridy-movediff*Math.sin(strikerMovementDirection));
            prevxs1=gridx-movediff*Math.cos(strikerMovementDirection);
            prevys1=gridy-movediff*Math.sin(strikerMovementDirection);
            striker.draw();

        }
        return result;
    }

    private boolean intersectss2(Ball ball, Striker striker) {

        Circle ballCircle = ball.getCircle();
        Circle strikerCircle = striker.getCircle();
        boolean result;
        result= getDistance(striker.getX(),striker.getY(),ball.getX(),ball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(),2);
        double gridx = striker.getX();
        double gridy = striker.getY();

        double disx=ball.getX()-gridx;
        double disy=ball.getY()-gridy;
        double sqaure=disx*disx+disy*disy;

        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
        double relativeCollisionX = ball.getX() - striker1.getX();
        double relativeCollisionY = ball.getY() - striker1.getY();
        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());

        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
        if (sqaure < sumOfRadiiSquared && prevxs2!=gridx && prevys2!=gridy) {
            double movediff=Math.pow((sumOfRadiiSquared-sqaure),0.5);
            striker.setX(gridx-movediff*Math.cos(strikerMovementDirection));
            striker.setY(gridy-movediff*Math.sin(strikerMovementDirection));
            prevxs2=gridx-movediff*Math.cos(strikerMovementDirection);
            prevys2=gridy-movediff*Math.sin(strikerMovementDirection);
            striker.draw();

        }
        return result;
    }

    private boolean intersectsslow(slow_ball slowball, Striker striker) {

        Circle ballCircle = slowball.getCircle();
        Circle strikerCircle = striker.getCircle();


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

        double disx=ball.getX()-gridx;
        double disy=ball.getY()-gridy;
        double sqaure=disx*disx+disy*disy;

        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
        //double sumOfRadiiSquared =

        double relativeCollisionX = ball.getX() - striker1.getX();
        double relativeCollisionY = ball.getY() - striker1.getY();
        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
        if (sqaure < sumOfRadiiSquared) {

            striker.setX(gridx);
            striker.setY(gridy);
        }


        else
        {

            striker.setX(gridx);
            striker.setY(gridy);
            prevxs1=gridx;
            prevys1=gridy;
            striker.draw();
        }
    }

    private void draggeds2(MouseEvent event, Striker striker) {

        // determines the tasks when striker is dragged
        double gridx = striker.getX() + event.getX();
        double gridy = striker.getY() + event.getY();

        double disx=ball.getX()-gridx;
        double disy=ball.getY()-gridy;
        double sqaure=disx*disx+disy*disy;

        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
        //double sumOfRadiiSquared =

        double relativeCollisionX = ball.getX() - striker1.getX();
        double relativeCollisionY = ball.getY() - striker1.getY();
        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
        if (sqaure < sumOfRadiiSquared) {

            striker.setX(gridx);
            striker.setY(gridy);
        }


        else
        {

            striker.setX(gridx);
            striker.setY(gridy);
            prevxs2=gridx;
            prevys2=gridy;

            striker.draw();
        }
    }





    @FXML
    private void handleSlowOpponentButtonClick(ActionEvent actionEvent) {

        if (slowballbool) {
            slowballbool = false;
            Media sound;
            sound = new Media(getClass().getResource("/slowBall.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> {
                // When media ends, restart it
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
            mediaPlayer.play();
            isSlowed = true;

            Circle c = new Circle();
            c.setStroke(Color.BLACK);
            Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
            ImagePattern ballPattern = new ImagePattern(ballImage);
            c.setFill(ballPattern);



            Random random = new Random();
            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
            ballVelocity = slowball.getInitialSpeed();

            ball.getCircle().setVisible(false);

            pane.getChildren().add(c);

            winText.setText("5");
            winText.setVisible(true);

            freeze1s.setDisable(true);
            freeze2s.setDisable(true);
            goal1s.setDisable(true);
            goal2s.setDisable(true);
            slowOpponentButton.setDisable(true);
            slow2s.setDisable(true);



            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(1),
                    new EventHandler<ActionEvent>() {
                        private int timeLeft = 5;

                        @Override
                        public void handle(ActionEvent event) {
                            timeLeft--;
                            winText.setText(String.valueOf(timeLeft));

                            if (timeLeft == 0) {
                                ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
                                isSlowed = false;
                                slowball.getCircle().setVisible(false);
                                ball.setX(slowball.getX());
                                ball.setY(slowball.getY());
                                ball.getCircle().setVisible(true);
                                if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                    mediaPlayer.stop();
                                }
                                winText.setVisible(false);
                                ((Timeline) event.getSource()).stop();
                                ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value

                                if(frozen) freeze1s.setDisable(false);
                                if(frozenplay2) freeze2s.setDisable(false);
                                if(goalpost) goal1s.setDisable(false);
                                if(goalpostplay2) goal2s.setDisable(false);
                                //if(slowballbool)slowOpponentButton.setDisable(false);
                                if (slowballboolplay2)slow2s.setDisable(false);

                                isSlowed = false;
                                slowball.getCircle().setVisible(false);

                                ball.setX(slowball.getX());
                                ball.setY(slowball.getY());

                                ball.getCircle().setVisible(true);

                            }
                            else if(score.getTime()==0)

                            {
                                ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value

                                if(frozen) freeze1s.setDisable(false);
                                if(frozenplay2) freeze2s.setDisable(false);
                                if(goalpost) goal1s.setDisable(false);
                                if(goalpostplay2) goal2s.setDisable(false);
                                //if(slowballbool)slowOpponentButton.setDisable(false);
                                if (slowballboolplay2)slow2s.setDisable(false);

                                isSlowed = false;
                                slowball.getCircle().setVisible(false);

                                ball.setX(slowball.getX());
                                ball.setY(slowball.getY());

                                ball.getCircle().setVisible(true);

                                ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
                                isSlowed = false;
                                slowball.getCircle().setVisible(false);
                                ball.setX(slowball.getX());
                                ball.setY(slowball.getY());
                                ball.getCircle().setVisible(true);
                                if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                    mediaPlayer.stop();
                                }
                                winText.setVisible(false);
                                ((Timeline) event.getSource()).stop();
                            }
                        }
                    }
            ));
            timeline.setCycleCount(5);
            timeline.play();
        }
//        if (slowballbool) {
//            slowballbool=false;
//            isSlowed = true;
//
//            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//            c.setStroke(Color.BLACK);
//            Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
//            ImagePattern ballPattern = new ImagePattern(ballImage);
//            c.setFill(ballPattern);
//
//            Random random = new Random();
//            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
//            ballVelocity=slowball.getInitialSpeed();
//
//            ball.getCircle().setVisible(false);
//
//            pane.getChildren().add(c);
//            freeze1s.setDisable(true);
//            freeze2s.setDisable(true);
//            goal1s.setDisable(true);
//            goal2s.setDisable(true);
//            slowOpponentButton.setDisable(true);
//            slow2s.setDisable(true);
//
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(5),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
//
//                            if(frozen) freeze1s.setDisable(false);
//                            if(frozenplay2) freeze2s.setDisable(false);
//                            if(goalpost) goal1s.setDisable(false);
//                            if(goalpostplay2) goal2s.setDisable(false);
//                            //if(slowballbool)slowOpponentButton.setDisable(false);
//                            if (slowballboolplay2)slow2s.setDisable(false);
//
//                            isSlowed = false;
//                            slowball.getCircle().setVisible(false);
//
//                            ball.setX(slowball.getX());
//                            ball.setY(slowball.getY());
//
//                            ball.getCircle().setVisible(true);
//
//                            /*   Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//                            c.setFill(Color.YELLOW);
//                            c.setStroke(Color.WHITE);*/
//
//
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//        }
    }

    @FXML
    private void handlefreezeOpponentButtonClick(ActionEvent actionEvent) {
        // Check if the opponent striker is not already frozen

        if (frozen) {
            frozen = false;
            if (!isOpponentStrikerFrozen) {
                // Set the flag to indicate that the opponent striker is frozen
                isOpponentStrikerFrozen = true;
                Media sound = new Media(getClass().getResource("/icespell.mp3").toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setOnEndOfMedia(() -> {
                    // When media ends, restart it
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                });
                mediaPlayer.play();
                // Disable mouse events for the opponent striker
                striker2.getCircle().setDisable(true);

                // Initialize the timer label
                winText.setText("5");
                winText.setVisible(true);
                freeze1s.setDisable(true);
                freeze2s.setDisable(true);
                goal1s.setDisable(true);
                goal2s.setDisable(true);
                slowOpponentButton.setDisable(true);
                slow2s.setDisable(true);


                // Disable mouse events for the opponent striker
                striker2.getCircle().setDisable(true);
                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen_s.png"));
                ImagePattern ballPattern = new ImagePattern(ballImage);
                striker2.getCircle().setFill(ballPattern);

                // Start a timeline to unfreeze the opponent striker after 5 seconds
                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                new EventHandler<ActionEvent>() {
                                    private int timeLeft = 5;

                                    @Override
                                    public void handle(ActionEvent event) {
                                        timeLeft--;
                                        if (timeLeft > 0) {
                                            winText.setText(String.valueOf(timeLeft));
                                        }


                                        else if(score.getTime()==0)
                                        {
                                            isOpponentStrikerFrozen = false;
                                            // if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);

                                            // Enable mouse events for the opponent striker
                                            striker2.getCircle().setDisable(false);
                                            Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
                                            ImagePattern ballPattern = new ImagePattern(ballImage);
                                            striker2.getCircle().setFill(ballPattern);
                                            goalpostbarrierplay2 = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player2goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                        }
                                        else {
                                            isOpponentStrikerFrozen = false;
                                            // if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);

                                            // Enable mouse events for the opponent striker
                                            striker2.getCircle().setDisable(false);
                                            Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
                                            ImagePattern ballPattern = new ImagePattern(ballImage);
                                            striker2.getCircle().setFill(ballPattern);
                                            // Reset the flag to indicate that the opponent striker is no longer frozen
                                            isOpponentStrikerFrozen = false;

                                            // Enable mouse events for the opponent striker
                                            striker2.getCircle().setDisable(false);
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                        }
                                    }
                                }
                        )
                );
                timeline.setCycleCount(5);
                timeline.play();
            }
        }

//        if(frozen)
//        {
//            frozen=false;
//            if (!isOpponentStrikerFrozen) {
//                // Set the flag to indicate that the opponent striker is frozen
//                isOpponentStrikerFrozen = true;
//                freeze1s.setDisable(true);
//                freeze2s.setDisable(true);
//                goal1s.setDisable(true);
//                goal2s.setDisable(true);
//                slowOpponentButton.setDisable(true);
//                slow2s.setDisable(true);
//
//
//                // Disable mouse events for the opponent striker
//                striker2.getCircle().setDisable(true);
//                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen_s.png"));
//                ImagePattern ballPattern = new ImagePattern(ballImage);
//                striker2.getCircle().setFill(ballPattern);
//
//                // Start a timeline to unfreeze the opponent striker after 5 seconds
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                // Reset the flag to indicate that the opponent striker is no longer frozen
//                                isOpponentStrikerFrozen = false;
//                                // if(frozen) freeze1s.setDisable(false);
//                                if(frozenplay2) freeze2s.setDisable(false);
//                                if(goalpost) goal1s.setDisable(false);
//                                if(goalpostplay2) goal2s.setDisable(false);
//                                if(slowballbool)slowOpponentButton.setDisable(false);
//                                if (slowballboolplay2)slow2s.setDisable(false);
//
//                                // Enable mouse events for the opponent striker
//                                striker2.getCircle().setDisable(false);
//                                Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
//                                ImagePattern ballPattern = new ImagePattern(ballImage);
//                                striker2.getCircle().setFill(ballPattern);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }

    }
    @FXML
    private void handlegoalpostbarrierButtonClick(ActionEvent actionEvent) {
        if (goalpost) {
            goalpost = false;
            Media sound = new Media(getClass().getResource("/bodyfall.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> {
                // When media ends, restart it
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
            mediaPlayer.play();

            if (!goalpostbarrier) {
                goalpostbarrier = true;
                player1goalps.setStroke(Color.RED);
                player1goalps.setStroke(Color.RED);
                freeze1s.setDisable(true);
                freeze2s.setDisable(true);
                goal1s.setDisable(true);
                goal2s.setDisable(true);
                slowOpponentButton.setDisable(true);
                slow2s.setDisable(true);

                // Initialize the goalpost timer label
                winText.setText("5");
                winText.setVisible(true);

                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                new EventHandler<ActionEvent>() {
                                    private int timeLeft = 5;

                                    @Override
                                    public void handle(ActionEvent event) {
                                        timeLeft--;
                                        if (timeLeft > 0) {
                                            winText.setText(String.valueOf(timeLeft));
                                        }

                                        else if(score.getTime()==0)
                                        {
                                            goalpostbarrier = false;
                                            if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            // if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);

                                            //  player1goalps.setStroke(Color.rgb(0e,55,69));
                                            player1goalps.setStroke(Color.web("#0e5569"));
                                            goalpostbarrierplay2 = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player2goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                        }
                                        else {
                                            goalpostbarrier = false;
                                            if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            // if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);

                                            //  player1goalps.setStroke(Color.rgb(0e,55,69));
                                            player1goalps.setStroke(Color.web("#0e5569"));
                                            goalpostbarrier = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player1goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                        }
                                    }
                                }
                        )
                );
                timeline.setCycleCount(5);
                timeline.play();
            }
        }

//        if(goalpost)
//        {
//            goalpost=false;
//            if (!goalpostbarrier) {
//
//                goalpostbarrier = true;
//                player1goalps.setStroke(Color.RED);
//                freeze1s.setDisable(true);
//                freeze2s.setDisable(true);
//                goal1s.setDisable(true);
//                goal2s.setDisable(true);
//                slowOpponentButton.setDisable(true);
//                slow2s.setDisable(true);
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                goalpostbarrier = false;
//                                if(frozen) freeze1s.setDisable(false);
//                                if(frozenplay2) freeze2s.setDisable(false);
//                                // if(goalpost) goal1s.setDisable(false);
//                                if(goalpostplay2) goal2s.setDisable(false);
//                                if(slowballbool)slowOpponentButton.setDisable(false);
//                                if (slowballboolplay2)slow2s.setDisable(false);
//
//                                //  player1goalps.setStroke(Color.rgb(0e,55,69));
//                                player1goalps.setStroke(Color.web("#0e5569"));
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }

    }

    @FXML
    private void handleSlowOpponentButtonClickplay2(ActionEvent actionEvent) {

        if (slowballboolplay2) {
            slowballboolplay2 = false;
            Media sound = new Media(getClass().getResource("/slowBall.mp3").toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> {
                // When media ends, restart it
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.play();
            });
            mediaPlayer.play();
            isSlowed = true;

            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
            c.setStroke(Color.BLACK);
            Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
            ImagePattern ballPattern = new ImagePattern(ballImage);
            c.setFill(ballPattern);

            freeze1s.setDisable(true);
            freeze2s.setDisable(true);
            goal1s.setDisable(true);
            goal2s.setDisable(true);
            slowOpponentButton.setDisable(true);
            slow2s.setDisable(true);

            Random random = new Random();
            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
            ballVelocity = slowball.getInitialSpeed();

            ball.getCircle().setVisible(false);

            pane.getChildren().add(c);

            // Initialize the slow timer label
            winText.setText("5");
            winText.setVisible(true);

            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(1),
                            new EventHandler<ActionEvent>() {
                                private int timeLeft = 5;

                                @Override
                                public void handle(ActionEvent event) {
                                    timeLeft--;
                                    if (timeLeft > 0) {
                                        winText.setText(String.valueOf(timeLeft));
                                    }
                                    else if(score.getTime()==0)
                                    {
                                        ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value

                                        isSlowed = false;
                                        slowball.getCircle().setVisible(false);


                                        ball.setX(slowball.getX());
                                        ball.setY(slowball.getY());

                                        ball.getCircle().setVisible(true);

                                        if(frozen) freeze1s.setDisable(false);
                                        if(frozenplay2) freeze2s.setDisable(false);
                                        if(goalpost) goal1s.setDisable(false);
                                        if(goalpostplay2) goal2s.setDisable(false);
                                        if(slowballbool)slowOpponentButton.setDisable(false);
                                        goalpostbarrierplay2 = false;
                                        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                            mediaPlayer.stop();
                                        }
                                        player2goalps.setStroke(Color.WHITE);
                                        winText.setVisible(false);
                                        ((Timeline) event.getSource()).stop();
                                    }

                                    else {
                                        ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value

                                        isSlowed = false;
                                        slowball.getCircle().setVisible(false);


                                        ball.setX(slowball.getX());
                                        ball.setY(slowball.getY());

                                        ball.getCircle().setVisible(true);

                                        if(frozen) freeze1s.setDisable(false);
                                        if(frozenplay2) freeze2s.setDisable(false);
                                        if(goalpost) goal1s.setDisable(false);
                                        if(goalpostplay2) goal2s.setDisable(false);
                                        if(slowballbool)slowOpponentButton.setDisable(false);
                                        ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
                                        isSlowed = false;
                                        slowball.getCircle().setVisible(false);

                                        ball.setX(slowball.getX());
                                        ball.setY(slowball.getY());

                                        ball.getCircle().setVisible(true);
                                        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                            mediaPlayer.stop();
                                        }
                                        winText.setVisible(false);
                                        ((Timeline) event.getSource()).stop();
                                    }
                                }
                            }
                    )
            );
            timeline.setCycleCount(5);
            timeline.play();
        }


//        if (slowballboolplay2) {
//            slowballboolplay2=false;
//            isSlowed = true;
//
//            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//           /* c.setFill(Color.YELLOW);
//            c.setStroke(Color.WHITE);*/
//
//            c.setStroke(Color.BLACK);
//            Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
//            ImagePattern ballPattern = new ImagePattern(ballImage);
//            c.setFill(ballPattern);
//
//            freeze1s.setDisable(true);
//            freeze2s.setDisable(true);
//            goal1s.setDisable(true);
//            goal2s.setDisable(true);
//            slowOpponentButton.setDisable(true);
//            slow2s.setDisable(true);
//
//
//            Random random = new Random();
//            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
//            ballVelocity=slowball.getInitialSpeed();
//
//            ball.getCircle().setVisible(false);
//
//            pane.getChildren().add(c);
//
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(5),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
//
//                            isSlowed = false;
//                            slowball.getCircle().setVisible(false);
//
//
//                            ball.setX(slowball.getX());
//                            ball.setY(slowball.getY());
//
//                            ball.getCircle().setVisible(true);
//
//                            if(frozen) freeze1s.setDisable(false);
//                            if(frozenplay2) freeze2s.setDisable(false);
//                            if(goalpost) goal1s.setDisable(false);
//                            if(goalpostplay2) goal2s.setDisable(false);
//                            if(slowballbool)slowOpponentButton.setDisable(false);
//                            //if (slowballboolplay2)slow2s.setDisable(false);
//
//
//                            /*   Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//                            c.setFill(Color.YELLOW);
//                            c.setStroke(Color.WHITE);*/
//
//
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//        }
    }

    @FXML
    private void handlefreezeOpponentButtonClickplay2(ActionEvent actionEvent) {
        // Check if the opponent striker is not already frozen

        if (frozenplay2) {
            frozenplay2 = false;
            if (!isOpponentStrikerFrozen) {
                // Set the flag to indicate that the opponent striker is frozen
                isOpponentStrikerFrozen = true;
                freeze1s.setDisable(true);
                freeze2s.setDisable(true);
                goal1s.setDisable(true);
                goal2s.setDisable(true);
                slowOpponentButton.setDisable(true);
                slow2s.setDisable(true);
                Media sound = new Media(getClass().getResource("/icespell.mp3").toExternalForm());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setOnEndOfMedia(() -> {
                    // When media ends, restart it
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                });
                mediaPlayer.play();
                // Disable mouse events for the opponent striker
                striker1.getCircle().setDisable(true);

                // Initialize the timer label
                winText.setText("5");
                winText.setVisible(true);
                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen.png"));
                ImagePattern ballPattern = new ImagePattern(ballImage);
                striker1.getCircle().setFill(ballPattern);

                // Start a timeline to unfreeze the opponent striker after 5 seconds
                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                new EventHandler<ActionEvent>() {
                                    private int timeLeft = 5;

                                    @Override
                                    public void handle(ActionEvent event) {
                                        timeLeft--;
                                        if (timeLeft > 0) {
                                            winText.setText(String.valueOf(timeLeft));
                                        }
                                        else if(score.getTime()==0)
                                        {
                                            goalpostbarrierplay2 = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player2goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                            if(frozen) freeze1s.setDisable(false);
                                            //if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);



                                            // Enable mouse events for the opponent striker
                                            striker1.getCircle().setDisable(false);
                                            Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
                                            ImagePattern ballPattern = new ImagePattern(ballImage);
                                            striker1.getCircle().setFill(ballPattern);
                                        }

                                        else {
                                            // Reset the flag to indicate that the opponent striker is no longer frozen
                                            isOpponentStrikerFrozen = false;
                                            // Enable mouse events for the opponent striker
                                            striker1.getCircle().setDisable(false);
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                            if(frozen) freeze1s.setDisable(false);
                                            //if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);



                                            // Enable mouse events for the opponent striker
                                            striker1.getCircle().setDisable(false);
                                            Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
                                            ImagePattern ballPattern = new ImagePattern(ballImage);
                                            striker1.getCircle().setFill(ballPattern);
                                        }
                                    }
                                }
                        )
                );
                timeline.setCycleCount(5);
                timeline.play();
            }
        }

//        if(frozenplay2)
//        {
//            frozenplay2=false;
//            if (!isOpponentStrikerFrozen) {
//                // Set the flag to indicate that the opponent striker is frozen
//                isOpponentStrikerFrozen = true;
//                freeze1s.setDisable(true);
//                freeze2s.setDisable(true);
//                goal1s.setDisable(true);
//                goal2s.setDisable(true);
//                slowOpponentButton.setDisable(true);
//                slow2s.setDisable(true);
//
//                // Disable mouse events for the opponent striker
//                striker1.getCircle().setDisable(true);
//                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen.png"));
//                ImagePattern ballPattern = new ImagePattern(ballImage);
//                striker1.getCircle().setFill(ballPattern);
//
//                // Start a timeline to unfreeze the opponent striker after 5 seconds
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                // Reset the flag to indicate that the opponent striker is no longer frozen
//                                if(frozen) freeze1s.setDisable(false);
//                                //if(frozenplay2) freeze2s.setDisable(false);
//                                if(goalpost) goal1s.setDisable(false);
//                                if(goalpostplay2) goal2s.setDisable(false);
//                                if(slowballbool)slowOpponentButton.setDisable(false);
//                                if (slowballboolplay2)slow2s.setDisable(false);
//
//
//
//                                // Enable mouse events for the opponent striker
//                                striker1.getCircle().setDisable(false);
//                                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
//                                ImagePattern ballPattern = new ImagePattern(ballImage);
//                                striker1.getCircle().setFill(ballPattern);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }

    }
    @FXML
    private void handlegoalpostbarrierButtonClickplay2(ActionEvent actionEvent) {

        Media sound = new Media(getClass().getResource("/bodyfall.mp3").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setOnEndOfMedia(() -> {
            // When media ends, restart it
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });

        if (goalpostplay2) {
            goalpostplay2 = false;
            if (!goalpostbarrierplay2)
            {
                freeze1s.setDisable(true);
                freeze2s.setDisable(true);
                goal1s.setDisable(true);
                goal2s.setDisable(true);
                slowOpponentButton.setDisable(true);
                slow2s.setDisable(true);

                goalpostbarrierplay2 = true;

               // mediaPlayer.play();
                goalpostbarrierplay2 = true;
                player2goalps.setStroke(Color.RED);


                // Initialize the goalpost timer label
                winText.setText("5");
                winText.setVisible(true);

                Timeline timeline = new Timeline(
                        new KeyFrame(
                                Duration.seconds(1),
                                new EventHandler<ActionEvent>() {
                                    private int timeLeft = 5;

                                    @Override
                                    public void handle(ActionEvent event) {
                                        timeLeft--;
                                        if (timeLeft > 0) {
                                            winText.setText(String.valueOf(timeLeft));
                                        }

                                        else if(score.getTime()==0)
                                        {
                                            goalpostbarrierplay2 = false;
                                            player2goalps.setStroke(Color.web("ffc321"));
                                            if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            // if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);
                                            goalpostbarrierplay2 = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player2goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                        }

                                        else {
                                            goalpostbarrierplay2 = false;
                                            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                                                mediaPlayer.stop();
                                            }
                                            player2goalps.setStroke(Color.WHITE);
                                            winText.setVisible(false);
                                            ((Timeline) event.getSource()).stop();
                                            goalpostbarrierplay2 = false;
                                            player2goalps.setStroke(Color.web("ffc321"));
                                            if(frozen) freeze1s.setDisable(false);
                                            if(frozenplay2) freeze2s.setDisable(false);
                                            if(goalpost) goal1s.setDisable(false);
                                            // if(goalpostplay2) goal2s.setDisable(false);
                                            if(slowballbool)slowOpponentButton.setDisable(false);
                                            if (slowballboolplay2)slow2s.setDisable(false);
                                        }
                                    }
                                }
                        )
                );
                timeline.setCycleCount(5);
                timeline.play();
            }
        }

//        if(goalpostplay2)
//        {
//            goalpostplay2=false;
//            if (!goalpostbarrierplay2) {
//
//                freeze1s.setDisable(true);
//                freeze2s.setDisable(true);
//                goal1s.setDisable(true);
//                goal2s.setDisable(true);
//                slowOpponentButton.setDisable(true);
//                slow2s.setDisable(true);
//
//                goalpostbarrierplay2 = true;
//                player2goalps.setStroke(Color.RED);
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                goalpostbarrierplay2 = false;
//                                player2goalps.setStroke(Color.web("ffc321"));
//                                if(frozen) freeze1s.setDisable(false);
//                                if(frozenplay2) freeze2s.setDisable(false);
//                                if(goalpost) goal1s.setDisable(false);
//                                // if(goalpostplay2) goal2s.setDisable(false);
//                                if(slowballbool)slowOpponentButton.setDisable(false);
//                                if (slowballboolplay2)slow2s.setDisable(false);
//
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }

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

                System.out.println("x " + gridx);
                System.out.println("y " + gridy);

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

        class KeyboardControl extends Thread {
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

                if (pressedKeys.contains(KeyCode.W)) deltaY -= 1;
                if (pressedKeys.contains(KeyCode.A)) deltaX -= 1;
                if (pressedKeys.contains(KeyCode.S)) deltaY += 1;
                if (pressedKeys.contains(KeyCode.D)) deltaX += 1;

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

//package com.example.aeroblitz;
//
//import javafx.animation.*;
//import javafx.application.Platform;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.ImagePattern;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Line;
//import javafx.scene.shape.Rectangle;
//import javafx.util.Duration;
//import javafx.scene.image.Image;
//
//import java.util.*;
//
//public class Board
//{
//
//
//    @FXML
//    private AnchorPane pane;
//
//    @FXML
//    private Rectangle boardneww;
//
//    private double prevxs1,prevys1,prevxs2,prevys2;
//    private boolean frozen=true,slowballbool=true,goalpost=true;
//
//    private boolean frozenplay2 = true, slowballboolplay2 = true, goalpostplay2 = true;
//
//    @FXML
//    private Rectangle player1goalps, player2goalps;
//
//    @FXML
//    private Button slowOpponentButton;
//    private boolean isOpponentStrikerFrozen = false;
//
//
//    private boolean blastEffectActive = false;
//
//    private long lastCollisionTime = 0;
//
//    private long lastUpdateTime = 0;
//    private static final long COLLISION_COOLDOWN = 500;
//
//    private final int GAME_WIDTH = 800; //adjust the size of the board
//    private final int GAME_HEIGHT = 600;
//    private final int BALL_DIAMETER = 20; //adjust the size of the ball
//
//
//    private final Set<KeyCode> pressedKeys = new HashSet<>(); // for keyboard movements in striker2
//
//    private double STRIKER_SPEED = 2; // speed of the striker2
//    private double goalpos = 230;
//
//    private Striker striker1;
//    private Striker striker2;
//    private Ball ball;
//    private slow_ball slowball;
//
//    private double ballVelocity; // Initial ball velocity
//    private int slowVelocity = 2;
//    private boolean isSlowed = false;
//    private int strikerSize = 100; // adjust the size of the striker, indicates the diameter
//    private double strokeWidth = 0;
//
//    private int MAX_BOUNCE_SPEED = 2; // determines the speed of the ball when the striker hits it
//
//
//    private double startX; // initial position of the mouse when it is pressed
//    private double startY;
//    private double startTime; // initial time of the mouse when it is pressed
//
//    private Canvas canvas;
//    private GraphicsContext gc, gc1;
//    private Score score;
//
//    private ttimer __timer;
//
////    private final ScaleTransition blastTransition = new ScaleTransition(Duration.seconds(0.3));
//
//    private double blastoffset = 5;
//
//
//    //for motiontrail on the ball
//
//    private final List<Line> motionTrail = new ArrayList<>();
//    private final int maxTrailLength = 20; // Adjust the maximum length of the motion trail as needed
//
//
//    // score label
//    @FXML
//    private Label winText;
//
//    private boolean gameEnded = false;
//
//    private boolean goalpostbarrier = false;
//    private boolean goalpostbarrierplay2 = false;
//
//    private final Database_Connection database = new Database_Connection();
//
//
//
//    private final AnimationTimer gameLoop = new AnimationTimer() {
//        private static final int FPS = 120; // Desired frames per second
//        private static final long ONE_SECOND_NANOS = 1_000_000_000L;
//        private static final long FRAME_INTERVAL_NANOS = ONE_SECOND_NANOS / FPS;
//        private long lastUpdateTime = 0;
//
//        @Override
//        public void handle(long now) {
//            if (lastUpdateTime == 0) {
//                lastUpdateTime = now;
//                return;
//            }
//
//            long elapsedNanos = now - lastUpdateTime;
//            if (elapsedNanos >= FRAME_INTERVAL_NANOS) {
//                lastUpdateTime = now;
//                updateGame();
//            }
//        }
//
//        private void updateGame()
//        {
//            drawTrail();
//
//            if (ballVelocity == slowball.getInitialSpeed()) {
//                slowball.move();
//                slowball.draw();
//                updateMotionTrail(ball);
//                checkCollisionslow();
//
//                System.out.println("time " + score.getTime());
//
//
//                if (score.getTime() == 0)
//                {
//                    gameEnded = true;
//
//                    frozen = true;
//                    frozenplay2 = true;
//                    slowballbool = true;
//                    slowballboolplay2 = true;
//
//                    goalpost = true;
//                    goalpostplay2 = true;
//                    goalpostbarrier = false;
//                    isSlowed = false;
//                    isOpponentStrikerFrozen = false;
//                    player1goalps.setStroke(Color.WHITE);
//                    slowball.getCircle().setVisible(false);
//
//                    ball.setX(slowball.getX());
//                    ball.setY(slowball.getY());
//
//                    ball.getCircle().setVisible(true);
//                    gameLoop.stop();
//                    score.pauseTimer();
//
//                    if (score.player1 > score.player2)
//                    {
//                        winText.setText(String.format("Player 1 wins!!"));
//                        winText.setVisible(true); // Hide the win text
//                    } else if (score.player2 > score.player1)
//                    {
//                        winText.setText(String.format("Player 2 wins!!"));
//                        winText.setVisible(true); // Hide the win text
//                    }
//                    else
//                    {
//                        winText.setText(String.format("Its a Draw!!"));
//                        winText.setVisible(true); // Hide the win text
//                    }
//
//
//                    database.saveScore("Player1", score.player1);
//                    database.saveScore("Player2", score.player2);
//
//                    System.out.println(score.player1);
//                    System.out.println(score.player2);
//                }
//
//            }
//            else
//            {
//                ball.getCircle().setVisible(true);
//                ball.move();
//                ball.draw();
//                updateMotionTrail(ball);
//                checkCollision();
//
//                System.out.println("time " + score.getTime());
//
//
//                if (score.getTime() == 0)
//                {
//                    gameEnded = true;
//                    goalpostbarrier = false;
//                    isSlowed = false;
//                    isOpponentStrikerFrozen = false;
//                    striker2.getCircle().setDisable(false);
//                    player1goalps.setStroke(Color.WHITE);
//                    slowball.getCircle().setVisible(false);
//
//                    ball.setX(slowball.getX());
//                    ball.setY(slowball.getY());
//
//                    ball.getCircle().setVisible(true);
//                    gameLoop.stop();
//                    score.pauseTimer();
//
//                    if (score.player1 > score.player2)
//                    {
//                        winText.setText(String.format("Player 1 wins!!"));
//                        winText.setVisible(true); // Hide the win text
//                    } else if (score.player2 > score.player1)
//                    {
//                        winText.setText(String.format("Player 2 wins!!"));
//                        winText.setVisible(true); // Hide the win text
//                    } else
//                    {
//                        winText.setText(String.format("Its a Draw!!"));
//                        winText.setVisible(true); // Hide the win text
//                    }
//
//
//                    database.saveScore("Player_green", score.player1);
//                    database.saveScore("Player_blue", score.player2);
//
////                score.setTime(30);
////                resetPositions();
////                draw_scoreboard();
//                    System.out.println(score.player1);
//                    System.out.println(score.player2);
//                }
//            }
//        }
//
//        private void endGame()
//        {
//            gameEnded = true;
//            gameLoop.stop();
//            score.pauseTimer();
//
//            if (score.player1 > score.player2) {
//                winText.setText("Player 1 wins!!");
//                winText.setVisible(true);
//            } else if (score.player2 > score.player1) {
//                winText.setText("Player 2 wins!!");
//                winText.setVisible(true);
//            } else {
//                winText.setText("It's a Draw!!");
//                winText.setVisible(true);
//            }
//
//            System.out.println(score.player1);
//            System.out.println(score.player2);
//        }
//    };
//
//    @FXML
//    public void initialize()
//    {
//
//        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT); // Adjust the size as needed
//
//        lastUpdateTime = System.nanoTime();
//        Image ballImage = new Image(getClass().getResourceAsStream("/board.png"));
//        ImagePattern ballPattern = new ImagePattern(ballImage);
//        boardneww.setFill(ballPattern);
//
//
//
//        gc = canvas.getGraphicsContext2D();
//        gc1 = canvas.getGraphicsContext2D();
//
//        pane.getChildren().add(canvas);
//
//        score = new Score(GAME_WIDTH, GAME_HEIGHT); // Pass the dimensions of your game window
//        score.setPlayerScores(0, 0); // Set initial scores
//
//        draw_scoreboard();
//
//        // Initialize the strikers
//        striker1 = new Striker(); // Initialize with appropriate values
//        striker2 = new Striker(); // Initialize with appropriate values
//
//
//        // Initialize the board
//        newStriker(100, 300, Color.RED, 1, striker1); // initial position of the strikers in the board
//        newStriker(700, 300, Color.GREEN, 2, striker2);
//        prevxs1 = 100;
//        prevys1 = 300;
//        prevxs2 = 700;
//        prevys2 = 300;
//
//        newBall();
//        newslowBall();// initial position of the ball, it will start from any random position in the middle line of the board
//        ballVelocity = ball.getspeed();
//
//        // Start the game loop
//        gameLoop.start();
//    }
//
//
//    //motion trail is turned off for now
//
//    private void drawTrail() {
//
//    }
//
//    private void perturbDirection(Ball ball) {
//        double speed = Math.sqrt(ball.getXVelocity() * ball.getXVelocity() + ball.getYVelocity() * ball.getYVelocity());
//        double angle = Math.atan2(ball.getYVelocity(), ball.getXVelocity());
//        double perturbationAngle = (Math.random() - 0.5) * 0.1; // Small perturbation angle
//
//        angle += perturbationAngle;
//
//        double newBallXVelocity = Math.cos(angle) * speed;
//        double newBallYVelocity = Math.sin(angle) * speed;
//
//        ball.setXDirection(newBallXVelocity);
//        ball.setYDirection(newBallYVelocity);
//    }
//
//    private void perturbDirectionslow(slow_ball ball) {
//        double speed = Math.sqrt(ball.getXVelocity() * ball.getXVelocity() + ball.getYVelocity() * ball.getYVelocity());
//        double angle = Math.atan2(ball.getYVelocity(), ball.getXVelocity());
//        double perturbationAngle = (Math.random() - 0.5) * 0.1; // Small perturbation angle
//
//        angle += perturbationAngle;
//
//        double newBallXVelocity = Math.cos(angle) * speed;
//        double newBallYVelocity = Math.sin(angle) * speed;
//
//        ball.setXDirection(newBallXVelocity);
//        ball.setYDirection(newBallYVelocity);
//    }
//
//
//    private void updateMotionTrail(Ball ball) {
//
//
//    }
//
//    private void draw_scoreboard() {
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
//        score.draw(gc); // Draw the score
//    }
//
//    public void newBall() {
//
//        Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//        c.setFill(Color.YELLOW);
//        c.setStroke(Color.BLACK);
//      //  Image ballImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("src/main/resources/com/example/aeroblitz/ball.jpg")));
//       // Image ballImage = new Image("src/main/resources/com/example/aeroblitz/ball.jpg"); // Update the path to your image file
//        Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
//        ImagePattern ballPattern = new ImagePattern(ballImage);
//        c.setFill(ballPattern);
//
//        Random random = new Random();
//
//        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2),(GAME_HEIGHT/2) - (BALL_DIAMETER/2)-16, BALL_DIAMETER, c);
//      //  ball=new Ball(GAME_WIDTH,GAME_HEIGHT-60,BALL_DIAMETER,c);
//
//        pane.getChildren().add(c);
//        ball.draw();
//
//    }
//
//    public void update() {
//        long now = System.nanoTime();
//        double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
//        lastUpdateTime = now;
//
//        ball.update(deltaTime);
//     /*   striker1.updatePosition(deltaTime);
//        striker2.updatePosition(deltaTime);*/
//
//        checkCollision();
//
//
//    }
//
//    public void newslowBall() {
//
//        Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//
//        c.setStroke(Color.BLACK);
//        Image ballImage = new Image(getClass().getResourceAsStream("/ballred.jpg"));
//        ImagePattern ballPattern = new ImagePattern(ballImage);
//        c.setFill(ballPattern);
//
//        Random random = new Random();
//        slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
//        pane.getChildren().add(c);
//
//
//    }
//
//    public void newStriker(int x, int y, Color color, int id, Striker striker) {
//        Circle c = new Circle();
//        Color neonColor = color.brighter().brighter().brighter();
//        c.setFill(Color.WHITE);
//        //c.setStroke(neonColor);
//
//
//        // Set the stroke width
//        strokeWidth = 15.0; // Change this value as needed
//        Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
//        ImagePattern ballPattern = new ImagePattern(ballImage);
//        c.setFill(ballPattern);
//       // c.setStrokeWidth(strokeWidth);
//
//
//
//       // double radius = strikerSize / 3.0;
//      //  double radius=strikerSize/3.0+15;
//        double radius=48.33;
//
//
//        striker.setID(id);
//        striker.setX(x);
//        striker.setY(y);
//        striker.setColor(color);
//        striker.setRadius(radius);
//        striker.setCircle(c); // Assign the Circle object to the Striker
//        pane.getChildren().add(c);
//
//        striker.draw(); //draw the striker
//
//        if (striker.getID() == 2)
//        {
////        if(true) {
//
////            // Setting mouse control
////            c.setOnMousePressed(event -> pressed(event, striker));
////            c.setOnMouseDragged(event -> dragged(event, striker));
////            c.setOnMouseReleased(event -> released(event, striker));
//
////            MouseControl m1 = new MouseControl(c, striker);
//            MouseControl m1 = new MouseControl(striker2, c);
//
//            m1.start();
//        }
//        else
//        {
//            KeyboardControl k1 = new KeyboardControl(striker, pane);
//
////            // Setting keyboard control
////            pane.setOnKeyPressed(event -> keyPressed(event, striker));
////            pane.requestFocus(); // Ensure the pane has focus to receive key events
//
////            KeyboardControl k1 = new KeyboardControl();
//
//            k1.start();
//
//        }
//    }
//
//
//    public void newStriker2(int x, int y, Color color, int id, Striker striker)
//    {
//        Circle c = new Circle();
//        Color neonColor = color.brighter().brighter().brighter();
//        c.setFill(Color.WHITE);
//       // c.setStroke(neonColor);
//
//
//        // Set the stroke width
//        strokeWidth = 15.0; // Change this value as needed
//        Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
//        ImagePattern ballPattern = new ImagePattern(ballImage);
//        c.setFill(ballPattern);
//      //  c.setStrokeWidth(strokeWidth);
//
//       // double radius = strikerSize / 3.0;
//
//        double radius=48.33;
//
//        striker.setID(id);
//        striker.setX(x);
//        striker.setY(y);
//        striker.setColor(color);
//        striker.setRadius(radius);
//        striker.setCircle(c); // Assign the Circle object to the Striker
//        pane.getChildren().add(c);
//
//        striker.draw(); //draw the striker
//
////        if(striker.getID() == 2) {
//        if(true) {
//
//            // Setting mouse control
//            // c.setOnMousePressed(event -> pressed(event, striker));
//            c.setOnMouseDragged(event -> draggeds2(event, striker));
//            c.setOnMouseReleased(event -> released(event, striker));
//        }
//        else
//        {
//            KeyboardControl k1 = new KeyboardControl(striker, pane);
//
////            // Setting keyboard control
////            pane.setOnKeyPressed(event -> keyPressed(event, striker));
////            pane.requestFocus(); // Ensure the pane has focus to receive key events
//
////            KeyboardControl k1 = new KeyboardControl();
//
//            k1.start();
//
//        }
//    }
//
//    //Collision physics
//
//    public void checkCollisionslow()
//    {
//
//        int diameter = strikerSize / 2;
//        int ball_radius = BALL_DIAMETER / 2;
//        int rectWidth = 50;
//        int rectHeight = 150;
//        int rectX = 0; // Positioned on the left wall
//        int rectx2 = rectX + GAME_WIDTH;
//        int rectY = (GAME_HEIGHT - rectHeight) / 2;
//        long currentTime = System.currentTimeMillis();
//
//
//        // left side Boundary checking for striker1
//        if (striker1.getX() <= diameter) {
//            striker1.setX(diameter);
//            striker1.draw();
//        }
//
//        if (striker1.getX() >= (GAME_WIDTH / 2 - (diameter))) {
//            striker1.setX(GAME_WIDTH / 2 - (diameter));
//            striker1.draw();
//        }
//
//        if (striker1.getY() <= diameter) {
//            striker1.setY(diameter);
//            striker1.draw();
//        }
//
//        if (striker1.getY() >= (GAME_HEIGHT - (diameter))) {
//            striker1.setY(GAME_HEIGHT - (diameter));
//            striker1.draw();
//        }
//
//
//        // Boundary checking for striker2
//        if (striker2.getX() <= GAME_WIDTH / 2 + diameter) {
//            striker2.setX(GAME_WIDTH / 2 + diameter);
//            striker2.draw();
//        }
//
//        if (striker2.getX() >= (GAME_WIDTH - (diameter))) {
//            striker2.setX(GAME_WIDTH - (diameter));
//            striker2.draw();
//        }
//
//        if (striker2.getY() <= diameter) {
//            striker2.setY(diameter);
//            striker2.draw();
//        }
//
//        if (striker2.getY() >= (GAME_HEIGHT - (diameter))) {
//            striker2.setY(GAME_HEIGHT - (diameter));
//            striker2.draw();
//        }
//        if (slowball.getX() - ball_radius <= rectX + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrier) {
//            // Check collision with the top side of the rectangle
//            if (slowball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//
//                slowball.setYDirection(-slowball.getYVelocity());
//                slowball.setY(rectY - ball_radius);
//            }
//            // Check collision with the bottom side of the rectangle
//            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setYDirection(-slowball.getYVelocity());
//                slowball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle
//
//            }
//        } else if (slowball.getX() - ball_radius >= rectx2 - rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrierplay2) {
//            // Check collision with the top side of the rectangle
//            if (slowball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//
//                slowball.setYDirection(-slowball.getYVelocity());
//                slowball.setY(rectY - ball_radius);
//            }
//            // Check collision with the bottom side of the rectangle
//            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setYDirection(-slowball.getYVelocity());
//                slowball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle
//
//            }
//        } else if (slowball.getX() >= (GAME_WIDTH - 36 - 30) && slowball.getY() >= (goalpos + BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrierplay2) {
//
//            score.player1++;
//            resetPositions();
//            draw_scoreboard();
//            gameLoop.stop();
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player2);
//
//        } else if (slowball.getX() <= 36 + 30 && slowball.getY() >= (goalpos + BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier && true) {
//
//            score.player2++;
//            resetPositions();
//            draw_scoreboard();
//            gameLoop.stop();
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player1);
//
//
//        }
//
//
//        if (slowball.getX() - ball_radius <= rectX + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier) {
//            // Check collision with the top side of the rectangle
//            if (slowball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setYDirection(-slowball.getYVelocity());
//            }
//            // Check collision with the bottom side of the rectangle
//            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setYDirection(-ball.getYVelocity());
//            }
//            // Check collision with the right side of the rectangle
//            if (slowball.getX() - ball_radius <= rectX + rectWidth) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                slowball.setX(rectX + rectWidth + ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setXDirection(-slowball.getXVelocity());
//            }
//        }
//
//
//        if (slowball.getX() - ball_radius >= rectx2 + rectWidth && slowball.getY() + ball_radius >= rectY && slowball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrierplay2) {
//            // Check collision with the top side of the rectangle
//            if (slowball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setYDirection(-slowball.getYVelocity());
//            }
//            // Check collision with the bottom side of the rectangle
//            if (slowball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                slowball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setYDirection(-slowball.getYVelocity());
//            }
//            // Check collision with the right side of the rectangle
//            if (slowball.getX() - ball_radius >= rectx2 - rectWidth) {
//                BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                slowball.setX(rectx2 - rectWidth - ball_radius); // Prevent the ball from entering the rectangle
//                slowball.setXDirection(-slowball.getXVelocity());
//            }
//        }
//
//
//        if (slowball.getY() <= 32 || slowball.getY() >= (GAME_HEIGHT - 50)) {
//            slowball.setYDirection(-slowball.getYVelocity());
//            perturbDirectionslow(slowball);
//            //  ball.setXDirection(ball.getXVelocity() + (Math.random() - 0.5) * 0.1);
//            if (slowball.getYVelocity() == 0 || slowball.getXVelocity() == 0) {
//                Random random = new Random();
//                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
//                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
//                slowball.setXDirection(randomXDirection * slowball.getInitialSpeed());
//                slowball.setYDirection(randomYDirection * slowball.getInitialSpeed());
//                perturbDirectionslow(slowball);
//            }
//
//        }
//        if (slowball.getX() <= 36 || slowball.getX() >= (GAME_WIDTH - 36)) {
//            slowball.setXDirection(-slowball.getXVelocity());
//            perturbDirectionslow(slowball);
//            //  ball.setYDirection(ball.getYVelocity() + (Math.random() - 0.5) * 0.1);
//
//            if (slowball.getYVelocity() == 0 || slowball.getXVelocity() == 0) {
//                Random random = new Random();
//                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
//                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
//                slowball.setXDirection(randomXDirection * slowball.getInitialSpeed());
//                slowball.setYDirection(randomYDirection * slowball.getInitialSpeed());
//                perturbDirectionslow(slowball);
//            }
//        }
//
//        // Collisions with the strikers
//        else if (intersectsslow(slowball, striker1) || intersectsslow(slowball, striker2)) {
//
//            BlastEffect blastEffect1 = new BlastEffect(slowball.getX() - blastoffset, slowball.getY() + blastoffset, Color.rgb(104, 110, 148));
//
//            pane.getChildren().add(blastEffect1);
//            blastEffectActive = true;
//
//            // Reset the flag after a certain duration
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                blastEffectActive = false;
//                pane.getChildren().remove(blastEffect1);
//            }));
//            timeline.setCycleCount(1);
//            timeline.play();
//
//
//            double relativeCollisionX = slowball.getX() - striker1.getX();
//            double relativeCollisionY = slowball.getY() - striker1.getY();
//
//            // Calculate the angle of collision
//            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//
//            // Determine the direction of the striker's movement
//            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//
//            // Calculate the angle between the striker's movement direction and the collision angle
//            double angleDifference = collisionAngle - strikerMovementDirection;
//            double randomAngleOffset = (Math.random() - 0.5) * 0.1;
//            angleDifference = (angleDifference + randomAngleOffset);
//
//            // Adjust the direction of the ball based on the angle difference
//            double newBallXVelocity = Math.cos(angleDifference) * slowball.getXVelocity() - Math.sin(angleDifference) * slowball.getYVelocity();
//            double newBallYVelocity = Math.sin(angleDifference) * slowball.getXVelocity() + Math.cos(angleDifference) * slowball.getYVelocity();
//
//            double newBallX = slowball.getX() + newBallXVelocity;
//            double newBallY = slowball.getY() + newBallYVelocity;
//            Random random = new Random();
//            int i = random.nextInt(5);
//
//
//            //top edge or left edge
//            if (newBallX <= 36 || newBallY <= 32) {
//                if (newBallX <= 36 && newBallY > 32) {
//                    newBallX += 50;
//                    if (newBallX <= 36) {
//                        //newBallX -= strikerMovementDirection*5;
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX > 36 && newBallY <= 32) {
//                    /*newBallY += newBallYVelocity;*/
//                    newBallY += 50;
//                    while (newBallY <= 32) {
//                        newBallY -= 2 * 050;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= 36 && newBallY <= 32) {
//                        newBallX -= 2 * 50;
//                    }
//                    // newBallY += newBallYVelocity;
//                    newBallY += 50;
//                    while (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            } else if (newBallX <= 36 || newBallY >= (GAME_HEIGHT - 50)) {
//                if (newBallX <= 36 && newBallY < (GAME_HEIGHT - 50)) {
//                    newBallX += 50;
//                    if (newBallX <= 36) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX > 36 && newBallY >= (GAME_HEIGHT - 50)) {
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= 36 && newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallX -= 50 * 2;
//                    }
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 50 * 2;
//                    }
//                }
//
//
//            } else if (newBallX >= (GAME_WIDTH - 36) || newBallY <= 32) {
//                if (newBallX >= (GAME_WIDTH - 36) && newBallY > 32) {
//                    newBallX += 50;
//                    if (newBallX >= (GAME_WIDTH - 36)) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX < (GAME_WIDTH - 36) && newBallY <= 32) {
//                    newBallY += 50;
//                    if (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX >= (GAME_WIDTH - 36) && newBallY <= 32) {
//                        newBallX -= 2 * 50;
//                    }
//                    newBallY += 50;
//                    if (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            } else if (newBallX >= (GAME_WIDTH - 36) || newBallY >= (GAME_HEIGHT - 50)) {
//                if (newBallX >= (GAME_WIDTH - 36) && newBallY < (GAME_HEIGHT - 50)) {
//                    newBallX += 50;
//                    if (newBallX <= (GAME_WIDTH - 36)) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX < (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallX -= 2 * 50;
//                    }
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            }
//
//
//            double perturbation = 0.1;  // Adjust this value as necessary
//            newBallXVelocity += (Math.random() - 0.5) * perturbation;
//            newBallYVelocity += (Math.random() - 0.5) * perturbation;
//
//
//            slowball.setX(newBallX);
//            slowball.setY(newBallY);
//            // Update the ball's velocity
//            slowball.setXDirection(newBallXVelocity);
//            slowball.setYDirection(newBallYVelocity);
//
//
//        }
////        }
//
//    }
//
//    public void checkCollision() {
//
//
//        int diameter = strikerSize / 2;
//        int ball_radius = BALL_DIAMETER / 2;
//        int rectWidth = 50;
//        int rectHeight = 150;
//        int rectX = 0; // Positioned on the left wall
//        int rectx2 = rectX + GAME_WIDTH;
//        int rectY = (GAME_HEIGHT - rectHeight) / 2;
//        long currentTime = System.currentTimeMillis();
//
//
//        // left side Boundary checking for striker1
//        if (striker1.getX() <= diameter) {
//            striker1.setX(diameter);
//            striker1.draw();
//        }
//
//        if (striker1.getX() >= (GAME_WIDTH / 2 - (diameter))) {
//            striker1.setX(GAME_WIDTH / 2 - (diameter));
//            striker1.draw();
//        }
//
//        if (striker1.getY() <= diameter) {
//            striker1.setY(diameter);
//            striker1.draw();
//        }
//
//        if (striker1.getY() >= (GAME_HEIGHT - (diameter))) {
//            striker1.setY(GAME_HEIGHT - (diameter));
//            striker1.draw();
//        }
//
//
//        // Boundary checking for striker2
//        if (striker2.getX() <= GAME_WIDTH / 2 + diameter) {
//            striker2.setX(GAME_WIDTH / 2 + diameter);
//            striker2.draw();
//        }
//
//        if (striker2.getX() >= (GAME_WIDTH - (diameter))) {
//            striker2.setX(GAME_WIDTH - (diameter));
//            striker2.draw();
//        }
//
//        if (striker2.getY() <= diameter) {
//            striker2.setY(diameter);
//            striker2.draw();
//        }
//
//        if (striker2.getY() >= (GAME_HEIGHT - (diameter))) {
//            striker2.setY(GAME_HEIGHT - (diameter));
//            striker2.draw();
//        }
//        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrier) {
//            // Check collision with the top side of the rectangle
//            if (ball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//
//                ball.setYDirection(-ball.getYVelocity());
//                ball.setY(rectY - ball_radius);
//            }
//            // Check collision with the bottom side of the rectangle
//            if (ball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setYDirection(-ball.getYVelocity());
//                ball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle
//
//            }
//        } else if (ball.getX() - ball_radius >= rectx2 - rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && !goalpostbarrierplay2) {
//            // Check collision with the top side of the rectangle
//            if (ball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//
//                ball.setYDirection(-ball.getYVelocity());
//                ball.setY(rectY - ball_radius);
//            }
//            // Check collision with the bottom side of the rectangle
//            if (ball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setYDirection(-ball.getYVelocity());
//                ball.setY(rectY + rectHeight + ball_radius); // Prevent the ball from entering the rectangle
//
//            }
//        } else if (ball.getX() >= (GAME_WIDTH - 36 - 30) && ball.getY() >= (goalpos + BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrierplay2) {
//
//            score.player1++;
//            resetPositions();
//            draw_scoreboard();
//            gameLoop.stop();
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player2);
//
//        } else if (ball.getX() <= 36 + 30 && /*ball.getY() >= (220+ BALL_DIAMETER) && ball.getY() <= (360) &&*/ ball.getY() >= (goalpos + BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier && true) {
//
//
//            score.player2++;
//
//
//            resetPositions();
//            draw_scoreboard();
//            gameLoop.stop();
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(2),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            gameLoop.start();
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//            System.out.println(score.player1);
//
//
//        }
//
//
//        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier) {
//            // Check collision with the top side of the rectangle
//            if (ball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
//                ball.setYDirection(-ball.getYVelocity());
//            }
//            // Check collision with the bottom side of the rectangle
//            if (ball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
//                ball.setYDirection(-ball.getYVelocity());
//            }
//            // Check collision with the right side of the rectangle
//            if (ball.getX() - ball_radius <= rectX + rectWidth) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                ball.setX(rectX + rectWidth + ball_radius); // Prevent the ball from entering the rectangle
//                ball.setXDirection(-ball.getXVelocity());
//            }
//        }
//
//
//        if (ball.getX() - ball_radius >= rectx2 + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrierplay2) {
//            // Check collision with the top side of the rectangle
//            if (ball.getY() - ball_radius <= rectY) {
//                lastCollisionTime = currentTime;
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setY(rectY + ball_radius); // Prevent the ball from entering the rectangle
//                ball.setYDirection(-ball.getYVelocity());
//            }
//            // Check collision with the bottom side of the rectangle
//            if (ball.getY() + ball_radius >= rectY + rectHeight) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                blastEffectActive = true;
//
//                // Reset the flag after a certain duration
//                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                    blastEffectActive = false;
//                    pane.getChildren().remove(blastEffect1);
//                }));
//                timeline.setCycleCount(1);
//                timeline.play();
//                ball.setY(rectY + rectHeight - ball_radius); // Prevent the ball from entering the rectangle
//                ball.setYDirection(-ball.getYVelocity());
//            }
//            // Check collision with the right side of the rectangle
//            if (ball.getX() - ball_radius >= rectx2 - rectWidth) {
//                BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//                pane.getChildren().add(blastEffect1);
//                ball.setX(rectx2 - rectWidth - ball_radius); // Prevent the ball from entering the rectangle
//                ball.setXDirection(-ball.getXVelocity());
//            }
//        }
//
//        if (ball.getY() <= 32 || ball.getY() >= (GAME_HEIGHT - 50)) {
//            ball.setYDirection(-ball.getYVelocity());
//            perturbDirection(ball);
//            //  ball.setXDirection(ball.getXVelocity() + (Math.random() - 0.5) * 0.1);
//            if (ball.getYVelocity() == 0 || ball.getXVelocity() == 0) {
//                Random random = new Random();
//                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
//                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
//                ball.setXDirection(randomXDirection * ball.getspeed());
//                ball.setYDirection(randomYDirection * ball.getspeed());
//            }
//
//        }
//        if (ball.getX() <= 36 || ball.getX() >= (GAME_WIDTH - 36)) {
//            ball.setXDirection(-ball.getXVelocity());
//            perturbDirection(ball);
//            //  ball.setYDirection(ball.getYVelocity() + (Math.random() - 0.5) * 0.1);
//
//            if (ball.getYVelocity() == 0 || ball.getXVelocity() == 0) {
//                Random random = new Random();
//                int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
//                int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
//                ball.setXDirection(randomXDirection * ball.getspeed());
//                ball.setYDirection(randomYDirection * ball.getspeed());
//            }
//        }
//
//
//        // Collisions with the strikers
//        else if (intersects(ball, striker1) || intersectss2(ball, striker2)) {
//
//
//            BlastEffect blastEffect1 = new BlastEffect(ball.getX() - blastoffset, ball.getY() + blastoffset, Color.rgb(104, 110, 148));
////
//
//
//            pane.getChildren().add(blastEffect1);
//            blastEffectActive = true;
//
//            // Reset the flag after a certain duration
//            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
//                blastEffectActive = false;
//                pane.getChildren().remove(blastEffect1);
//            }));
//            timeline.setCycleCount(1);
//            timeline.play();
//
//
//            double relativeCollisionX = ball.getX() - striker1.getX();
//            double relativeCollisionY = ball.getY() - striker1.getY();
//
//            // Calculate the angle of collision
//            double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//
//            // Determine the direction of the striker's movement
//            double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//
//            // Calculate the angle between the striker's movement direction and the collision angle
//            double angleDifference = collisionAngle - strikerMovementDirection;
//            double randomAngleOffset = (Math.random() - 0.5) * 0.1;
//            angleDifference = (angleDifference + randomAngleOffset);
//
//            // Adjust the direction of the ball based on the angle difference
//            double newBallXVelocity = Math.cos(angleDifference) * ball.getXVelocity() - Math.sin(angleDifference) * ball.getYVelocity();
//            double newBallYVelocity = Math.sin(angleDifference) * ball.getXVelocity() + Math.cos(angleDifference) * ball.getYVelocity();
//
//            double newBallX = ball.getX() + newBallXVelocity;
//            double newBallY = ball.getY() + newBallYVelocity;
//            Random random = new Random();
//            int i = random.nextInt(5);
//
//
//            //top edge or left edge
//            if (newBallX <= 36 || newBallY <= 32) {
//                if (newBallX <= 36 && newBallY > 32) {
//                    newBallX += 50;
//                    if (newBallX <= 36) {
//                        //newBallX -= strikerMovementDirection*5;
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX > 36 && newBallY <= 32) {
//                    /*newBallY += newBallYVelocity;*/
//                    newBallY += 50;
//                    while (newBallY <= 32) {
//                        newBallY -= 2 * 050;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= 36 && newBallY <= 32) {
//                        newBallX -= 2 * 50;
//                    }
//                    // newBallY += newBallYVelocity;
//                    newBallY += 50;
//                    while (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            } else if (newBallX <= 36 || newBallY >= (GAME_HEIGHT - 50)) {
//                if (newBallX <= 36 && newBallY < (GAME_HEIGHT - 50)) {
//                    newBallX += 50;
//                    if (newBallX <= 36) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX > 36 && newBallY >= (GAME_HEIGHT - 50)) {
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= 36 && newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallX -= 50 * 2;
//                    }
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 50 * 2;
//                    }
//                }
//
//
//            } else if (newBallX >= (GAME_WIDTH - 36) || newBallY <= 32) {
//                if (newBallX >= (GAME_WIDTH - 36) && newBallY > 32) {
//                    newBallX += 50;
//                    if (newBallX >= (GAME_WIDTH - 36)) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX < (GAME_WIDTH - 36) && newBallY <= 32) {
//                    newBallY += 50;
//                    if (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                } else {
//                    newBallX += 50;
//                    if (newBallX >= (GAME_WIDTH - 36) && newBallY <= 32) {
//                        newBallX -= 2 * 50;
//                    }
//                    newBallY += 50;
//                    if (newBallY <= 32) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            } else if (newBallX >= (GAME_WIDTH - 36) || newBallY >= (GAME_HEIGHT - 50)) {
//                if (newBallX >= (GAME_WIDTH - 36) && newBallY < (GAME_HEIGHT - 50)) {
//                    newBallX += 50;
//                    if (newBallX <= (GAME_WIDTH - 36)) {
//                        newBallX -= 2 * 50;
//                    }
//                } else if (newBallX < (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//
//                } else {
//                    newBallX += 50;
//                    if (newBallX <= (GAME_WIDTH - 36) && newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallX -= 2 * 50;
//                    }
//                    newBallY += 50;
//                    if (newBallY >= (GAME_HEIGHT - 50)) {
//                        newBallY -= 2 * 50;
//                    }
//                }
//
//
//            }
//
//
//            double perturbation = 0.1;  // Adjust this value as necessary
//            newBallXVelocity += (Math.random() - 0.5) * perturbation;
//            newBallYVelocity += (Math.random() - 0.5) * perturbation;
//
//
//            ball.setX(newBallX);
//            ball.setY(newBallY);
//            // Update the ball's velocity
//            ball.setXDirection(newBallXVelocity);
//            ball.setYDirection(newBallYVelocity);
//
//
//        }
//
//
//    }
//
//
//    // game restart after winning 1
//    @FXML
//    private void restartGame(MouseEvent event) {
//        if (gameEnded) {
////             Reset the game state
//            goalpostbarrier = false;
//            isSlowed = false;
//            isOpponentStrikerFrozen = false;
//            score.setPlayerScores(0, 0); // Reset scores
//            resetPositions(); // Reset positions of the ball and strikers
//            draw_scoreboard();
//            winText.setVisible(false); // Hide the win text
//            score.setPlayerScores(0, 0);
//            score.setTime(30);
//
//
//            // Restart the game loop
//            gameLoop.start();
//            score.startTimer(gc);
//
//            // Reset the game ended flag
//            gameEnded = false;
//        }
//    }
//
//
//    private void resetPositions()
//    {
//        // Remove previously drawn ball and strikers from the pane
//        pane.getChildren().remove(ball.getCircle());
//        pane.getChildren().remove(striker1.getCircle());
//        pane.getChildren().remove(striker2.getCircle());
//
//        // Reset positions for striker1 and striker2
//
//        newStriker(100, 300, Color.RED,1, striker1); // initial position of the strikers in the board
//        newStriker2(700, 300, Color.GREEN,2, striker2);
//
//        // Reset ball position
//        newBall();
//    }
//
//    private boolean intersects(Ball ball, Striker striker) {
//
//        Circle ballCircle = ball.getCircle();
//        Circle strikerCircle = striker.getCircle();
//        boolean result;
//        result = getDistance(striker.getX(), striker.getY(), ball.getX(), ball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(), 2);
//        double gridx = striker.getX();
//        double gridy = striker.getY();
//
//        double disx = ball.getX() - gridx;
//        double disy = ball.getY() - gridy;
//        double sqaure = disx * disx + disy * disy;
//
//        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
//        double relativeCollisionX = ball.getX() - striker1.getX();
//        double relativeCollisionY = ball.getY() - striker1.getY();
//        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//
//        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//        if (sqaure < sumOfRadiiSquared && prevxs1 != gridx && prevys1 != gridy) {
//            double movediff = Math.pow((sumOfRadiiSquared - sqaure), 0.5);
//            striker.setX(gridx - movediff * Math.cos(strikerMovementDirection));
//            striker.setY(gridy - movediff * Math.sin(strikerMovementDirection));
//            prevxs1 = gridx - movediff * Math.cos(strikerMovementDirection);
//            prevys1 = gridy - movediff * Math.sin(strikerMovementDirection);
//            striker.draw();
//
//        }
//        return result;
//    }
//
//    private boolean intersectss2(Ball ball, Striker striker) {
//
//        Circle ballCircle = ball.getCircle();
//        Circle strikerCircle = striker.getCircle();
//        boolean result;
//        result = getDistance(striker.getX(), striker.getY(), ball.getX(), ball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(), 2);
//        double gridx = striker.getX();
//        double gridy = striker.getY();
//
//        double disx = ball.getX() - gridx;
//        double disy = ball.getY() - gridy;
//        double sqaure = disx * disx + disy * disy;
//
//        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
//        double relativeCollisionX = ball.getX() - striker1.getX();
//        double relativeCollisionY = ball.getY() - striker1.getY();
//        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//
//        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//        if (sqaure < sumOfRadiiSquared && prevxs2 != gridx && prevys2 != gridy) {
//            double movediff = Math.pow((sumOfRadiiSquared - sqaure), 0.5);
//            striker.setX(gridx - movediff * Math.cos(strikerMovementDirection));
//            striker.setY(gridy - movediff * Math.sin(strikerMovementDirection));
//            prevxs2 = gridx - movediff * Math.cos(strikerMovementDirection);
//            prevys2 = gridy - movediff * Math.sin(strikerMovementDirection);
//            striker.draw();
//
//        }
//        return result;
//    }
//
//    private boolean intersectsslow(slow_ball slowball, Striker striker) {
//
//        Circle ballCircle = slowball.getCircle();
//        Circle strikerCircle = striker.getCircle();
//
//
//        return getDistance(striker.getX(), striker.getY(), slowball.getX(), slowball.getY()) <= Math.pow(ballCircle.getRadius() + strikerCircle.getRadius(), 2);
//
////        // checks that if both bounds collide or not
////        return strikerCircle.getBoundsInParent().intersects(ballCircle.getBoundsInParent()) || ballCircle.getBoundsInParent().intersects(strikerCircle.getBoundsInParent());
//    }
//
//    private boolean intersects(Striker striker1, Striker striker2) {
//
////        Circle ballCircle = ball.getCircle();
////        Circle strikerCircle = striker.getCircle();
//
////        System.out.println(getDistance(striker1.getX(),striker1.getY(),striker2.getX(),striker2.getY()));
////        System.out.println(Math.pow(striker1.getradius() + striker2.getradius(),2));
//
//        return getDistance(striker1.getX(), striker1.getY(), striker2.getX(), striker2.getY()) <= Math.pow(striker1.getradius() + striker2.getradius(), 2);
//
////        // checks that if both bounds collide or not
////        return strikerCircle.getBoundsInParent().intersects(ballCircle.getBoundsInParent()) || ballCircle.getBoundsInParent().intersects(strikerCircle.getBoundsInParent());
//    }
//
//
//    private double getDistance(double x1, double y1, double x2, double y2) {
//        double xDis = x2 - x1;
//        double yDis = y2 - y1;
//
//        return xDis * xDis + yDis * yDis;
//
//    }
//
//    private void released(MouseEvent event, Striker striker) {
//
//        long endTime = System.nanoTime();
//        double timeElapsed = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
//        double distanceX = event.getSceneX() - startX;
//        double distanceY = event.getSceneY() - startY;
//        double speedX = distanceX / timeElapsed; // Speed in pixels per second
//        double speedY = distanceY / timeElapsed; // Speed in pixels per second
//
//        System.out.println("Mouse Dragging Speed (X): " + speedX);
//        System.out.println("Mouse Dragging Speed (Y): " + speedY);
//    }
//
//
//    private void dragged(MouseEvent event, Striker striker) {
//
//        // determines the tasks when striker is dragged
//        double gridx = striker.getX() + event.getX();
//        double gridy = striker.getY() + event.getY();
//
//        double disx = ball.getX() - gridx;
//        double disy = ball.getY() - gridy;
//        double sqaure = disx * disx + disy * disy;
//
//        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
//        //double sumOfRadiiSquared =
//
//        double relativeCollisionX = ball.getX() - striker1.getX();
//        double relativeCollisionY = ball.getY() - striker1.getY();
//        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//        if (sqaure < sumOfRadiiSquared) {
//
//            striker.setX(gridx);
//            striker.setY(gridy);
//        } else {
//
//            striker.setX(gridx);
//            striker.setY(gridy);
//            prevxs1 = gridx;
//            prevys1 = gridy;
//            striker.draw();
//        }
//    }
//
//    private void draggeds2(MouseEvent event, Striker striker) {
//
//        // determines the tasks when striker is dragged
//        double gridx = striker.getX() + event.getX();
//        double gridy = striker.getY() + event.getY();
//
//        double disx = ball.getX() - gridx;
//        double disy = ball.getY() - gridy;
//        double sqaure = disx * disx + disy * disy;
//
//        double sumOfRadiiSquared = Math.pow(ball.getCircle().getRadius() + striker.getCircle().getRadius(), 2);
//        //double sumOfRadiiSquared =
//
//        double relativeCollisionX = ball.getX() - striker1.getX();
//        double relativeCollisionY = ball.getY() - striker1.getY();
//        double strikerMovementDirection = Math.atan2(striker1.getSpeedY(), striker1.getSpeedX());
//        double collisionAngle = Math.atan2(relativeCollisionY, relativeCollisionX);
//        if (sqaure < sumOfRadiiSquared) {
//
//            striker.setX(gridx);
//            striker.setY(gridy);
//        } else {
//
//            striker.setX(gridx);
//            striker.setY(gridy);
//            prevxs2 = gridx;
//            prevys2 = gridy;
//
//            striker.draw();
//        }
//    }
//
//
//    @FXML
//    private void handleSlowOpponentButtonClick(ActionEvent actionEvent) {
//        if (slowballbool) {
//            slowballbool = false;
//            isSlowed = true;
//
//            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//            c.setFill(Color.YELLOW);
//            c.setStroke(Color.WHITE);
//
//            Random random = new Random();
//            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
//            ballVelocity = slowball.getInitialSpeed();
//
//            ball.getCircle().setVisible(false);
//
//            pane.getChildren().add(c);
//
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(5),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
//
//                            isSlowed = false;
//                            slowball.getCircle().setVisible(false);
//
//                            ball.setX(slowball.getX());
//                            ball.setY(slowball.getY());
//
//                            ball.getCircle().setVisible(true);
//
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//        }
//    }
//
//    @FXML
//    private void handlefreezeOpponentButtonClick(ActionEvent actionEvent) {
//        // Check if the opponent striker is not already frozen
//
//        if (frozen) {
//            frozen = false;
//            if (!isOpponentStrikerFrozen) {
//                // Set the flag to indicate that the opponent striker is frozen
//                isOpponentStrikerFrozen = true;
//
//                // Disable mouse events for the opponent striker
//                striker2.getCircle().setDisable(true);
//                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen_s.png"));
//                ImagePattern ballPattern = new ImagePattern(ballImage);
//                striker2.getCircle().setFill(ballPattern);
//
//                // Start a timeline to unfreeze the opponent striker after 5 seconds
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                // Reset the flag to indicate that the opponent striker is no longer frozen
//                                isOpponentStrikerFrozen = false;
//
//                                // Enable mouse events for the opponent striker
//                                striker2.getCircle().setDisable(false);
//                                Image ballImage = new Image(getClass().getResourceAsStream("/str.jpg"));
//                                ImagePattern ballPattern = new ImagePattern(ballImage);
//                                striker2.getCircle().setFill(ballPattern);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }
//
//    }
//
//    @FXML
//    private void handlegoalpostbarrierButtonClick(ActionEvent actionEvent) {
//
//        if (goalpost) {
//            goalpost = false;
//            if (!goalpostbarrier) {
//
//                goalpostbarrier = true;
//                player1goalps.setStroke(Color.RED);
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                goalpostbarrier = false;
//                                player1goalps.setStroke(Color.WHITE);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }
//
//    }
//
//    @FXML
//    private void handleSlowOpponentButtonClickplay2(ActionEvent actionEvent)
//    {
//        if (slowballboolplay2) {
//            slowballboolplay2 = false;
//            isSlowed = true;
//
//            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//            c.setFill(Color.YELLOW);
//            c.setStroke(Color.WHITE);
//
//            Random random = new Random();
//            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
//            ballVelocity = slowball.getInitialSpeed();
//
//            ball.getCircle().setVisible(false);
//
//            pane.getChildren().add(c);
//
//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.seconds(5),
//                    new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value
//
//                            isSlowed = false;
//                            slowball.getCircle().setVisible(false);
//
//                            ball.setX(slowball.getX());
//                            ball.setY(slowball.getY());
//
//                            ball.getCircle().setVisible(true);
//
//                            /*   Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
//                            c.setFill(Color.YELLOW);
//                            c.setStroke(Color.WHITE);*/
//
//
//                        }
//                    }
//            ));
//            timeline.setCycleCount(1);
//            timeline.play();
//        }
//    }
//
//    @FXML
//    private void handlefreezeOpponentButtonClickplay2(ActionEvent actionEvent) {
//        // Check if the opponent striker is not already frozen
//
//        if (frozenplay2) {
//            frozenplay2 = false;
//            if (!isOpponentStrikerFrozen) {
//                // Set the flag to indicate that the opponent striker is frozen
//                isOpponentStrikerFrozen = true;
//
//                // Disable mouse events for the opponent striker
//                striker1.getCircle().setDisable(true);
//                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-frozen.png"));
//                ImagePattern ballPattern = new ImagePattern(ballImage);
//                striker1.getCircle().setFill(ballPattern);
//
//                // Start a timeline to unfreeze the opponent striker after 5 seconds
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                // Reset the flag to indicate that the opponent striker is no longer frozen
//                                isOpponentStrikerFrozen = false;
//
//                                // Enable mouse events for the opponent striker
//                                striker1.getCircle().setDisable(false);
//                                Image ballImage = new Image(getClass().getResourceAsStream("/cropped-str1.jpg"));
//                                ImagePattern ballPattern = new ImagePattern(ballImage);
//                                striker1.getCircle().setFill(ballPattern);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }
//
//    }
//
//    @FXML
//    private void handlegoalpostbarrierButtonClickplay2(ActionEvent actionEvent) {
//
//        if (goalpostplay2) {
//            goalpostplay2 = false;
//            if (!goalpostbarrierplay2) {
//
//                goalpostbarrierplay2 = true;
//                player2goalps.setStroke(Color.RED);
//                Timeline timeline = new Timeline(new KeyFrame(
//                        Duration.seconds(5),
//                        new EventHandler<ActionEvent>() {
//                            @Override
//                            public void handle(ActionEvent event) {
//                                goalpostbarrierplay2 = false;
//                                player2goalps.setStroke(Color.WHITE);
//                            }
//                        }
//                ));
//                timeline.setCycleCount(1);
//                timeline.play();
//            }
//        }
//    }
//
//        class MouseControl extends Thread {
//            private Striker striker;
//            private Circle circle;
//
//            public MouseControl(Striker striker, Circle circle) {
//                this.striker = striker;
//                this.circle = circle;
//            }
//
//            @Override
//            public void run() {
//                circle.setOnMousePressed(event -> pressed(event, striker));
//                circle.setOnMouseDragged(event -> dragged(event, striker));
//                circle.setOnMouseReleased(event -> released(event, striker));
//            }
//
//            private void released(MouseEvent event, Striker striker) {
//
//                long endTime = System.nanoTime();
//                double timeElapsed = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds
//                double distanceX = event.getSceneX() - startX;
//                double distanceY = event.getSceneY() - startY;
//                double speedX = distanceX / timeElapsed; // Speed in pixels per second
//                double speedY = distanceY / timeElapsed; // Speed in pixels per second
//
//                System.out.println("Mouse Dragging Speed (X): " + speedX);
//                System.out.println("Mouse Dragging Speed (Y): " + speedY);
//            }
//
//            private void dragged(MouseEvent event, Striker striker) {
//
//                // determines the tasks when striker is dragged
//                double gridx = striker.getX() + event.getX();
//                double gridy = striker.getY() + event.getY();
//
//                System.out.println("x " + gridx);
//                System.out.println("y " + gridy);
//
//                striker.setX(gridx);
//                striker.setY(gridy);
//                striker.draw();
//            }
//
//            private void pressed(MouseEvent event, Striker striker) {
//                startX = event.getSceneX();
//                startY = event.getSceneY();
//                startTime = System.nanoTime();
//            }
//        }
//
//        class KeyboardControl extends Thread {
//            private Striker striker;
//            private Pane pane;
//            private Set<KeyCode> pressedKeys = new HashSet<>();
//            private static final double STRIKER_SPEED = 30.0;
//
//            public KeyboardControl(Striker striker, Pane pane) {
//                this.striker = striker;
//                this.pane = pane;
//            }
//
//            @Override
//            public void run() {
//                Platform.runLater(() -> {
//                    pane.setOnKeyPressed(this::keyPressed);
//                    pane.setOnKeyReleased(this::keyReleased);
//                    pane.requestFocus(); // Ensure the pane has focus to receive key events
//                });
//            }
//
//            private void keyPressed(KeyEvent event) {
//                pressedKeys.add(event.getCode());
//                moveStriker();
//            }
//
//            private void keyReleased(KeyEvent event) {
//                pressedKeys.remove(event.getCode());
//                moveStriker();
//            }
//
//            private void moveStriker() {
//                double deltaX = 0;
//                double deltaY = 0;
//
//                if (pressedKeys.contains(KeyCode.W)) deltaY -= 1;
//                if (pressedKeys.contains(KeyCode.A)) deltaX -= 1;
//                if (pressedKeys.contains(KeyCode.S)) deltaY += 1;
//                if (pressedKeys.contains(KeyCode.D)) deltaX += 1;
//
//                // Normalize diagonal movement
//                if (deltaX != 0 && deltaY != 0) {
//                    double diagonalFactor = Math.sqrt(2) / 2;
//                    deltaX *= diagonalFactor;
//                    deltaY *= diagonalFactor;
//                }
//
//                double newX = striker.getX() + deltaX * STRIKER_SPEED;
//                double newY = striker.getY() + deltaY * STRIKER_SPEED;
//
//                // Boundary checking can be added here if needed
//                // newX = Math.max(Math.min(newX, GAME_WIDTH - strikerSize / 2), strikerSize / 2);
//                // newY = Math.max(Math.min(newY, GAME_HEIGHT - strikerSize / 2), strikerSize / 2);
//
//                striker.setX(newX);
//                striker.setY(newY);
//                striker.draw();
//            }
//
//        }
//
//    }
//
//
//
//
//
//
