package model.item;

// Phương tiện giao thông
public class Vehicle extends Item {

    private String brand; //thương hiệu
    private int    year;  //năm sản xuất
    private int    km; // số km đã chạy

    public Vehicle(String id, String name, String description,
                   double startPrice, String brand, int year, int km) {
        super(id, name, description, startPrice);
        this.brand = brand;
        this.year  = year;
        this.km    = km;
    }

    public Vehicle(String id, String name, String description, double startPrice) {
        this(id, name, description, startPrice, "Không rõ", 2024, 0);
    }

    @Override
    public String getType() { return "Vehicle"; }

    public String getBrand() { return brand; }
    public int    getYear()  { return year; }
    public int    getKm()    { return km; }

    @Override
    public String toString() {
        return super.toString() + " | Hãng: " + brand + " | Năm: " + year + " | Km: " + km;
    }
}
