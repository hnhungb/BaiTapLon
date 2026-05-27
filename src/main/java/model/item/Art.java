package model.item;
import model.user.Seller;
public class Art extends Item {
    public Art(String id, String name, String description, double startPrice, Seller seller) {
        super(id, name, description, startPrice, seller);
    }
    @Override
    public String getType() {
        return "Art";
    }
}