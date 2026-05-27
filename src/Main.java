import Factory.ItemFactory;
import exception.AuctionClosedException;
import exception.InvalidBidException;
import model.auction.Auction;
import model.auction.Bid;
import model.item.Item;
import model.item.ItemType;
import model.user.Bidder;
import model.user.Seller;
import observer.BidderClient;

public class Main {
    public static void main(String[] args) {
        Bidder bidder1 =
                new Bidder(
                        "1",
                        "Alice",
                        "123456"
                );

        Bidder bidder2 =
                new Bidder(
                        "2",
                        "Bob",
                        "456789"
                );
        Seller seller =
                new Seller(
                        "S1",
                        "David",
                        "123456"
                );

        Item item =
                ItemFactory.createItem(
                        ItemType.ELECTRONICS,
                        "I1",
                        "Laptop",
                        "Gaming Laptop",
                        100,
                        seller
                );

        Auction auction = new Auction(item);

        // observer
        auction.addObserver(
                new BidderClient("Client1")
        );

        auction.addObserver(
                new BidderClient("Client2")
        );

        // multi-thread bidding
        Thread t1 = new Thread(() -> {

            try {

                auction.placeBid(
                        new Bid(
                                bidder1,
                                120
                        )
                );

            } catch (InvalidBidException e) {

                System.err.println(
                        "T1 lỗi: " +
                                e.getMessage()
                );
            } catch (AuctionClosedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {

            try {

                auction.placeBid(
                        new Bid(
                                bidder2,
                                150
                        )
                );

            } catch (InvalidBidException e) {

                System.err.println(
                        "T2 lỗi: " +
                                e.getMessage()
                );
            } catch (AuctionClosedException e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();
    }
}