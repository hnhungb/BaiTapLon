# Hệ thống Đấu Giá
## Cấu trúc dự án

```
src/
 ├──main/
 │    ├──java/
 │    │    ├──client/            ClientApp, LoginController, MainController, ServerConnection
 │    │    ├──exception/         AuctionClosedException, InvalidBidException
 │    │    ├──Factory/           ItemFactory
 │    │    ├──observer/          BidderClient, BidObserver(Interface)
 │    │    ├──server/            AuctionServer, ClientHandler, Protocol
 │    │    ├──service/           AuctionManager(Singleton), AuctionServiec, Userservice
 │    │    └──model/
 │    │         ├──auction/      Auction, AuctionStatus, AutoBidCongig, Bid
 │    │         ├──Item/         Item(abstract), Art, Electronics, Vehicle, ItemType
 │    │         └──User/         User(abstract), Admin, Bidder, Seller
 │    └──view/
 │         ├──Login.fxml
 │         └──Main.fxml
 └──Test/
      └──AuctionTest
```


## Design Patterns đã dùng

| Pattern        | Ở đâu                                             |
|:---------------|:--------------------------------------------------|
| **Singleton**  | `AuctionManager` - chỉ có 1 instance duy nhất     |
| **Factory**    | `ItemFactory` - tạo Electronics / Art / Vehicle   |
| **Observer**   | `BidObserver` - thông báo realtime khi có bid     |


## Tính năng

**Bắt buộc:**
- Đăng ký / Đăng nhập với 3 vai trò: Bidder, Seller, Admin
- Tạo và quản lý sản phẩm đấu giá
- Đặt giá, kiểm tra hợp lệ, cập nhật người dẫn đầu
- Trạng thái phiên: OPEN → RUNNING → FINISHED → PAID / CANCELED
- Xử lý lỗi: InvalidBidException, AuctionClosedException
- GUI JavaFX + FXML
- Kiến trúc Client-Server (TCP Socket + JSON)
- Concurrent bidding an toàn (synchronized)
- Realtime update (Observer Pattern + Socket push)

**Nâng cao:**
- Auto-bidding (maxBid + bước tăng, ưu tiên người đăng ký trước)
- Anti-sniping (gia hạn 60s nếu có bid trong 30s cuối)
- Biểu đồ giá realtime (JavaFX LineChart)

---

## Cách chạy

### Yêu cầu: Java 17+, Maven 3.8+

### Bước 1: Chạy Server
```bash
mvn compile
mvn exec:java -Dexec.mainClass="server.AuctionServer"
```
Server khởi động ở cổng **9090**, tự tạo sẵn data mẫu.

### Bước 2: Chạy Client (cửa sổ mới)
```bash
mvn javafx:run
```

### Tài khoản mẫu
| Username | Password | Vai trò |
|----------|----------|---------|
| admin    | admin123 | Admin   |
| alice    | 123456   | Seller  |
| bob      | 123456   | Bidder  |
| charlie  | 123456   | Bidder  |

---

## Chạy Test
```bash
mvn test
```

---

## Sơ đồ kế thừa (OOP)

```
User (abstract)
├── Bidder       - getRole() = "BIDDER"
├── Seller       - getRole() = "SELLER"
└── Admin        - getRole() = "ADMIN"

Item (abstract)
├── Electronics  - getType() = "Electronics"
├── Art          - getType() = "Art"
└── Vehicle      - getType() = "Vehicle"
```
