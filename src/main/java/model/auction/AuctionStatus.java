package model.auction;

public enum AuctionStatus {
    OPEN,       // created, not yet started
    RUNNING,    // accepting bids
    FINISHED,   // time expired, winner determined
    PAID,       // winner paid
    CANCELED    // canceled before finish
}