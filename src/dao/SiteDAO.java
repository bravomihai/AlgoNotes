package dao;

import db.Database;
import model.Site;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SiteDAO {

    public void insert(Site site) throws SQLException {
        String sql = "INSERT INTO sites (name, url) VALUES (?, ?)";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, site.getName());
            ps.setString(2, site.getUrl());
            ps.executeUpdate();
        }
    }

    public List<Site> findAll() throws SQLException {
        List<Site> result = new ArrayList<>();

        String sql = "SELECT * FROM sites";

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Site s = new Site(rs.getInt("id"), rs.getString("name"), rs.getString("url"));
                result.add(s);
            }
        }
        return result;
    }

    public Site findById(int id) throws SQLException {
        String sql = "SELECT id, name, url FROM sites WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Site(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("url")
                    );
                }
            }
        }
        return null;
    }

    public void update(Site site) throws SQLException {
        String sql = "UPDATE sites SET name = ?, url = ? WHERE id = ?";

        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, site.getName());
            ps.setString(2, site.getUrl());
            ps.setInt(3, site.getId());
            ps.executeUpdate();
        }
    }

    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM sites WHERE id = ?";

        try (Connection c = Database.getConnection();

             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


}
