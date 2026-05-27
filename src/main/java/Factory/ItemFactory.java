package factory;

import model.item.*;

public class ItemFactory {

    // Tạo item từ string type (dùng khi nhận từ bên ngoài (client) qua network)
    public static Item createItem(String type, String id, String name,
                                  String description, double startPrice) {
        return switch (type.toUpperCase()) {
            case "ELECTRONICS" -> new Electronics(id, name, description, startPrice);
            case "ART"         -> new Art(id, name, description, startPrice);
            case "VEHICLE"     -> new Vehicle(id, name, description, startPrice);
            default -> throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + type);
        };
    }

    // Tạo item từ enum (dùng khi gọi trong code Java an toàn hơn String, tránh lỗi gõ sai chữ)
    public static Item createItem(ItemType type, String id, String name,
                                  String description, double startPrice) {
        return createItem(type.name(), id, name, description, startPrice);
    }
}
