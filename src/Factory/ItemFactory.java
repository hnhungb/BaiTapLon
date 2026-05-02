package Factory;

import model.item.*;
public class ItemFactory {

    public static Item createItem(String type, String id, String name, double price) {
        switch (type) {
            case "ELECTRONICS":
                return new Electronics(id, name, price);
            case "ART":
                return new Art(id, name, price);
            case "VEHICLE":
                return new Vehicle(id, name, price);
            default:
                throw new IllegalArgumentException("Invalid type");
        }
    }
}