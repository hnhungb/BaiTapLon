package server;

import com.google.gson.*;
import exception.*;
import model.auction.*;
import model.user.*;
import observer.BidObserver;
import service.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

// Mỗi client kết nối vào sẽ có 1 ClientHandler chạy trên 1 thread riêng
// Handler này nhận lệnh từ client, xử lý, trả về kết quả
public class ClientHandler implements Runnable, BidObserver {

    private Socket socket;           //socket kết nối với client
    private BufferedReader in;       //đọc dlieu từ client
    private PrintWriter out;         //gửi dlieu về client
    private Gson gson;               // Gson dùng để convert Java object <-> JSON
    private AuctionService auctionService;     //server xly auction
    private UserService userService;           //xly user
    private User loggedInUser;                 // user đang đăng nhập trên kết nối này
    // ID phiên đang xem (để nhận push update)
    private String watchingAuctionId;

    public ClientHandler(Socket socket, AuctionService auctionService, UserService userService) {
        this.socket         = socket;
        this.auctionService = auctionService;
        this.userService    = userService;
        this.gson           = new Gson();
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                String response = handleRequest(line);
                out.println(response);
            }

        } catch (IOException e) {
            System.out.println("Client ngắt kết nối: " + socket.getRemoteSocketAddress());
        } finally {
            // Bỏ đăng ký observer khi client disconnect
            if (watchingAuctionId != null) {
                Auction a = auctionService.getAuction(watchingAuctionId);
                if (a != null) a.removeObserver(this);
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    // Nhận bid update từ Auction, đẩy xuống client
    @Override
    public synchronized void update(Bid bid) {
        JsonObject push = new JsonObject();
        push.addProperty("action", Protocol.BID_UPDATE);
        push.addProperty("auctionId", watchingAuctionId); // THÊM DÒNG NÀY
        push.addProperty("bidder", bid.getBidder().getUsername());
        push.addProperty("amount", bid.getAmount());
        push.addProperty("isAuto",   bid.isAuto());
        push.addProperty("time",     bid.getTimeFormatted());
        out.println(gson.toJson(push));
    }

    // Xử lý từng lệnh nhận từ client
    private String handleRequest(String json) {
        try {
            JsonObject req = JsonParser.parseString(json).getAsJsonObject();
            String action = req.get("action").getAsString();

            switch (action) {
                case Protocol.REGISTER:       return doRegister(req);
                case Protocol.LOGIN:          return doLogin(req);
                case Protocol.LIST_AUCTIONS:  return doListAuctions();
                case Protocol.GET_AUCTION:    return doGetAuction(req);
                case Protocol.PLACE_BID:      return doPlaceBid(req);
                case Protocol.AUTO_BID:       return doAutoBid(req);
                case Protocol.CREATE_AUCTION: return doCreateAuction(req);
                case Protocol.CLOSE_AUCTION:  return doCloseAuction(req);
                case Protocol.CANCEL_AUCTION: return doCancelAuction(req);
                default: return error("Lệnh không hợp lệ: " + action);
            }
        } catch (Exception e) {
            return error("Lỗi server: " + e.getMessage());
        }
    }

    //Xử lý từng lệnh

    private String doRegister(JsonObject req) {
        String username = req.get("username").getAsString();
        String password = req.get("password").getAsString();
        String role     = req.has("role") ? req.get("role").getAsString() : "BIDDER";
        String email    = req.has("email") ? req.get("email").getAsString() : username + "@gmail.com";

        User user;
        switch (role.toUpperCase()) {
            case "SELLER": user = new Seller(username, username, password, email); break;
            case "ADMIN":  user = new Admin(username, username, password, email);  break;
            default:       user = new Bidder(username, username, password, email); break;
        }

        boolean ok = userService.register(user);
        if (ok) {
            loggedInUser = user;
            return success(userToJson(user));
        } else {
            return error("Username đã tồn tại hoặc thông tin không hợp lệ");
        }
    }

    private String doLogin(JsonObject req) {
        String username = req.get("username").getAsString();
        String password = req.get("password").getAsString();

        User user = userService.login(username, password);
        if (user != null) {
            loggedInUser = user;
            return success(userToJson(user));
        } else {
            return error("Sai username hoặc password");
        }
    }

    private String doListAuctions() {
        List<Auction> list = auctionService.getAllAuctions();
        JsonArray arr = new JsonArray();
        for (Auction a : list) arr.add(auctionToJson(a));
        return success(arr);
    }

    private String doGetAuction(JsonObject req) {
        String id = req.get("auctionId").getAsString();
        Auction auction = auctionService.getAuction(id);
        if (auction == null) return error("Không tìm thấy phiên: " + id);

        // Đăng ký nhận push update cho phiên này
        if (watchingAuctionId != null) {
            Auction old = auctionService.getAuction(watchingAuctionId);
            if (old != null) old.removeObserver(this);
        }
        watchingAuctionId = id;
        auction.addObserver(this);

        return success(auctionToJson(auction));
    }

    private String doPlaceBid(JsonObject req) {
        if (loggedInUser == null) return error("Chưa đăng nhập");

        String auctionId = req.get("auctionId").getAsString();
        double amount    = req.get("amount").getAsDouble();

        try {
            auctionService.placeBid(auctionId, loggedInUser.getUsername(), amount);
            Auction a = auctionService.getAuction(auctionId);
            return success(auctionToJson(a));
        } catch (InvalidBidException e) {
            return error("Giá không hợp lệ: " + e.getMessage());
        } catch (AuctionClosedException e) {
            return error("Phiên đã đóng: " + e.getMessage());
        }
    }

    private String doAutoBid(JsonObject req) {
        if (loggedInUser == null) return error("Chưa đăng nhập");

        String auctionId = req.get("auctionId").getAsString();
        double maxBid    = req.get("maxBid").getAsDouble();
        double step      = req.get("step").getAsDouble();

        try {
            auctionService.registerAutoBid(auctionId, loggedInUser.getUsername(), maxBid, step);
            return success(new JsonPrimitive("Đăng ký auto-bid thành công!"));
        } catch (AuctionClosedException e) {
            return error(e.getMessage());
        }
    }

    private String doCreateAuction(JsonObject req) {
        if (loggedInUser == null) return error("Chưa đăng nhập");

        String id       = "A" + System.currentTimeMillis();
        String itemType = req.get("itemType").getAsString();
        String itemName = req.get("itemName").getAsString();
        String itemDesc = req.has("itemDesc") ? req.get("itemDesc").getAsString() : "";
        double price    = req.get("startPrice").getAsDouble();
        int    duration = req.has("duration") ? req.get("duration").getAsInt() : 60;

        Auction auction = auctionService.createAuction(
                id, loggedInUser.getUsername(), itemType, itemName, itemDesc, price, duration
        );
        return success(auctionToJson(auction));
    }

    private String doCloseAuction(JsonObject req) {
        if (loggedInUser == null) return error("Chưa đăng nhập");
        String id = req.get("auctionId").getAsString();
        try {
            auctionService.closeAuction(id);
            return success(new JsonPrimitive("Đã đóng phiên: " + id));
        } catch (AuctionClosedException e) {
            return error(e.getMessage());
        }
    }

    private String doCancelAuction(JsonObject req) {
        if (loggedInUser == null) return error("Chưa đăng nhập");
        auctionService.cancelAuction(req.get("auctionId").getAsString());
        return success(new JsonPrimitive("Đã hủy phiên"));
    }

    // JSON helpers

    private JsonObject auctionToJson(Auction a) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",           a.getId());
        obj.addProperty("itemName",     a.getItem().getName());
        obj.addProperty("itemType",     a.getItem().getType());
        obj.addProperty("itemDesc",     a.getItem().getDescription());
        obj.addProperty("startPrice",   a.getItem().getStartPrice());
        obj.addProperty("currentPrice", a.getCurrentPrice());
        obj.addProperty("status",       a.getStatus().name());
        obj.addProperty("endTime",      a.getEndTimeFormatted());
        obj.addProperty("winner",       a.getWinner() != null ? a.getWinner().getBidder().getUsername() : "");
        obj.addProperty("leader",       a.getWinningBid() != null ? a.getWinningBid().getBidder().getUsername() : "");

        // Lịch sử bid
        JsonArray history = new JsonArray();
        for (Bid b : a.getBids()) {
            JsonObject bObj = new JsonObject();
            bObj.addProperty("bidder", b.getBidder().getUsername());
            bObj.addProperty("amount", b.getAmount());
            bObj.addProperty("time",   b.getTimeFormatted());
            bObj.addProperty("isAuto", b.isAuto());
            history.add(bObj);
        }
        obj.add("history", history);

        return obj;
    }

    private JsonObject userToJson(User u) {
        JsonObject obj = new JsonObject();
        obj.addProperty("username", u.getUsername());
        obj.addProperty("role",     u.getRole());
        return obj;
    }

    private String success(JsonElement data) {
        JsonObject resp = new JsonObject();
        resp.addProperty("ok", true);
        resp.add("data", data);
        return gson.toJson(resp);
    }

    private String error(String msg) {
        JsonObject resp = new JsonObject();
        resp.addProperty("ok",    false);
        resp.addProperty("error", msg);
        return gson.toJson(resp);
    }
}
