package com.example.aeroblitz;

import javafx.animation.*;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

public class Board {


    @FXML
    private AnchorPane pane;

    private boolean frozen=true,slowballbool=true,goalpost=true;

    @FXML
    private Rectangle player1goalps,player2goalps;

    @FXML
    private Button slowOpponentButton;
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

    private boolean goalpostbarrier=false;



    private final AnimationTimer gameLoop = new AnimationTimer() { //game loop for running updates in the board
        @Override
        public void handle(long now) {

            drawTrail();

            if (ballVelocity == slowball.getInitialSpeed()) {

                slowball.move();
                slowball.draw();

                checkCollisionslow();

                System.out.println("time " + score.getTime());

                if (score.getTime() == 0) {
                    gameEnded = true;
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
        newStriker(700, 300, Color.GREEN,2, striker2);
        newBall();
        newslowBall();// initial position of the ball, it will start from any random position in the middle line of the board
        ballVelocity=ball.getspeed();

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
        ball = new Ball((GAME_WIDTH / 2) - (BALL_DIAMETER / 2),(GAME_HEIGHT/2) - (BALL_DIAMETER/2), BALL_DIAMETER, c);
      //  ball=new Ball(36,32,BALL_DIAMETER,c);
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
        c.setFill(Color.YELLOW);
        c.setStroke(Color.WHITE);

        Random random = new Random();
        slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
        pane.getChildren().add(c);


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
           // c.setOnMousePressed(event -> pressed(event, striker));
            c.setOnMouseDragged(event -> dragged(event, striker));
            c.setOnMouseReleased(event -> released(event, striker));
        }
        else
        {

//

        }
    }


    //Collision physics

    public void checkCollisionslow() {


        int diameter = strikerSize/2;
        int ball_radius = BALL_DIAMETER/2;
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


        if (slowball.getY() <= 32 || slowball.getY() >= (GAME_HEIGHT - 50)) {
            slowball.setYDirection(-slowball.getYVelocity());
        }


        if (slowball.getX() <= 36 &&  slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) ) {

            score.player2++;
            resetPositions();
            draw_scoreboard();

            //pause the game for 5seconds after every goal
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

        else if (slowball.getX() >= (GAME_WIDTH - 36) && slowball.getY() >= (goalpos+ BALL_DIAMETER) && slowball.getY() <= (goalpos + 140 - BALL_DIAMETER) ) {

            score.player1++;
            resetPositions();
            draw_scoreboard();

            //pause the game for 5seconds after each goal
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

        else if (slowball.getX() <= 36 || slowball.getX() >= (GAME_WIDTH - 36)) {
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


        // Collisions with the strikers
        if (intersectsslow(slowball, striker1) || intersectsslow(slowball, striker2) ) {

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
            double newBallXVelocity = Math.cos(angleDifference) * slowball.getInitialSpeed() - Math.sin(angleDifference) * slowball.getInitialSpeed();
            double newBallYVelocity = Math.sin(angleDifference) * slowball.getInitialSpeed() + Math.cos(angleDifference) * slowball.getInitialSpeed();

            double newBallX = slowball.getX() + newBallXVelocity;
            double newBallY = slowball.getY() + newBallYVelocity;
            Random random = new Random();
            int i=random.nextInt(5);


            //ball moving beyond top edge or left edge
            if(newBallX<=36 || newBallY<=32)
            {
                //ball moving beyond left edge only
                if(newBallX<=36 && newBallY>32)
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {

                        newBallX -= 2 * 50;
                    }
                }

                //ball moving beyond top edge only
                else if (newBallX>36 && newBallY<=32) {

                    newBallY+=50;
                    while (newBallY<=32)
                    {
                        newBallY -=2*050;
                    }
                }

                //ball mobing both beyond top and left edge

                else {
                    newBallX += 50;
                    if (newBallX <= 36 && newBallY <= 32) {
                        newBallX -= 2* 50;
                    }

                    newBallY+=50;
                    while (newBallY <= 32) {
                        newBallY -= 2 * 50;
                    }
                }



            }

            //ball moving beyond left edge or beyond bottom edge
            else if(newBallX<=36 || newBallY>=(GAME_HEIGHT-50))

            {

                //ball moving beyond left edge only
                if(newBallX<=36 && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if (newBallX<=36)
                    {
                        newBallX -=2*50;
                    }
                }

                //ball moving beyond bottom edge only
                else if (newBallX>36 && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if (newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -=2*50;
                    }
                }

                //ball moving both beyond left edge and bottom edge
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



            //ball moving right edge or top edge
            else if(newBallX>=(GAME_WIDTH-36) || newBallY<=32)
            {

                //ball moving beyond right edge only
                if(newBallX>=(GAME_WIDTH-36) && newBallY>32)
                {
                    newBallX += 50;
                    if(newBallX>=(GAME_WIDTH-36))
                    {
                        newBallX -=2*50;
                    }
                }

                //ball moving beyond top edge only
                else if (newBallX<(GAME_WIDTH-36) && newBallY<=32) {
                    newBallY += 50;
                    if (newBallY<=32)
                    {
                        newBallY -= 2*50;
                    }
                }

                //ball moving beyond right edge and top edge both
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

            //ball moving beyond right edge or bottom edge
            else if(newBallX>=(GAME_WIDTH-36) || newBallY>=(GAME_HEIGHT-50))
            {

                //ball moving beyond right edge only
                if(newBallX>=(GAME_WIDTH-36) && newBallY<(GAME_HEIGHT-50))
                {
                    newBallX += 50;
                    if(newBallX<=(GAME_WIDTH-36))
                    {
                        newBallX -= 2*50;
                    }
                }
                //ball moving beyond bottom edge only
                else if (newBallX<(GAME_WIDTH-36) && newBallY>=(GAME_HEIGHT-50)) {
                    newBallY += 50;
                    if(newBallY>=(GAME_HEIGHT-50))
                    {
                        newBallY -= 2*50;
                    }

                }

                //ball moving beyond right and bottom edge both
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

            // Ensure the new position of the ball stays within the boundaries of the board
            // Adjust the position if it goes beyond the boundaries
            newBallX = Math.max(slowball.getCircle().getRadius(), Math.min(newBallX, GAME_WIDTH - slowball.getCircle().getRadius()));
            newBallY = Math.max(slowball.getCircle().getRadius(), Math.min(newBallY, GAME_HEIGHT - slowball.getCircle().getRadius()));

            if (newBallX >= slowball.getCircle().getRadius() || newBallX == GAME_WIDTH - slowball.getCircle().getRadius()) {
                newBallX += newBallXVelocity;
            }
            if (newBallY >= slowball.getCircle().getRadius() || newBallY == GAME_HEIGHT - slowball.getCircle().getRadius()) {
                newBallY += newBallYVelocity;
            }
            // Update the position of the ball
            slowball.setX(newBallX);
            slowball.setY(newBallY);
            // Update the ball's velocity
            slowball.setXDirection(newBallXVelocity);
            slowball.setYDirection(newBallYVelocity);


        }

    }

    public void checkCollision() {


        int diameter = strikerSize/2;
        int ball_radius = BALL_DIAMETER/2;
        int rectWidth = 50;
        int rectHeight = 150;
        int rectX = 0; // Positioned on the left wall
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


        if (ball.getY() <= 32 || ball.getY() >= (GAME_HEIGHT - 50)) {
            ball.setYDirection(-ball.getYVelocity());
        }


       if (ball.getX() <= 36 && /*ball.getY() >= (220+ BALL_DIAMETER) && ball.getY() <= (360) &&*/ ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && !goalpostbarrier) {


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

       else if (ball.getX() >= (GAME_WIDTH - 36) && ball.getY() >= (goalpos+ BALL_DIAMETER) && ball.getY() <= (goalpos + 140 - BALL_DIAMETER) && true) {

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


/*
        if (ball.getX() <= 32 && ball.getY() >240 && ball.getY() < (360)  && !goalpostbarrier) {
            // Check collision with the top side of the rectangle

            if (ball.getY()>240) {
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
                ball.setY(235); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }
            // Check collision with the bottom side of the rectangle
            if ( ball.getY() < (360)) {
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
                ball.setY(365); // Prevent the ball from entering the rectangle
                ball.setYDirection(-ball.getYVelocity());
            }

        }*/

        if (ball.getX() - ball_radius <= rectX + rectWidth && ball.getY() + ball_radius >= rectY && ball.getY() - ball_radius <= rectY + rectHeight && goalpostbarrier) {
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

      else if (ball.getX() <= 36 || ball.getX() >= (GAME_WIDTH - 36)) {
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


        // Collisions with the strikers
        if (intersects(ball, striker1) || intersects(ball, striker2) ) {



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
            double newBallXVelocity = Math.cos(angleDifference) * ball.getspeed() - Math.sin(angleDifference) * ball.getspeed();
            double newBallYVelocity = Math.sin(angleDifference) * ball.getspeed() + Math.cos(angleDifference) * ball.getspeed();

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

            // Ensure the new position of the ball stays within the boundaries of the board
            // Adjust the position if it goes beyond the boundaries
            newBallX = Math.max(ball.getCircle().getRadius(), Math.min(newBallX, GAME_WIDTH - ball.getCircle().getRadius()));
            newBallY = Math.max(ball.getCircle().getRadius(), Math.min(newBallY, GAME_HEIGHT - ball.getCircle().getRadius()));

            if (newBallX >= ball.getCircle().getRadius() || newBallX == GAME_WIDTH - ball.getCircle().getRadius()) {
                newBallX += newBallXVelocity;
            }
            if (newBallY >= ball.getCircle().getRadius() || newBallY == GAME_HEIGHT - ball.getCircle().getRadius()) {
                newBallY += newBallYVelocity;
            }
            // Update the position of the ball
            ball.setX(newBallX);
            ball.setY(newBallY);
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
        if (sqaure < sumOfRadiiSquared) {
            double movediff=Math.pow((sumOfRadiiSquared-sqaure),0.5);


            striker.setX(gridx-20*Math.cos(strikerMovementDirection));
            striker.setY(gridy-20*Math.sin(strikerMovementDirection));
            striker.draw();

        }
        return result;
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
            striker.draw();
        }
    }




    @FXML
    private void handleSlowOpponentButtonClick(ActionEvent actionEvent) {
        if (slowballbool) {
            slowballbool=false;
            isSlowed = true;

            Circle c = new Circle(); // before passing it to the ball constructor first we are creating a circle
            c.setFill(Color.YELLOW);
            c.setStroke(Color.WHITE);

            Random random = new Random();
            slowball = new slow_ball(ball.getX(), ball.getY(), BALL_DIAMETER, c);
            ballVelocity=slowball.getInitialSpeed();

            ball.getCircle().setVisible(false);

            pane.getChildren().add(c);

            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(5),
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            ballVelocity = ball.getspeed(); // Reset the ball's velocity to its initial value

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

        if(frozen)
        {
            frozen=false;
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

    }
    @FXML
    private void handlegoalpostbarrierButtonClick(ActionEvent actionEvent) {

        if(goalpost)
        {
            goalpost=false;
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

    }

}
