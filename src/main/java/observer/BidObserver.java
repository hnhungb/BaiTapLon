package observer;

import model.auction.Bid;
//muốn nhận thông báo bid mới thì implement interface này
public interface BidObserver {
    void update(Bid bid);
}
