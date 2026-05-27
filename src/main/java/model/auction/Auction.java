package model.auction;

import exception.AuctionClosedException;
import exception.InvalidBidException;
import model.item.Item;
import model.user.Bidder;
import model.user.Seller;
import observer.BidObserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// Một phiên đấu giá
public class Auction {

    // Thông tin cơ bản 
    private String       id;
    private Item         item;  
    private Seller       seller;      // người đăng bán
    private double       currentPrice;  // giá cao nhất hiện tại
    private Bid          winningBid;  // bid cao nhất hiện tại
    private List<Bid>    bids;    //lịch sử đấu giá

    // Trạng thái và thời gian
    private AuctionStatus status;
    private LocalDateTime  startTime;
    private LocalDateTime  endTime;

    // Danh sách auto-bid (auto-bit: tính năng trả giá tự động dựa trên giá hiện tại và  giá max bid)
    //Lưu cấu hình kiểu: người A: giá max bid sẵn sàng trả,bước nhảy
    private List<AutoBidConfig> autoBidList;

    // Danh sách observer 
    private List<BidObserver> observers;

    // Lock để tránh race condition khi nhiều người đặt cùng lúc hoặc ghi đè dữ liệu(Concurrency)
    private ReentrantLock lock = new ReentrantLock();

    // Anti-snipe: cơ chế ngăn chặn sniper
    // Nếu có bid trong 30s cuối thì gia hạn thêm 60s
    private static final int ANTI_SNIPE_WINDOW = 30;
    private static final int ANTI_SNIPE_EXTEND = 60;


    // Constructor chính
    public Auction(String id, Item item, Seller seller, int durationMinutes) {
        this.id           = id;
        this.item         = item;
        this.seller       = seller;
        this.currentPrice = item.getStartPrice();  //set giá ban đầu
        this.status       = AuctionStatus.RUNNING;  //trạng thái = RUNNING
        this.startTime    = LocalDateTime.now();                   //set thời gian: startTime = now 
        this.endTime      = LocalDateTime.now().plusMinutes(durationMinutes); //endTime = now + duration
        this.bids         = new ArrayList<>();
        this.autoBidList  = new ArrayList<>();
        this.observers    = new ArrayList<>();
    }

    // Constructor test nhanh: Auto : id = timestamp, duration = 60p 
    public Auction(Item item) {
        this("auto-" + System.currentTimeMillis(), item, null, 60);
    }

    //Observer (realtime update): quan sát và cập nhật thời gian thực
    // đăng kí
    public void addObserver(BidObserver observer) {
        observers.add(observer);
    }
    //hủy đăng kí
    public void removeObserver(BidObserver observer) {
        observers.remove(observer);
    }
    //khi có bid gọi update cho tất cả observer
    private void notifyObservers(Bid bid) {
        for (BidObserver o : observers) {
            o.update(bid);
        }
    }

    //Quan trọng
    public boolean placeBid(Bid bid)
            throws InvalidBidException, AuctionClosedException {

        lock.lock(); // tránh 2 người đặt cùng lúc bị lỗi, đảm bảo thread-safe
        try {
            // Kiểm tra trạng thái: phiên còn mở không
            if (status != AuctionStatus.RUNNING) {
                throw new AuctionClosedException("Phiên đấu giá đã đóng! Trạng thái: " + status);
            }

            // Tự đóng nếu hết giờ
            if (LocalDateTime.now().isAfter(endTime)) {
                status = AuctionStatus.FINISHED;
                throw new AuctionClosedException("Phiên đấu giá đã hết thời gian!");
            }

            // Kiểm tra giá hợp lệ 
            if (bid.getAmount() <= currentPrice) {
                throw new InvalidBidException(
                    "Giá đặt (" + bid.getAmount() + ") phải lớn hơn giá hiện tại (" + currentPrice + ")"
                );
            }

            // Cập nhật: lưu bid
            currentPrice = bid.getAmount();
            winningBid   = bid;
            bids.add(bid);

            // Anti-snipe: gia hạn nếu đặt trong 30s cuối
            applyAntiSnipe();

            // Thông báo cho tất cả observer
            notifyObservers(bid);

            // Kiểm tra xem có auto-bid nào cần kích hoạt không
            triggerAutoBid(bid);

            return true;

        } finally {
            lock.unlock();
        }
    }

    // Anti-snipe 
    private void applyAntiSnipe() {
        LocalDateTime threshold = endTime.minusSeconds(ANTI_SNIPE_WINDOW);
        if (LocalDateTime.now().isAfter(threshold)) {
            endTime = endTime.plusSeconds(ANTI_SNIPE_EXTEND);
            System.out.println("⏰ Anti-snipe! Gia hạn thêm " + ANTI_SNIPE_EXTEND + "s. Kết thúc mới: " + getEndTimeFormatted());
        }
    }

