package com.example.aeroblitz;

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


    Random random;
    double xVelocity;
    double yVelocity;
    int initialSpeed = 3;
    public Ball(double x, double y, double radius, Circle c)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.c = c;

        random = new Random();


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
        setXDirection(randomXDirection * initialSpeed);

        //for y
        int randomYDirection = random.nextInt(2); //setting a random direction for the ball
        if(randomYDirection == 0)
        {
            randomYDirection--;
        }
        setYDirection(randomYDirection * initialSpeed);


    }

    void setYDirection(double randomYDirection) {
        yVelocity = randomYDirection;
    }

    void setXDirection(double randomXDirection) {
        xVelocity = randomXDirection;
    }

    public void move()
    {
        x += xVelocity;
        y += yVelocity;
    }

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

    public double getYVelocity() {
        return yVelocity;
    }

    public Circle getCircle() {
        return c;
    }
}
