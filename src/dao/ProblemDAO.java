package dao;

import db.Database;
import model.Problem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProblemDAO {

    public void insert(Problem problem) throws SQLException {
        String sql = "INSERT INTO problems (site_id, code, title, difficulty, link, tries) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, problem.getSiteId());
            ps.setString(2, problem.getCode());
            ps.setString(3, problem.getTitle());
            ps.setString(4, problem.getDifficulty());
            ps.setString(5, problem.getLink());
            ps.setInt(6, problem.getTries());
            ps.executeUpdate();
        }
    }

    public List<Problem> findAll() throws SQLException {
        List<Problem> result = new ArrayList<>();

        String sql = "SELECT * FROM problems";

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Problem s = new Problem(
                        rs.getInt("id"),
                        rs.getInt("site_id"),
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getString("difficulty"),
                        rs.getString("link"),
                        rs.getInt("tries"));
                result.add(s);
            }
        }
        return result;
    }

    public Problem findById(int id) throws SQLException {
        String sql = "SELECT * FROM problems WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Problem(
                            rs.getInt("id"),
                            rs.getInt("site_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("difficulty"),
                            rs.getString("link"),
                            rs.getInt("tries")
                    );
                }
            }
        }
        return null;
    }

    public List<Problem> findBySiteId(int siteId) throws SQLException {
        List<Problem> result = new ArrayList<>();

        String sql = "SELECT * FROM problems WHERE site_id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, siteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Problem p = new Problem(
                            rs.getInt("id"),
                            rs.getInt("site_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getString("difficulty"),
                            rs.getString("link"),
                            rs.getInt("tries")
                    );
                    result.add(p);
                }
            }
        }
        return result;
    }


    public void update(Problem problem) throws SQLException {
        String sql = """
        UPDATE problems
        SET site_id = ?, code = ?, title = ?, difficulty = ?, link = ?, tries = ?
        WHERE id = ?
    """;

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, problem.getSiteId());
            ps.setString(2, problem.getCode());
            ps.setString(3, problem.getTitle());
            ps.setString(4, problem.getDifficulty());
            ps.setString(5, problem.getLink());
            ps.setInt(6, problem.getTries());
            ps.setInt(7, problem.getId());

            ps.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM problems WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

}
