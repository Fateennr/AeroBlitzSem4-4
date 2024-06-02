package com.example.aeroblitz;

import java.sql.*;

public class Database_Connection {
    // JDBC connection parameters
    private static final String url = "jdbc:postgresql://localhost/aeroblitz";
    private static final String user = "postgres";
    private static final String password = "1234";

    // Method to connect to the database
    protected static Connection connect() throws SQLException
    {
        return DriverManager.getConnection(url, user, password);
    }

    // Method to save a score to the database
    public static void saveScore(String playerName, int score) {
        String query = "INSERT INTO high_scores (player_name, score) VALUES (?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            System.out.println("connected successfully.");

            pstmt.setString(1, playerName);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("Score saved successfully.");
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to retrieve top 5 high scores from the database
    public static void retrieveTopScores() {
        String query = "SELECT player_name, score FROM high_scores ORDER BY score DESC LIMIT 5";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Top 5 High Scores:");
            System.out.println("-------------------");
            while (rs.next()) {
                String playerName = rs.getString("player_name");
                int score = rs.getInt("score");
                System.out.println(playerName + ": " + score);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving top scores: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
