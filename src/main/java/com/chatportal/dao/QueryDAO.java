package com.chatportal.dao;

import com.chatportal.model.Query;
import com.chatportal.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryDAO {

    public boolean addQuery(int userId, String subjectCode, String content) {
        if (!isUserValid(userId)) {
            System.err.println("Invalid user ID: " + userId);
            return false;
        }

        String query = "INSERT INTO queries (user_id, subject_code, content) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, userId);
            ps.setString(2, subjectCode);
            ps.setString(3, content);
            int rowsInserted = ps.executeUpdate();

            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Query> getUserQueries(int userId) {
        List<Query> queries = new ArrayList<>();
        String query = "SELECT * FROM queries WHERE user_id = ? ORDER BY timestamp DESC";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Query queryObj = new Query(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("subject_code"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("teacher_response")
                );
                queries.add(queryObj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queries;
    }

    public List<Query> getAllQueries() {
        List<Query> queries = new ArrayList<>();
        String query = "SELECT * FROM queries ORDER BY timestamp DESC";  // Fetch all queries

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Query queryObj = new Query(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("subject_code"),
                        rs.getString("content"),
                        rs.getTimestamp("timestamp"),
                        rs.getString("teacher_response")
                );
                queries.add(queryObj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queries;
    }

    private boolean isUserValid(int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateQueryResponse(int queryId, String response) {
        String query = "UPDATE queries SET teacher_response = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, response);
            ps.setInt(2, queryId);
            int rowsUpdated = ps.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
