package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlite:auction.db";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }

    public static void initialize() {

        try (
                Connection conn = getConnection();
                Statement stmt = conn.createStatement()
        ) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users(
                    id TEXT PRIMARY KEY,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL,
                    email TEXT
                )
            """);
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS auctions(
            id TEXT PRIMARY KEY,
            itemId TEXT,
            currentPrice REAL,
            startTime TEXT,
            endTime TEXT,
            status TEXT,
            winnerId TEXT,
            FOREIGN KEY(itemId)
            REFERENCES items(id)
               )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
