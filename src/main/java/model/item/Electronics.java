package model.item;

// Sản phẩm điện tử
public class Electronics extends Item {

    private String brand; // thương hiệu
    private String model; // dòng sản phẩm

    public Electronics(String id, String name, String description,
                       double startPrice, String brand, String model) {
        super(id, name, description, startPrice);
        this.brand = brand;
        this.model = model;
    }

    public Electronics(String id, String name, String description, double startPrice) {
        this(id, name, description, startPrice, "Không rõ", "Không rõ");
    }

    @Override
    public String getType() { return "Electronics"; }

    public String getBrand() { return brand; }
    public String getModel() { return model; }

    @Override
    public String toString() {
        return super.toString() + " | Hãng: " + brand + " | Model: " + model;
    }
}
