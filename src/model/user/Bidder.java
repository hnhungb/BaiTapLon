package model.user;

public class Bidder extends User {

    public Bidder(String id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() {
        return "BIDDER";
    }
}
