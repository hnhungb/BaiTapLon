package dao;

import database.DatabaseManager;
import model.item.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAOImpl
        implements ItemDAO {

    @Override
    public void save(Item item) {

        String sql =
                """
                INSERT INTO items
                (id,name,description,startPrice,itemType)
                VALUES(?,?,?,?,?)
                """;

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setDouble(4, item.getStartPrice());

            ps.setString(
                    5,
                    item.getClass()
                            .getSimpleName()
            );

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Item item) {

        String sql =
                """
                UPDATE items
                SET name=?,
                    description=?,
                    startPrice=?
                WHERE id=?
                """;

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, item.getName());
            ps.setString(2, item.getDescription());
            ps.setDouble(3, item.getStartPrice());
            ps.setString(4, item.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String itemId) {

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                "DELETE FROM items WHERE id=?")
        ) {

            ps.setString(1, itemId);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Item findById(String itemId) {

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(
                                "SELECT * FROM items WHERE id=?")
        ) {

            ps.setString(1, itemId);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return createItem(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Item> findAll() {

        List<Item> items =
                new ArrayList<>();

        try (
                Connection conn =
                        DatabaseManager.getConnection();

                Statement st =
                        conn.createStatement()
        ) {

            ResultSet rs =
                    st.executeQuery(
                            "SELECT * FROM items");

            while (rs.next()) {

                items.add(
                        createItem(rs)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    private Item createItem(ResultSet rs)
            throws SQLException {

        String type =
                rs.getString("itemType");

        return switch (type) {

            case "Electronics" ->
                    new Electronics(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("startPrice")
                    );

            case "Art" ->
                    new Art(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("startPrice")
                    );

            default ->
                    new Vehicle(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("startPrice")
                    );
        };
    }
}