package observer;

public class BidderClient implements BidObserver {

    private String name;

    public BidderClient(String name) {
        this.name = name;
    }

    @Override
    public void update(Bid bid) {
        System.out.println(name + " received update: New bid = " + bid.getAmount());
    }
}