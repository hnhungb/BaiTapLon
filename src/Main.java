import Factory.ItemFactory;
import model.item.Item;
import model.user.Bidder;
import model.auction.*;
import service.*;
import observer.*;
import exception.*;

public class Main {

    public static void main(String[] args) {

        Bidder bidder1 = new Bidder("1", "Alice");
        Bidder bidder2 = new Bidder("2", "Bob");

        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "iPhone", 100);

        Auction auction = new Auction(item);

        // observer
        auction.addObserver(new BidderClient("Client1"));
        auction.addObserver(new BidderClient("Client2"));

        // multi-thread bidding
        Thread t1 = new Thread(() -> {
            try {
                auction.placeBid(new Bid(bidder1, 120));
            } catch (InvalidBidException e) {
                System.err.println("T1 Lỗi đặt giá: " + e.getMessage());
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                auction.placeBid(new Bid(bidder2, 150));
            } catch (InvalidBidException e) {
                System.out.println("T2 lỗi: " + e.getMessage());
            }
        });

        t1.start();
        t2.start();
    }
}
