package test.java;

import Factory.ItemFactory;
import model.auction.*;
import model.item.*;
import model.user.*;
import exception.*;
import org.junit.Test;
import org.testng.annotations.Test;
import service.*;
import observer.*;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {

    @org.testng.annotations.Test
    public void testValidBid() throws Exception {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "test", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        model.user.Bidder b = new model.user.Bidder("B1", "Alice");
        assertTrue(auction.placeBid(new model.auction.Bid(b, 150)));
    }

    @Test
    public void testInvalidBid() {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "test", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        model.user.Bidder b = new model.user.Bidder("B1", "Alice");
        assertThrows(exception.InvalidBidException.class, () -> {
            auction.placeBid(new model.auction.Bid(b, 50)); // thấp hơn giá khởi điểm -> lỗi
        });
    }

    // ── Các test bổ sung ─────────────────────────────────────────────

    // Test ItemFactory tạo đúng loại
    @Test
    public void testFactory() {
        model.item.Item e = ItemFactory.createItem("ELECTRONICS", "E1", "Laptop", "", 999);
        model.item.Item a = ItemFactory.createItem("ART",         "A1", "Tranh",  "", 500);
        model.item.Item v = ItemFactory.createItem("VEHICLE",     "V1", "Xe máy", "", 8000);

        assertInstanceOf(model.item.Electronics.class, e);
        assertInstanceOf(model.item.Art.class, a);
        assertInstanceOf(model.item.Vehicle.class, v);
    }

    // Test đặt giá khi phiên đã đóng
    @Test
    public void testBidOnClosedAuction() throws Exception {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        auction.closeAuction();

        model.user.Bidder b = new model.user.Bidder("B1", "Alice");
        assertThrows(exception.AuctionClosedException.class, () -> {
            auction.placeBid(new model.auction.Bid(b, 200));
        });
    }

    // Test xác định người thắng
    @Test
    public void testNguoiThang() throws Exception {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        model.user.Bidder alice = new model.user.Bidder("B1", "Alice");
        model.user.Bidder bob   = new model.user.Bidder("B2", "Bob");

        auction.placeBid(new model.auction.Bid(alice, 120));
        auction.placeBid(new model.auction.Bid(bob,   180)); // Bob thắng
        auction.closeAuction();

        assertEquals("Bob", auction.getWinner().getBidder().getUsername());
    }

    // Test printInfo polymorphism
    @Test
    public void testPrintInfo() {
        model.user.Bidder b  = new model.user.Bidder("1", "alice");
        model.user.Seller s  = new model.user.Seller("2", "bob");
        model.user.Admin a  = new model.user.Admin("3",  "admin");

        assertTrue(b.toString().contains("BIDDER"));
        assertTrue(s.toString().contains("SELLER"));
        assertTrue(a.toString().contains("ADMIN"));
    }

    // Test Observer được gọi khi có bid mới
    @Test
    public void testObserver() throws Exception {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        List<Double> received = new ArrayList<>();
        auction.addObserver(bid -> received.add(bid.getAmount()));

        model.user.Bidder b = new model.user.Bidder("B1", "Alice");
        auction.placeBid(new model.auction.Bid(b, 150));
        auction.placeBid(new model.auction.Bid(b, 200));

        assertEquals(2, received.size());
        assertEquals(200.0, received.get(1));
    }

    // Test auto-bid kích hoạt khi có bid từ đối thủ
    @Test
    public void testAutoBid() throws Exception {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        model.user.Bidder alice = new model.user.Bidder("B1", "Alice");
        model.user.Bidder bob   = new model.user.Bidder("B2", "Bob");

        // Bob đăng ký auto-bid: tối đa 300, bước 20
        auction.registerAutoBid(new model.auction.AutoBidConfig(bob, 300, 20));

        // Alice đặt 150 -> Bob tự động đặt 170
        auction.placeBid(new model.auction.Bid(alice, 150));

        assertEquals(170.0, auction.getCurrentPrice(), 0.01);
        assertEquals("Bob", auction.getWinningBid().getBidder().getUsername());
    }

    // Test AuctionManager là Singleton
    @Test
    public void testSingleton() {
        service.AuctionManager m1 = service.AuctionManager.getInstance();
        service.AuctionManager m2 = service.AuctionManager.getInstance();
        assertSame(m1, m2);
    }

    // Test UserService đăng ký và đăng nhập
    @Test
    public void testUserService() {
        service.UserService us = new service.UserService();
        us.register(new model.user.Bidder("B1", "testuser", "123456", "test@gmail.com"));

        // Đăng nhập đúng
        assertNotNull(us.login("testuser", "123456"));

        // Đăng nhập sai password
        assertNull(us.login("testuser", "saipass"));
    }

    // Test 2 người đặt giá đồng thời - không bị race condition
    @Test
    public void testConcurrentBid() throws InterruptedException {
        model.item.Item item = ItemFactory.createItem("ELECTRONICS", "I1", "Phone", "", 100);
        model.auction.Auction auction = new model.auction.Auction(item);

        model.user.Bidder[] bidders = new model.user.Bidder[10];
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            bidders[i] = new model.user.Bidder("B" + i, "User" + i);
            final int idx = i;
            threads[i] = new Thread(() -> {
                try {
                    auction.placeBid(new model.auction.Bid(bidders[idx], 100 + (idx + 1) * 10));
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
