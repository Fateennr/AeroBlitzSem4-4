package com.example.aeroblitz;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;

public class Striker
{
    private int id ; // to determine the type of the striker players or opponents
    private  double x;
    private  double y;
    private  double radius;
    private Circle c;
    private DropShadow dropShadow;

    public Striker()
    {
        this.x = 0;
        this.y = 0;
        this.radius = 0;
        this.c = new Circle();
    }
    public Striker(double x, double y, double radius,int id, Circle c)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.c = c != null ? c : new Circle(); // Initialize c if it's null


        // Create a DropShadow effect
        dropShadow = new DropShadow();
        dropShadow.setRadius(10);
        dropShadow.setColor(Color.BLACK);
        c.setEffect(dropShadow);
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

    public void setColor(Color color)
    {
        c.setFill(color);

    }

    public void draw()
    {
//        System.out.println("draw inv0ked\n");
        c.setRadius(radius);
        c.setTranslateX(x);
        c.setTranslateY(y);
    }

    public void setRadius(double radius) {
        this.radius = radius;
        c.setRadius(radius);
    }

    public double getradius()
    {
        return radius;
    }

    public void setCircle(Circle c) {
        this.c = c;
    }

    public Circle getCircle() {
        return c;
    }

    public double getSpeedY() {
        return 2.5;
    }

    public double getSpeedX() {
        return 2.5;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getID()
    {
        return id;
    }
}