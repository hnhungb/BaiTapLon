package client;

import com.google.gson.*;

import java.io.*;
import java.net.Socket;

// Quản lý kết nối TCP đến server
// Gửi lệnh JSON, nhận kết quả JSON
public class ServerConnection {

    private static final String HOST = "localhost";

    private Socket       socket;
    private BufferedReader reader;
    private PrintWriter   writer;
    private Gson         gson = new Gson();

    // Kết nối đến server
    public boolean connect() {
        try {
            socket = new Socket(HOST, server.Protocol.PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            System.err.println("Không thể kết nối server: " + e.getMessage());
            return false;
        }
    }

    // Gửi 1 lệnh và đọc 1 dòng kết quả
    public JsonObject send(JsonObject request) {
        try {
            writer.println(gson.toJson(request));
            String line = reader.readLine();
            if (line == null) return errorJson("Mất kết nối server");
            return JsonParser.parseString(line).getAsJsonObject();
        } catch (IOException e) {
            return errorJson("Lỗi kết nối: " + e.getMessage());
        }
    }

    // Đọc 1 dòng từ server (dùng để nhận push update)
    public String readLine() throws IOException {
        return reader.readLine();
    }

    // Tạo request JSON đơn giản
    public static JsonObject req(String action) {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", action);
        return obj;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void disconnect() {
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
    }

    private JsonObject errorJson(String msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("ok", false);
        obj.addProperty("error", msg);
        return obj;
    }
}
