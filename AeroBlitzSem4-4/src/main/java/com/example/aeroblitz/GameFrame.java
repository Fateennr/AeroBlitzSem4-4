//package com.example.aeroblitz;
//
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.stage.Stage;
//
//public class GameFrame extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        GamePanel panel = new GamePanel();
//        primaryStage.setTitle("Pong Game");
//        primaryStage.setResizable(false);
//        primaryStage.setScene(new Scene(panel, GamePanel.GAME_WIDTH, GamePanel.GAME_HEIGHT));
//        primaryStage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
//
////
////import java.awt.*;
////import java.awt.event.*;
////import java.util.*;
////import javax.swing.*;
////
////public class GameFrame extends JFrame {
////
////    GamePanel panel;
////
////    GameFrame()
////    {
////        panel = new GamePanel();
////        this.add(panel);
////        this.setTitle("Pong Game");
////        this.setResizable(false);
////        this.setBackground(Color.black);
////        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        this.pack();
////        this.setVisible(true);
////        this.setLocationRelativeTo(null);
////
////    }
////
////}
