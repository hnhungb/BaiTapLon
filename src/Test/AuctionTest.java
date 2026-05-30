import Factory.ItemFactory;
import model.auction.*;
import model.item.*;
import model.user.*;
import exception.*;
import service.*;
import observer.*;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {

    // ── 2 test từ code gốc (giữ nguyên) ────────────────────────────

    @Test
    void testValidBid() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "test", 100);
        Auction auction = new Auction(item);

        Bidder b = new Bidder("B1", "Alice");
        assertTrue(auction.placeBid(new Bid(b, 150)));
    }

    @Test
    void testInvalidBid() {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "test", 100);
        Auction auction = new Auction(item);

        Bidder b = new Bidder("B1", "Alice");
        assertThrows(InvalidBidException.class, () -> {
            auction.placeBid(new Bid(b, 50)); // thấp hơn giá khởi điểm -> lỗi
        });
    }

    // ── Các test bổ sung ─────────────────────────────────────────────

    // Test ItemFactory tạo đúng loại
    @Test
    void testFactory() {
        Item e = ItemFactory.createItem("ELECTRONICS", "E1", "Laptop", "", 999);
        Item a = ItemFactory.createItem("ART",         "A1", "Tranh",  "", 500);
        Item v = ItemFactory.createItem("VEHICLE",     "V1", "Xe máy", "", 8000);

        assertInstanceOf(Electronics.class, e);
        assertInstanceOf(Art.class, a);
        assertInstanceOf(Vehicle.class, v);
    }

    // Test đặt giá khi phiên đã đóng
    @Test
    void testBidOnClosedAuction() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        Auction auction = new Auction(item);

        auction.closeAuction();

        Bidder b = new Bidder("B1", "Alice");
        assertThrows(AuctionClosedException.class, () -> {
            auction.placeBid(new Bid(b, 200));
        });
    }

    // Test xác định người thắng
    @Test
    void testNguoiThang() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        Auction auction = new Auction(item);

        Bidder alice = new Bidder("B1", "Alice");
        Bidder bob   = new Bidder("B2", "Bob");

        auction.placeBid(new Bid(alice, 120));
        auction.placeBid(new Bid(bob,   180)); // Bob thắng
        auction.closeAuction();

        assertEquals("Bob", auction.getWinner().getBidder().getUsername());
    }

    // Test printInfo polymorphism
    @Test
    void testPrintInfo() {
        Bidder b  = new Bidder("1", "alice");
        Seller s  = new Seller("2", "bob");
        Admin  a  = new Admin("3",  "admin");

        assertTrue(b.toString().contains("BIDDER"));
        assertTrue(s.toString().contains("SELLER"));
        assertTrue(a.toString().contains("ADMIN"));
    }

    // Test Observer được gọi khi có bid mới
    @Test
    void testObserver() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        Auction auction = new Auction(item);

        List<Double> received = new ArrayList<>();
        auction.addObserver(bid -> received.add(bid.getAmount()));

        Bidder b = new Bidder("B1", "Alice");
        auction.placeBid(new Bid(b, 150));
        auction.placeBid(new Bid(b, 200));

        assertEquals(2, received.size());
        assertEquals(200.0, received.get(1));
    }

    // Test auto-bid kích hoạt khi có bid từ đối thủ
    @Test
    void testAutoBid() throws Exception {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        Auction auction = new Auction(item);

        Bidder alice = new Bidder("B1", "Alice");
        Bidder bob   = new Bidder("B2", "Bob");

        // Bob đăng ký auto-bid: tối đa 300, bước 20
        auction.registerAutoBid(new AutoBidConfig(bob, 300, 20));

        // Alice đặt 150 -> Bob tự động đặt 170
        auction.placeBid(new Bid(alice, 150));

        assertEquals(170.0, auction.getCurrentPrice(), 0.01);
        assertEquals("Bob", auction.getWinningBid().getBidder().getUsername());
    }

    // Test AuctionManager là Singleton
    @Test
    void testSingleton() {
        AuctionManager m1 = AuctionManager.getInstance();
        AuctionManager m2 = AuctionManager.getInstance();
        assertSame(m1, m2);
    }

    // Test UserService đăng ký và đăng nhập
    @Test
    void testUserService() {
        UserService us = new UserService();
        us.register(new Bidder("B1", "testuser", "123456", "test@gmail.com"));

        // Đăng nhập đúng
        assertNotNull(us.login("testuser", "123456"));

        // Đăng nhập sai password
        assertNull(us.login("testuser", "saipass"));
    }

    // Test 2 người đặt giá đồng thời - không bị race condition
    @Test
    void testConcurrentBid() throws InterruptedException {
        Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        Auction auction = new Auction(item);

        Bidder[] bidders = new Bidder[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            bidders[i] = new Bidder("B" + i, "User" + i);
            final int idx = i;
            threads[i] = new Thread(() -> {
                try {
                    auction.placeBid(new Bid(bidders[idx], 100 + (idx + 1) * 10));
                } catch (Exception ignored) {}
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        // Giá hiện tại phải >= giá khởi điểm và nhất quán với winningBid
        assertTrue(auction.getCurrentPrice() >= 100);
        if (auction.getWinningBid() != null) {
            assertEquals(auction.getCurrentPrice(), auction.getWinningBid().getAmount(), 0.001);
        }
    }
}
