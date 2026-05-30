package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import server.Protocol;

public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label         statusLabel;
    @FXML private TabPane       tabPane;

    private ServerConnection connection;
    private Stage             primaryStage;

    @FXML
    public void initialize() {
        if (roleCombo != null) {
            roleCombo.getItems().addAll("BIDDER", "SELLER");
            roleCombo.setValue("BIDDER");
        }
    }

    public void setConnection(ServerConnection conn) { this.connection = conn; }
    public void setPrimaryStage(Stage stage)         { this.primaryStage = stage; }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter username and password.", true);
            return;
        }

        new Thread(() -> {
            ObjectNode payload = connection.payload();
            payload.put("username", username);
            payload.put("password", password);
            JsonNode resp = connection.send(Protocol.LOGIN, payload);

            Platform.runLater(() -> {
                if (resp.path("success").asBoolean()) {
                    String role = resp.path("data").path("role").asText();
                    openMainWindow(username, role);
                } else {
                    setStatus("Login failed: " + resp.path("error").asText(), true);
                }
            });
        }).start();
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleCombo != null ? roleCombo.getValue() : "BIDDER";

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please fill in all fields.", true);
            return;
        }

        new Thread(() -> {
            ObjectNode payload = connection.payload();
            payload.put("username", username);
            payload.put("password", password);
            payload.put("role",     role);
            JsonNode resp = connection.send(Protocol.REGISTER, payload);

            Platform.runLater(() -> {
                if (resp.path("success").asBoolean()) {
                    setStatus("Registered! You are now logged in.", false);
                    openMainWindow(username, role);
                } else {
                    setStatus("Registration failed: " + resp.path("error").asText(), true);
                }
            });
        }).start();
    }

    private void openMainWindow(String username, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            Parent root = loader.load();
            MainController ctrl = loader.getController();
            ctrl.setConnection(connection);
            ctrl.setCurrentUser(username, role);
            ctrl.loadAuctions();

            Stage stage = new Stage();
            stage.setTitle("Auction System — " + username + " (" + role + ")");
            stage.setScene(new Scene(root, 960, 640));
            stage.show();
            primaryStage.close();
        } catch (Exception e) {
            setStatus("Error opening main window: " + e.getMessage(), true);
        }
    }

    private void setStatus(String msg, boolean error) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.setStyle(error ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        }
    }
}