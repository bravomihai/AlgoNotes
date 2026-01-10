package dao;

import db.Database;
import model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    public void insert(Note note) throws SQLException {
        String sql = """
        INSERT INTO notes (problem_id, content)
        VALUES (?, ?)
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, note.getProblemId());
            ps.setString(2, note.getContent());
            ps.executeUpdate();
        }
    }


    public List<Note> findAll() throws SQLException {
        List<Note> result = new ArrayList<>();

        String sql = "SELECT * FROM notes";

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Note n = new Note(
                        rs.getInt("id"),
                        rs.getInt("problem_id"),
                        rs.getString("content"),
                        rs.getString("date_added"),
                        rs.getString("last_updated")
                );
                result.add(n);
            }
        }
        return result;
    }

    public Note findById(int id) throws SQLException {
        String sql = "SELECT * FROM notes WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Note(
                            rs.getInt("id"),
                            rs.getInt("problem_id"),
                            rs.getString("content"),
                            rs.getString("date_added"),
                            rs.getString("last_updated")
                    );
                }
            }
        }
        return null;
    }

    public List<Note> findByProblemId(int problemId) throws SQLException {
        List<Note> result = new ArrayList<>();

        String sql = "SELECT * FROM notes WHERE problem_id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, problemId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Note n = new Note(
                            rs.getInt("id"),
                            rs.getInt("problem_id"),
                            rs.getString("content"),
                            rs.getString("date_added"),
                            rs.getString("last_updated")
                    );
                    result.add(n);
                }
            }
        }
        return result;
    }

    public void update(Note note) throws SQLException {
        String sql = """
            UPDATE notes
            SET content = ?, last_updated = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, note.getContent());
            ps.setInt(2, note.getId());
            ps.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM notes WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
