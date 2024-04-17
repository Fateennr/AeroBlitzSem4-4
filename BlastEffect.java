package com.example.aeroblitz;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class BlastEffect extends Group {

    private static final double BLAST_DURATION_SECONDS = 0.8; // Duration of the blast effect animation
    private static final double MAX_BLAST_RADIUS = 40; // Maximum radius of the blast effect

    public BlastEffect(double x, double y, Color color) {
        // Create a circle for the blast effect
        Circle blastCircle = new Circle(x, y, 20);
        blastCircle.setFill(Color.TRANSPARENT);
        blastCircle.setStroke(color);
        blastCircle.setStrokeWidth(3);

        // Add the blast circle to the group
        getChildren().add(blastCircle);

        // Create a scale animation for increasing the size of the circle
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(BLAST_DURATION_SECONDS), blastCircle);
        scaleTransition.setToX(MAX_BLAST_RADIUS / blastCircle.getRadius());
        scaleTransition.setToY(MAX_BLAST_RADIUS / blastCircle.getRadius());

        // Create a fade animation for the blast effect
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(BLAST_DURATION_SECONDS), blastCircle);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);

        // Combine scale and fade animations
        ParallelTransition parallelTransition = new ParallelTransition(scaleTransition, fadeTransition);
        parallelTransition.setOnFinished(event -> getChildren().remove(blastCircle));

        // Start the animation
        parallelTransition.play();
    }
}
