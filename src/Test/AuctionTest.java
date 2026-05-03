import Factory.ItemFactory;
import model.item.*;
import model.auction.*;
import model.user.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class AuctionTest {

    @Test
    void testValidBid() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", 100);
        Auction auction = new Auction(item);

        Bidder b = new Bidder("B1", "A");

        assertTrue(auction.placeBid(new Bid(b, 150)));
    }

    @Test
    void testInvalidBid() {
        Item item;
        item = ItemFactory.createItem("ELECTRONICS","I1", "Phone", 100);
        Auction auction = new Auction(item);

        Bidder b = new Bidder("B1", "A");

        assertThrows(Exception.class, () -> {
            auction.placeBid(new Bid(b, 50));
        });
    }
}

