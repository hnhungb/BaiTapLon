package model.auction;

import model.user.Bidder;

import java.time.LocalDateTime;

/**
 * Configuration for automatic bidding.
 * The system will bid on behalf of the user up to maxBid.
 */
public class AutoBidConfig {

    private final Bidder        bidder;
    private final double        maxBid;
    private final double        increment;
    private final LocalDateTime registeredAt;

    public AutoBidConfig(Bidder bidder, double maxBid, double increment) {
        this.bidder       = bidder;
        this.maxBid       = maxBid;
        this.increment    = increment;
        this.registeredAt = LocalDateTime.now();
    }

    public Bidder        getBidder()      { return bidder; }
    public double        getMaxBid()      { return maxBid; }
    public double        getIncrement()   { return increment; }
    public LocalDateTime getRegisteredAt(){ return registeredAt; }
}