package model.item;
import model.user.Seller;
public class Vehicle extends Item {
    public Vehicle(String id, String name, String description, double startPrice, Seller seller) {
        super(id, name, description, startPrice, seller);
    }
    @Override
    public String getType() {
        return "Vehicle";
    }
}