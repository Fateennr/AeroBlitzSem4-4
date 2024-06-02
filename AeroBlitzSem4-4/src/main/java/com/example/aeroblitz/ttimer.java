package com.example.aeroblitz;

//import com.sun.glass.ui.Timer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class ttimer {
    private int timeSeconds;
    private Timeline countdownTimeline;
    private Score score; // Instance of Score class to access drawTimer method
    private GraphicsContext gc; // GraphicsContext object

    public ttimer(int initialSeconds, Score score) {
        this.timeSeconds = initialSeconds;
        this.score = score;
        this.gc = gc; // Assign the GraphicsContext object
    }

    public void startCountdown(GraphicsContext gc) {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownTimeline = new Timeline();
        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                timeSeconds--;
                                score.updateTimer(gc, timeSeconds); // Update the timer text
//                                if (timeSeconds <= 0)
//                                {
//                                    countdownTimeline.stop();
//                                   // Timer gameLoop;
//                                    //gameLoop.stop();
//                                  //  if(player1.score>player2.score) {
//
//                                    //}
//                                    timeSeconds = 30;// Reset the timer to 30 seconds
//                                }
                            }
                        }));
        countdownTimeline.playFromStart();
    }

    public void pauseCountdown() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
    }

    public int getTimeSeconds()
    {
        if(timeSeconds <0 )
        {
            timeSeconds = 0;
        }

        return timeSeconds;
    }
    public int setTimeSeconds(int timeSeconds) {
        return this.timeSeconds = timeSeconds;
    }
}
