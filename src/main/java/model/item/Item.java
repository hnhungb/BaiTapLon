package model.item;

public abstract class Item {

    protected String id;
    protected String name;
    protected String description;
    protected double startPrice;

    public Item(String id, String name, String description, double startPrice) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.startPrice  = startPrice;
    }

    // Mỗi loại item tự trả về type của mình
    public abstract String getType();

    public String getId()          { return id; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public double getStartPrice()  { return startPrice; }

    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setStartPrice(double startPrice)   { this.startPrice = startPrice; }

    @Override
    public String toString() {
        return "ID: " + id + " | " + name + " [" + getType() + "] | Giá khởi điểm: " + startPrice;
    }
}
