package service;

import exception.*;
import Factory.ItemFactory;
import model.auction.*;
import model.item.Item;
import model.user.*;

import java.util.List;

// Xử lý các nghiệp vụ đấu giá
// Mở rộng từ code gốc: thêm createAuction với Seller, đặt auto-bid
public class AuctionService {

    private UserService    userService;

    public AuctionService(UserService userService) {
        this.userService = userService;
    }

    // Tạo phiên đấu giá mới
    public Auction createAuction(String auctionId, String sellerUsername,
                                 String itemType, String itemName,
                                 String itemDesc, double startPrice,
                                 int durationMinutes) {
        // Tìm seller
        User u = userService.findByUsername(sellerUsername);
        Seller seller = (u instanceof Seller) ? (Seller) u : null;

        // Tạo item qua Factory
        Item item = ItemFactory.createItem(itemType, "item-" + auctionId, itemName, itemDesc, startPrice);

        // Tạo phiên
        Auction auction = new Auction(auctionId, item, seller, durationMinutes);

        // Lưu vào AuctionManager (Singleton)
        AuctionManager.getInstance().addAuction(auction);

        return auction;
    }

    // Lấy tất cả phiên (giữ nguyên từ gốc)
    public List<Auction> getAllAuctions() {
        return AuctionManager.getInstance().getAuctions();
    }

    public Auction getAuction(String id) {
        return AuctionManager.getInstance().getAuctionById(id);
    }

    // Đặt giá thủ công
    public boolean placeBid(String auctionId, String bidderUsername, double amount)
            throws InvalidBidException, AuctionClosedException {

        Auction auction = getAuction(auctionId);
        if (auction == null) throw new RuntimeException("Không tìm thấy phiên: " + auctionId);

        User u = userService.findByUsername(bidderUsername);
        if (!(u instanceof Bidder)) throw new RuntimeException("Người dùng không phải Bidder");

        return auction.placeBid(new Bid((Bidder) u, amount));
    }

    // Đăng ký auto-bid
    public void registerAutoBid(String auctionId, String bidderUsername,
                                double maxBid, double step)
            throws AuctionClosedException {

        Auction auction = getAuction(auctionId);
        if (auction == null) throw new RuntimeException("Không tìm thấy phiên");
        if (auction.isClosed()) throw new AuctionClosedException("Phiên đã đóng");

        User u = userService.findByUsername(bidderUsername);
        if (!(u instanceof Bidder)) throw new RuntimeException("Không phải Bidder");

        auction.registerAutoBid(new AutoBidConfig((Bidder) u, maxBid, step));
    }

    // Đóng phiên thủ công (dành cho admin/seller)
    public void closeAuction(String auctionId) throws AuctionClosedException {
        Auction auction = getAuction(auctionId);
        if (auction == null) throw new RuntimeException("Không tìm thấy phiên");
        auction.closeAuction();
    }

    public void cancelAuction(String auctionId) {
        Auction auction = getAuction(auctionId);
        if (auction != null) auction.cancelAuction();
    }
}
