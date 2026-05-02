package model.item;
public class Art extends Item {
    public Art(String id, String name, double price) {
        super(id, name, price);
    }
    @Override
    public String getType() {
        return "Art";
    }
}
