package client;

import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.Protocol;

public class LoginController {

    // Panel đăng nhập - mỗi field có id riêng, không trùng với panel đăng ký
    @FXML private VBox          panelLogin;
    @FXML private TextField     usernameField;      // chỉ dùng cho login
    @FXML private PasswordField passwordField;      // chỉ dùng cho login
    @FXML private Label         statusLabel;

    // Panel đăng ký - id riêng biệt hoàn toàn
    @FXML private VBox          panelRegister;
    @FXML private TextField     regUsernameField;   // chỉ dùng cho register
    @FXML private PasswordField regPasswordField;   // chỉ dùng cho register
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label         regStatusLabel;

    private ServerConnection connection;
    private Stage             stage;

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("BIDDER", "SELLER");
        roleCombo.setValue("BIDDER");
    }

    public void setConnection(ServerConnection conn) { this.connection = conn; }
    public void setStage(Stage stage)                { this.stage = stage; }

    // Chuyển sang màn hình đăng ký
    @FXML
    private void showRegister() {
        panelLogin.setVisible(false);
        panelLogin.setManaged(false);
        panelRegister.setVisible(true);
        panelRegister.setManaged(true);
        regStatusLabel.setText("");
    }

    // Chuyển về màn hình đăng nhập
    @FXML
    private void showLogin() {
        panelRegister.setVisible(false);
        panelRegister.setManaged(false);
        panelLogin.setVisible(true);
        panelLogin.setManaged(true);
        statusLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        // Đọc từ đúng field của panel login
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Vui lòng nhập đủ thông tin!", true);
            return;
        }

        new Thread(() -> {
            JsonObject req = ServerConnection.req(Protocol.LOGIN);
            req.addProperty("username", username);
            req.addProperty("password", password);

            JsonObject resp = connection.send(req);

            Platform.runLater(() -> {
                if (resp.get("ok").getAsBoolean()) {
                    String role = resp.getAsJsonObject("data").get("role").getAsString();
                    moManHinhChinh(username, role);
                } else {
                    showStatus("Lỗi: " + resp.get("error").getAsString(), true);
                }
            });
        }).start();
    }

    @FXML
    private void handleRegister() {
        // Đọc từ đúng field của panel register
        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText().trim();
        String role     = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showRegStatus("Vui lòng nhập đủ thông tin!", true);
            return;
        }

        new Thread(() -> {
            JsonObject req = ServerConnection.req(Protocol.REGISTER);
            req.addProperty("username", username);
            req.addProperty("password", password);
            req.addProperty("role",     role);

            JsonObject resp = connection.send(req);

            Platform.runLater(() -> {
                if (resp.get("ok").getAsBoolean()) {
                    showRegStatus("Đăng ký thành công!", false);
                    moManHinhChinh(username, role);
                } else {
                    showRegStatus("Lỗi: " + resp.get("error").getAsString(), true);
                }
            });
        }).start();
    }

    private void moManHinhChinh(String username, String role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            Parent root = loader.load();

            MainController ctrl = loader.getController();
            ctrl.setConnection(connection);
            ctrl.setCurrentUser(username, role);
            ctrl.loadDanhSachPhien();

            stage.setTitle("Đấu Giá - Xin chào " + username + " [" + role + "]");
            stage.setScene(new Scene(root, 950, 620));
        } catch (Exception e) {
            showStatus("Lỗi mở cửa sổ chính: " + e.getMessage(), true);
        }
    }

    // Status cho panel login
    private void showStatus(String msg, boolean isError) {
        statusLabel.setText(msg);
        statusLabel.setStyle(isError ? "-fx-text-fill:red;" : "-fx-text-fill:green;");
    }

    // Status cho panel register
    private void showRegStatus(String msg, boolean isError) {
        regStatusLabel.setText(msg);
        regStatusLabel.setStyle(isError ? "-fx-text-fill:red;" : "-fx-text-fill:green;");
    }
}
