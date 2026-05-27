import Factory.ItemFactory;
import exception.InvalidBidException;
import model.auction.Auction;
import model.auction.Bid;
import model.item.Item;
import model.item.ItemType;
import model.user.Bidder;
import model.user.Seller;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuctionTest {

    @Test
    void testValidBid() throws Exception {

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
                        "Phone",
                        "iPhone 17",
                        100,
                        seller
                );

        Auction auction =
                new Auction(item);

        Bidder bidder =
                new Bidder(
                        "B1",
                        "Alice",
                        "123456"
                );

        assertTrue(
                auction.placeBid(
                        new Bid(
                                bidder,
                                150
                        )
                )
        );
    }

    @Test
    void testInvalidBid() {

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
                        "Phone",
                        "iPhone 17",
                        100,
                        seller
                );

        Auction auction =
                new Auction(item);

        Bidder bidder =
                new Bidder(
                        "B1",
                        "Alice",
                        "123456"
                );

        assertThrows(
                InvalidBidException.class,
                () -> {
                    auction.placeBid(
                            new Bid(
                                    bidder,
                                    50
                            )
                    );
                }
        );
    }
}