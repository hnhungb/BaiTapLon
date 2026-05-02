package observer;

import model.auction.*;

public interface BidObserver {
    void update(Bid bid);
}