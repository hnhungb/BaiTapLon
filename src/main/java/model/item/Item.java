package model.item;
import model.user.Seller;
public abstract class Item {
    protected String id;
    protected String name;
    protected String description;
    protected double startPrice;
    protected Seller seller;
    public Item(String id, String name, String description, double startPrice, Seller seller){
        this.id =id;
        this.name =name;
        this.description =description;
        this.startPrice =startPrice;
        this.seller =seller;
}
public abstract String getType();
public String getId() {
    return id;
}
public String getName() {
    return name;
}
public String getDescription() {
    return description;
}
public double getStartPrice() {
    return startPrice;
}
public Seller getSeller() {
    return seller;
}
public String toString() {
    return "Item ID: " + id + ", Name: " + name + ", Type: " + getType() + ", Start Price: " + startPrice;
}
}