package model.item;
public class Electronics extends Item{
    public Electronics(String id, String name, double price){
        super(id, name, price);
    }
    @Override
    public String getType() {
        return "Electronics";
    }
}
