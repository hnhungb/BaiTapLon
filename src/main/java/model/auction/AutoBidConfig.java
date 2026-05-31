package model.auction;

import model.user.Bidder;
import java.time.LocalDateTime;

// Cấu hình đặt giá tự động (auto-bid)
// Hệ thống sẽ tự đặt giá thay người dùng khi có ai đó vượt qua họ
public class AutoBidConfig {

    private Bidder        bidder;
    private double        maxBid;      // giá cao nhất mà người đó có thể trả
    private double        step;        // bước nhảy: mỗi lần ra giá tăng bao nhiêu
    private LocalDateTime registeredAt;

    public AutoBidConfig(Bidder bidder, double maxBid, double step) {
        this.bidder       = bidder;
        this.maxBid       = maxBid;
        this.step         = step;
        this.registeredAt = LocalDateTime.now();
    }

    public Bidder        getBidder()       { return bidder; }
    public double        getMaxBid()       { return maxBid; }
    public double        getStep()         { return step; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}
