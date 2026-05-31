package dao;

import model.auction.Auction;

import java.util.List;

public interface AuctionDAO {

    void save(Auction auction);

    void update(Auction auction);

    Auction findById(String id);

    List<Auction> findAll();

    void delete(String id);
}