package dao;

import model.item.Item;

import java.util.List;

public interface ItemDAO {

    void save(Item item);

    void update(Item item);

    void delete(String itemId);

    Item findById(String itemId);

    List<Item> findAll();
}