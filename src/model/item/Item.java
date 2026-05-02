package model.item;
public abstract class Item{
    
    protected String id;
    protected String name;
    protected double startPrice;

    public Item(String id, String name, double startPrice){
        this.id = id;
        this.name = name;
        this.startPrice = startPrice;
    }
    public abstract String getType();
    public String getName(){
        return name;
    }
    public double getStartPrice(){
        return startPrice;
    }
}
