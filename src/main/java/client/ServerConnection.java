package client;

import com.google.gson.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerConnection {

    private static final String HOST = System.getProperty("server.host", "localhost");

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private final Gson gson = new Gson();

    private final BlockingQueue<JsonObject> responses = new LinkedBlockingQueue<>();
    private MessageListener listener;

    public interface MessageListener {
        void onMessage(JsonObject msg);
    }

    public void setListener(MessageListener listener) {
        this.listener = listener;
    }

    public boolean connect() {
        try {
            socket = new Socket(HOST, server.Protocol.PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            startReaderThread();
            return true;
        } catch (IOException e) {
            System.err.println("Không thể kết nối server: " + e.getMessage());
            return false;
        }
    }

    private void startReaderThread() {
        Thread t = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    JsonObject msg = JsonParser.parseString(line).getAsJsonObject();
                    // Luôn ưu tiên đẩy cho listener xử lý nếu đó là tin nhắn PUSH từ server
                    if (msg.has("action") && listener != null) {
                        listener.onMessage(msg);
                    } else {
                        // Nếu là phản hồi cho lệnh vừa gửi (hỏi-đáp)
                        responses.offer(msg);
                    }
                }
            } catch (Exception ignored) {}
        });
        t.setDaemon(true);
        t.start();
    }

    public JsonObject send(JsonObject request) {
        try {
            writer.println(gson.toJson(request));
            return responses.take();
        } catch (Exception e) {
            JsonObject obj = new JsonObject();
            obj.addProperty("ok", false);
            obj.addProperty("error", e.getMessage());
            return obj;
        }
    }

    public static JsonObject req(String action) {
        JsonObject obj = new JsonObject();
        obj.addProperty("action", action);
        return obj;
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public void disconnect() {
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
    }
}
