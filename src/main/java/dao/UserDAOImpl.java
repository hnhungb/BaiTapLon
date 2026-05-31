package dao;

import database.DatabaseManager;
import model.user.Admin;
import model.user.Bidder;
import model.user.Seller;
import model.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void save(User user) {

        String sql =
                """
                INSERT INTO users
                (id,username,password,role,email)
                VALUES(?,?,?,?,?)
                """;

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            ps.setString(5, user.getEmail());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User findByUsername(String username) {

        String sql =
                "SELECT * FROM users WHERE username=?";

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String role =
                        rs.getString("role");

                return switch (role) {

                    case "ADMIN" ->
                            new Admin(
                                    rs.getString("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email")
                            );

                    case "SELLER" ->
                            new Seller(
                                    rs.getString("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email")
                            );

                    default ->
                            new Bidder(
                                    rs.getString("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("email")
                            );
                };
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<User> findAll() {

        List<User> users =
                new ArrayList<>();

        String sql =
                "SELECT * FROM users";

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                Statement stmt =
                        conn.createStatement()
        ) {

            ResultSet rs =
                    stmt.executeQuery(sql);

            while (rs.next()) {

                User user;

                String role =
                        rs.getString("role");

                switch (role) {

                    case "ADMIN":
                        user = new Admin(
                                rs.getString("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                        break;

                    case "SELLER":
                        user = new Seller(
                                rs.getString("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                        break;

                    default:
                        user = new Bidder(
                                rs.getString("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                }

                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
}
