package model.auction;

import model.entity.Entity;

import java.time.LocalDateTime;

/**
 * Immutable record of a successfully placed Bid.
 * Stored in Auction's bid history.
 */
public class BidTransaction extends Entity {

    private final String        auctionId;
    private final Bid           bid;
    private final double        priceAfterBid;
    private final LocalDateTime recordedAt;

    public BidTransaction(String auctionId, Bid bid, double priceAfterBid) {
        super();
        this.auctionId     = auctionId;
        this.bid           = bid;
        this.priceAfterBid = priceAfterBid;
        this.recordedAt    = LocalDateTime.now();
    }

    public String        getAuctionId()    { return auctionId; }
    public Bid           getBid()          { return bid; }
    public double        getPriceAfterBid(){ return priceAfterBid; }
    public LocalDateTime getRecordedAt()   { return recordedAt; }

    @Override
    public String printInfo() {
        return String.format("BidTransaction[auction=%s  bidder=%s  price=%.2f  at=%s]",
                auctionId, bid.getBidder().getUsername(), priceAfterBid, recordedAt);
    }
}
