package observer;
import model.auction.Bid;
// Một client đang theo dõi phiên đấu giá
// Mỗi khi có bid mới, update() sẽ được gọi tự động
public class BidderClient implements BidObserver{
    private String name;
    public BidderClient(String name) {
        this.name = name;
    }

    @Override
    public void update(Bid bid) {
        System.out.println("[" + name + "] Bid mới: "
                + bid.getBidder().getUsername() + " đặt " + bid.getAmount());
    }
}