    //Auto-bid

    public void registerAutoBid(AutoBidConfig config) {
        // Nếu người này đã đăng ký rồi thì replace
        autoBidList.removeIf(c -> c.getBidder().getId().equals(config.getBidder().getId()));
        autoBidList.add(config);
    }

    // Sau khi có bid mới, kích hoạt auto-bid của đối thủ (nếu đủ điều kiện)
    private void triggerAutoBid(Bid justPlaced) {
        // Sắp xếp theo thời gian đăng ký - ai đăng ký trước được ưu tiên
        autoBidList.sort((a, b) -> a.getRegisteredAt().compareTo(b.getRegisteredAt()));
        // duyệt từng config
        for (AutoBidConfig config : autoBidList) {
            // Bỏ qua người vừa đặt
            if (config.getBidder().getId().equals(justPlaced.getBidder().getId())) {
                continue;
            }
            //tính giá tiếp
            double nextPrice = currentPrice + config.getStep();

            // Nếu giá tiếp theo vẫn trong giới hạn maxBid thì auto đặt
            if (nextPrice <= config.getMaxBid()) {
                Bid autoBid = new Bid(config.getBidder(), nextPrice, true); //tạo lượt mới
                // Cập nhật trạng thía phiên đấu giá
                currentPrice = nextPrice;   //cập nhật giá hiện tại 
                winningBid   = autoBid;     // ghi nhận lượt giá cao nhất
                bids.add(autoBid);          // thêm lượt này vào dnah sách lịch sử đấu giá
                applyAntiSnipe();           //Anti-snipe
                notifyObservers(autoBid);   //notify
                System.out.println("🤖 Auto-bid: " + config.getBidder().getUsername() + " tự đặt " + nextPrice);
                break; // Chỉ 1 auto-bid kích hoạt mỗi lượt
            }
        }
    }

    // Đóng phiên đấu giá
    public void closeAuction() throws AuctionClosedException {
        lock.lock();
        try {
            if (status == AuctionStatus.FINISHED || status == AuctionStatus.CANCELED) {
                throw new AuctionClosedException("Phiên đã đóng rồi!");
            }
            // nếu chưa đóng thì:
            status = AuctionStatus.FINISHED;

            // thông báo kết quả 
            System.out.println("=== Phiên đấu giá KẾT THÚC ===");
            Bid winner = getWinner();
            // trường hợp có người đặt giá
            if (winner != null) {
                System.out.println("Người thắng: " + winner.getBidder().getUsername()
                        + " | Giá: " + winner.getAmount());
                winner.getBidder().tangSoLanThang();
                if (seller != null) seller.tangSoSanPhamDaBan();
            } else {
                System.out.println("Không có ai đặt giá.");
            }
        } finally {
            lock.unlock();
        }
    }
    // Hủy phiên đấu giá: do nhiều nguyên nhân:sản phẩm lỗi, vi phạm quy định, người bán không muốn bán sản phẩm
    public void cancelAuction() {
        status = AuctionStatus.CANCELED;
        System.out.println("Phiên " + id + " đã bị hủy.");
    }

    // dùng cho scheduler - tự đóng khi hết giờ
    public void checkAndAutoClose() {
        if (status == AuctionStatus.RUNNING && LocalDateTime.now().isAfter(endTime)) {
            try {
                closeAuction();
            } catch (AuctionClosedException e) {
                //ignore
            }
        }
    }

    //Getters

    public String         getId()           { return id; }
    public Item           getItem()         { return item; }
    public Seller         getSeller()       { return seller; }
    public double         getCurrentPrice() { return currentPrice; }
    public AuctionStatus  getStatus()       { return status; }
    public LocalDateTime  getStartTime()    { return startTime; }
    public LocalDateTime  getEndTime()      { return endTime; }
    public List<Bid>      getBids()         { return new ArrayList<>(bids); }
    public Bid            getWinningBid()   { return winningBid; }

    // Kiểm tra
    public boolean isClosed() { return status != AuctionStatus.RUNNING; }

    // Lấy người thắng
    public Bid getWinner() {
        return winningBid;
    }
    // Format thời gian:
    public String getEndTimeFormatted() {
        if (endTime == null) return "N/A";
        return endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        return "Phiên [" + id + "] " + item.getName()
                + " | Giá: " + currentPrice
                + " | " + status
                + " | Kết thúc: " + getEndTimeFormatted();
    }
}
