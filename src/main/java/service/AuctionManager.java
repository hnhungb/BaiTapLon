package service;

import java.util.ArrayList;
import java.util.List;

public class AuctionManager {

    private static AuctionManager instance;
    private List<Auction> auctions = new ArrayList<>();

    private AuctionManager() {}

    public static AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void addAuction(Auction auction) {
        auctions.add(auction);
    }

    public List<Auction> getAuctions() {
        return auctions;
    }
}
