package com.example.aeroblitz;


import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ball extends Group {
    private double x;
    private double y;
    private double radius;
    private Circle c;
    private DropShadow dropShadow;

    private List<Node> motionTrail;
    private int maxTrailLength = 10; // Maximum number of trail segments
    private Color trailColor = Color.rgb(255, 0, 0, 0.3); // Trail color with transparency

    Random random;
    double xVelocity;
    double yVelocity;
    double initialSpeed = 7;

    public Ball(double x, double y, double radius, Circle c) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.c = c;

        random = new Random();

        // Create a DropShadow effect
        dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.BLACK);



        // Apply the ImagePattern to the ball

        c.setEffect(dropShadow);

        // Initialize motion trail
        motionTrail = new ArrayList<>();

        // Initialize velocities
        int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
        int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
        setXDirection(randomXDirection * initialSpeed);
        setYDirection(randomYDirection * initialSpeed);
        /*setXDirection((0));
        setYDirection(-initialSpeed);*/
    }

    void setYDirection(double randomYDirection) {
        yVelocity = randomYDirection;
    }

    void setXDirection(double randomXDirection) {
        xVelocity = randomXDirection;
    }

    public void move() {

        // Add motion trail segment
        Line trailSegment = new Line(x + radius / 2, y + radius / 2, x + radius / 2 - xVelocity, y + radius / 2 - yVelocity);
        trailSegment.setStroke(trailColor);
        motionTrail.add(trailSegment);

        if (motionTrail.size() > maxTrailLength) {
            getChildren().remove(motionTrail.remove(0)); // Remove oldest trail segment
        }

        // Update ball position
        x += xVelocity;
        y += yVelocity;

        // Update motion trail position
        for (Node node : motionTrail)
        {
            if (node instanceof Line) {
                Line trail = (Line) node;
                trail.setStartX(trail.getStartX() + xVelocity);
                trail.setStartY(trail.getStartY() + yVelocity);
                trail.setEndX(trail.getEndX() + xVelocity);
                trail.setEndY(trail.getEndY() + yVelocity);
            }
        }
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setColor(Color color) {
        c.setFill(color);
    }

    public void draw() {
        c.setRadius(radius);
        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    public double getXVelocity() {
        return xVelocity;
    }

    public void setxVelocity(double speed)
    {
        xVelocity=speed;
    }
    public void setyVelocity(double speed)
    {
        yVelocity=speed;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public Circle getCircle() {
        return c;
    }

    public double getradius() {
        return  radius;
    }

    public void setspeed(double speed)
    {
        initialSpeed=speed;
    }

    public double getspeed()
    {
        return initialSpeed;
    }


    public void update(double deltaTime) {
        // Update the position of the ball based on its velocity and the elapsed time
        double newX = x + xVelocity * deltaTime;
        double newY = y + yVelocity * deltaTime;

        // Update the position of the ball
        setX(newX);
        setY(newY);
    }

}


