package com.example.aeroblitz;

<<<<<<< HEAD

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
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
=======
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;

import java.util.Random;

public class Ball extends Node {
    private  double x;
    private  double y;
    private  double radius;
    private Circle c;
    private DropShadow dropShadow;

>>>>>>> edfae3f (ggs)

    Random random;
    double xVelocity;
    double yVelocity;
<<<<<<< HEAD
    double initialSpeed = 5;

    public Ball(double x, double y, double radius, Circle c) {
=======
    int initialSpeed = 3;
    public Ball(double x, double y, double radius, Circle c)
    {
>>>>>>> edfae3f (ggs)
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.c = c;

        random = new Random();

<<<<<<< HEAD
        // Create a DropShadow effect
        dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.BLACK);
        c.setEffect(dropShadow);

        // Initialize motion trail
        motionTrail = new ArrayList<>();

        // Initialize velocities
        int randomXDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for x velocity
        int randomYDirection = random.nextInt(2) == 0 ? -1 : 1; // Random direction for y velocity
=======

        // Create a DropShadow effect
        dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(javafx.scene.paint.Color.BLACK);
        c.setEffect(dropShadow);


        //for x
        int randomXDirection = random.nextInt(2); //setting a random direction for the ball
        if(randomXDirection == 0)
        {
            randomXDirection--;
        }
>>>>>>> edfae3f (ggs)
        setXDirection(randomXDirection * initialSpeed);
        setYDirection(randomYDirection * initialSpeed);
<<<<<<< HEAD
=======


>>>>>>> edfae3f (ggs)
    }

    void setYDirection(double randomYDirection) {
        yVelocity = randomYDirection;
    }

    void setXDirection(double randomXDirection) {
        xVelocity = randomXDirection;
    }

<<<<<<< HEAD
    public void move() {

        // Add motion trail segment
        Line trailSegment = new Line(x + radius / 2, y + radius / 2, x + radius / 2 - xVelocity, y + radius / 2 - yVelocity);
        trailSegment.setStroke(trailColor);
        motionTrail.add(trailSegment);

        if (motionTrail.size() > maxTrailLength) {
            getChildren().remove(motionTrail.remove(0)); // Remove oldest trail segment
        }

        // Update ball position
=======
    public void move()
    {
>>>>>>> edfae3f (ggs)
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

<<<<<<< HEAD
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

=======
    public void setX(double x)
    {
        this.x = x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setColor(javafx.scene.paint.Color color)
    {
        c.setFill(color);

    }

    public void draw()
    {
        c.setRadius(radius);
        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }

    public double getXVelocity() {
        return xVelocity;
    }

>>>>>>> edfae3f (ggs)
    public double getYVelocity() {
        return yVelocity;
    }

    public Circle getCircle() {
        return c;
    }
<<<<<<< HEAD

    public double getradius() {
        return  radius;
    }

    public void setspeed(double speed)
    {
        initialSpeed=speed;
    }
=======
>>>>>>> edfae3f (ggs)
}


//=======
//import java.awt.*;
//import java.awt.event.*;
//import java.util.*;
//import javax.swing.*;
//
//public class Ball extends Rectangle {
//
//    Random random;
//    int xVelocity;
//    int yVelocity;
//    int initialSpeed = 3;
//
//    Ball(int x, int y, int width, int height)
//    {
//        super(x,y,width,height);
//        random = new Random();
//
//        //for x
//        int randomXDirection = random.nextInt(2); //setting a random direction for the ball
//        if(randomXDirection == 0)
//        {
//            randomXDirection--;
//        }
//        setXDirection(randomXDirection * initialSpeed);
//
//        //for y
//        int randomYDirection = random.nextInt(2); //setting a random direction for the ball
//        if(randomYDirection == 0)
//        {
//            randomYDirection--;
//        }
//        setYDirection(randomYDirection * initialSpeed);
//
//    }
//
//    public void setXDirection(int randomXDirection)
//    {
//        xVelocity = randomXDirection;
//    }
//
//    public void setYDirection(int randomYDirection)
//    {
//        yVelocity = randomYDirection;
//    }
//
//    public void move()
//    {
//        x += xVelocity;
//        y += yVelocity;
//    }
//
//    public void draw(Graphics g)
//    {
//        g.setColor(Color.YELLOW);
//        g.fillOval(x,y,width,height);
//    }
//
//}
//>>>>>>> 1709435 (ggs)
