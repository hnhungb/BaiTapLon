package server;

import model.user.*;
import service.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Server chính- chờ client kết nối + tạo thread xử lý từng client
public class AuctionServer {
    public static void main(String[] args) throws IOException {
        UserService    userService    = new UserService();   //service qly user
        AuctionService auctionService = new AuctionService(userService);   //qly auction

        themDataMau(userService, auctionService); //data mẫu để test
        System.out.println("Server chạy tại port " + Protocol.PORT );

        ServerSocket serverSocket = new ServerSocket(Protocol.PORT);

        while (true) {      //cho server chạy liên tục
            Socket clientSocket = serverSocket.accept();     //chờ cho client kết nối
            System.out.println("Client kết nối: " + clientSocket.getRemoteSocketAddress());

            ClientHandler handler = new ClientHandler(clientSocket, auctionService, userService);
            Thread thread = new Thread(handler);               // Mỗi client chạy trên 1 thread riêng
            thread.start();
        }
    }

    private static void themDataMau(UserService us, AuctionService as) {
        // Tạo users mẫu
        us.register(new Admin("admin1",  "admin",   "admin123",  "admin@auction.com"));
        us.register(new Seller("s1",     "alice",   "123456",    "alice@gmail.com"));
        us.register(new Bidder("b1",     "bob",     "123456",    "bob@gmail.com"));
        us.register(new Bidder("b2",     "charlie", "123456",    "charlie@gmail.com"));

        // Tạo phiên đấu giá mẫu (60 phút)

        as.createAuction("A001", "seller1", "ELECTRONICS", "iPhone 18",
                "Mới 100%, fullbox", 20000, 10);

        as.createAuction("A002", "seller1", "ART", "Tranh Sơn Dầu ",
                "Sơn dầu trên canvas 60x80cm", 5000, 10);

        as.createAuction("A003", "seller1", "VEHICLE", "Honda Wave Alpha 2022",
                "Còn mới, đi 5000km", 50000, 10);

        System.out.println("Đã tạo data mẫu: 4 users, 3 phiên đấu giá");

    }
}
