//import javafx.animation.AnimationTimer;
//import javafx.scene.Group;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Pane;
//import javafx.scene.paint.Color;
//
//import java.util.Random;
//
//public class GamePanel extends Pane {
//    private static final int GAME_WIDTH = 1000;
//    private static final int GAME_HEIGHT = (int) (GAME_WIDTH * (5.0 / 9.0));
//    private static final int BALL_DIAMETER = 20;
//    private static final int PADDLE_WIDTH = 25;
//    private static final int PADDLE_HEIGHT = 100;
//
//    private Canvas canvas;
//    private GraphicsContext gc;
//
//    private Paddle paddle1;
//    private Paddle paddle2;
//    private Ball ball;
//    private Score score;
//
//    public GamePanel() {
//        canvas = new Canvas(GAME_WIDTH, GAME_HEIGHT);
//        gc = canvas.getGraphicsContext2D();
//        getChildren().add(canvas);
//
//        // Initialize game objects
//        newPaddles();
//        newBall();
//        score = new Score(GAME_WIDTH, GAME_HEIGHT);
//
//        // Start the game loop
//        startGameLoop();
//    }
//
//    private void newBall() {
//        // Initialize the ball
//
//        random = new Random();
//        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2), random.nextInt((GAME_HEIGHT)-(BALL_DIAMETER)),BALL_DIAMETER,BALL_DIAMETER);
//        // for new ball we are using y to start off the ball any height at the middle line of the field
//    }
//
//    private void newPaddles() {
//        // Initialize paddles
//        paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
//        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
//    }
//
//    private void startGameLoop() {
//        new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                // Game loop logic goes here
//                move();
//                checkCollision();
//                draw();
//            }
//        }.start();
//    }
//
//    private void move() {
//        // Move game objects
//
//        paddle1.move();
//        paddle2.move();
//        ball.move();
//    }
//
//    private void checkCollision() {
//        // Check for collisions
//
//        //boundary checking for paddle1
//        if(paddle1.y<=0)
//            paddle1.y=0;
//
//        if(paddle1.y>= (GAME_HEIGHT-PADDLE_HEIGHT))
//            paddle1.y=GAME_HEIGHT-PADDLE_HEIGHT;
//
//        //boundary checking for paddle2
//        if(paddle2.y<=0)
//            paddle2.y=0;
//
//        if(paddle2.y>= (GAME_HEIGHT-PADDLE_HEIGHT))
//            paddle2.y=GAME_HEIGHT-PADDLE_HEIGHT;
//
//        //ball bounce
//
//        if(ball.y <=0){
//            ball.setYDirection(-ball.yVelocity);
//        }
//
//        if(ball.y >= GAME_HEIGHT-BALL_DIAMETER){
//            ball.setYDirection(-ball.yVelocity);
//        }
//
//        //bounces of paddles or not
//        if(ball.intersects(paddle1))
//        {
//            ball.xVelocity = Math.abs(-ball.xVelocity);
//            ball.xVelocity++; // ball returns faster after collision
//            if(ball.yVelocity>0)
//            {
//                ball.yVelocity++;
//            }
//            else
//            {
//                ball.yVelocity--;
//            }
//
//            ball.setXDirection(ball.xVelocity);
//            ball.setYDirection(ball.yVelocity);
//        }
//
//        if(ball.intersects(paddle2))
//        {
//            ball.xVelocity = -ball.xVelocity;
//            ball.xVelocity++; // ball returns faster after collision
//            if(ball.yVelocity>0)
//            {
//                ball.yVelocity++;
//            }
//            else
//            {
//                ball.yVelocity--;
//            }
//
//            ball.setXDirection(ball.xVelocity);
//            ball.setYDirection(ball.yVelocity);
//        }
//
//        //for bounces on the front wall
//        if(ball.x <= 0)
//        {
//            score.player2++;
//            newPaddles();
//            newBall();
//            System.out.println(score.player2);
//        }
//
//        if(ball.x >= (GAME_WIDTH-BALL_DIAMETER))
//        {
//            score.player1++;
//            newPaddles();
//            newBall();
//            System.out.println(score.player1);
//        }
//    }
//
//    private void draw() {
//        // Draw game objects
//    }
//
//    private void handleKeyPress(KeyCode keyCode) {
//        // Handle key presses
//    }
//
//    private void handleMousePress(MouseEvent event) {
//        // Handle mouse press
//    }
//
//    private void handleMouseDrag(MouseEvent event) {
//        // Handle mouse drag
//    }
//}