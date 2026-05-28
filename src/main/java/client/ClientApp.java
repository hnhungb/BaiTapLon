package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX client application entry point.
 * Run: mvn javafx:run
 */
public class ClientApp extends Application {

    private static ServerConnection connection;

    @Override
    public void start(Stage primaryStage) throws Exception {
        connection = new ServerConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            System.err.println("Could not connect to server: " + e.getMessage());
            // Allow running in offline/demo mode
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setConnection(connection);
        ctrl.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Online Auction System");
        primaryStage.setScene(new Scene(root, 480, 500));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void stop() {
        if (connection != null) connection.disconnect();
    }

    public static ServerConnection getConnection() { return connection; }

    public static void main(String[] args) {
        launch(args);
    }
}
