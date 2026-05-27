package Factory;

import model.user.Seller;

public class ItemFactory {

    public static Item createItem(
            ItemType type,
            String id,
            String name,
            String description,
            double startPrice,
            Seller seller
    ) {

        if (type == ItemType.ELECTRONICS) {

            return new Electronics(
                    id,
                    name,
                    description,
                    startPrice,
                    seller
            );

        } else if (type == ItemType.ART) {

            return new Art(
                    id,
                    name,
                    description,
                    startPrice,
                    seller
            );

        } else if (type == ItemType.VEHICLE) {

            return new Vehicle(
                    id,
                    name,
                    description,
                    startPrice,
                    seller
            );

        } else {

            throw new IllegalArgumentException(
                    "Invalid item type"
            );
        }
    }
}