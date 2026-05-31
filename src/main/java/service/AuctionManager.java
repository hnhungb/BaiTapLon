package service;

import model.auction.Auction;
import model.auction.AuctionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Singleton - quản lý tất cả phiên đấu giá
public class AuctionManager {

    private static AuctionManager instance; // Singleton instance

    private List<Auction> auctions = java.util.Collections.synchronizedList(new ArrayList<>());

    // Scheduler chạy ngầm, mỗi giây kiểm tra phiên nào hết giờ
    private ScheduledExecutorService scheduler;

    private AuctionManager() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::autoCloseExpired, 1, 1, TimeUnit.SECONDS);
    }

    // Singleton getInstance
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

    public Auction getAuctionById(String id) {
        for (Auction a : auctions) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    // Lấy các phiên đang chạy
    public List<Auction> getRunningAuctions() {
        List<Auction> result = new ArrayList<>();
        for (Auction a : auctions) {
            if (a.getStatus() == AuctionStatus.RUNNING) result.add(a);
        }
        return result;
    }

    // Tự động đóng phiên hết giờ
    private void autoCloseExpired() {
        for (Auction a : auctions) {
            a.checkAndAutoClose();
        }
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}
