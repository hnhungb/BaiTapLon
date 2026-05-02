package model.user;

public class Seller extends User {

    public Seller(String id, String name) {
        super(id, name);
    }

    @Override
    public String getRole() {
        return "SELLER";
    }
}
