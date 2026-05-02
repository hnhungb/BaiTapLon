package service;

import exception.*;
import model.auction.*;
import model.item.*;

import java.util.HashMap;
import java.util.Map;

public class AuctionService {

    // lưu tất cả auction theo id
    private Map<String, Auction> auctionMap = new HashMap<>();

    // tạo auction
    public void createAuction(String auctionId, Item item) {
        Auction auction = new Auction(item);
        auctionMap.put(auctionId, auction);

        // cũng add vào manager (Singleton)
        AuctionManager.getInstance().addAuction(auction);
    }

    // lấy auction
    public Auction getAuction(String auctionId) {
        return auctionMap.get(auctionId);
    }

    // đặt giá
    public void placeBid(String auctionId, Bid bid)
            throws InvalidBidException, AuctionClosedException {

        Auction auction = auctionMap.get(auctionId);

        if (auction == null) {
            throw new RuntimeException("Auction not found");
        }

        auction.placeBid(bid);
    }

    // đóng auction
    public void closeAuction(String auctionId) {
        Auction auction = auctionMap.get(auctionId);

        if (auction != null) {
            try {
                auction.closeAuction();
            } catch (AuctionClosedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}