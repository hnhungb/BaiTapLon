package model.auction;

import model.user.Bidder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Một lần đặt giá
public class Bid {

    private Bidder        bidder;
    private double        amount;
    private LocalDateTime time;
    private boolean       isAuto; // true = do auto-bid tự đặt

    public Bid(Bidder bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;
        this.time   = LocalDateTime.now();
        this.isAuto = false;
    }

    public Bid(Bidder bidder, double amount, boolean isAuto) {
        this(bidder, amount);
        this.isAuto = isAuto;
    }

    public Bidder        getBidder() { return bidder; }
    public double        getAmount() { return amount; }
    public LocalDateTime getTime()   { return time; }
    public boolean       isAuto()    { return isAuto; }

    public String getTimeFormatted() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    // Dùng để gửi qua socket (format đơn giản)
    public String getName() { return bidder.getUsername(); }

    @Override
    public String toString() {
        String tag = isAuto ? " [auto]" : "";
        return bidder.getUsername() + " đặt " + amount + " lúc " + getTimeFormatted() + tag;
    }
}
