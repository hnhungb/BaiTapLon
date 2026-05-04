package model.auction;
import model.item.*;

import observer.BidObserver;
import exception.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {

    private Item item;
    private double currentPrice;
    private List<Bid> bids;
    private List<BidObserver> observers;

    private boolean isClosed = false; // trạng thái auction

    private ReentrantLock lock = new ReentrantLock();

    public Auction(Item item) {
        this.item = item;
        this.currentPrice = item.getStartPrice();
        this.bids = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addObserver(BidObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(Bid bid) {
        for (BidObserver o : observers) {
            o.update(bid);
        }
    }

    public boolean placeBid(Bid bid) throws InvalidBidException {
        lock.lock();
        try {
            if (bid.getAmount() <= currentPrice) {
                throw new InvalidBidException("Bid must be higher!");
            }

            currentPrice = bid.getAmount();
            bids.add(bid);
            notifyObservers(bid);

            return true;

        } finally {
            lock.unlock();
        }
    }

    public void closeAuction() throws AuctionClosedException {
        lock.lock();
        try {
            isClosed = true;
            System.out.println("Auction CLOSED!");

            Bid winner = getWinner();
            if (winner != null) {
                System.out.println("Winner: " + winner.getBidder().getName()
                        + " with price " + winner.getAmount());
            } else {
                System.out.println("No bids placed.");
            }

        } finally {
            lock.unlock();
        }
    }

    public Bid getWinner() {
        if (bids.isEmpty()) return null;

        // lấy bid cao nhất
        return Collections.max(bids, Comparator.comparingDouble(Bid::getAmount));
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public boolean isClosed() {
        return isClosed;
    }
}