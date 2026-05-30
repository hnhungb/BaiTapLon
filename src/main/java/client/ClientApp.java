package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Entry point của ứng dụng JavaFX
public class ClientApp extends Application {

    // Dùng static để các controller khác có thể truy cập
    private static ServerConnection connection;

    @Override
    public void start(Stage stage) throws Exception {
        // Kết nối server
        connection = new ServerConnection();
        boolean connected = connection.connect();

        if (!connected) {
            System.err.println("Chạy server trước! (server.AuctionServer)");
        }

        // Load màn hình đăng nhập
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setConnection(connection);
        ctrl.setStage(stage);

        stage.setTitle("Hệ thống Đấu Giá Trực Tuyến");
        stage.setScene(new Scene(root, 420, 320));
        stage.show();
    }

    @Override
    public void stop() {
        if (connection != null) connection.disconnect();
        AuctionManager.shutdown();
    }

    public static ServerConnection getConnection() { return connection; }

    // Để stop AuctionManager khi thoát
    private static class AuctionManager {
        static void shutdown() {
            service.AuctionManager.getInstance().shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
