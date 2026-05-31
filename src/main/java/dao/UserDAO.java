package dao;

import model.user.User;

import java.util.List;

public interface UserDAO {

    void save(User user);

    User findByUsername(String username);

    List<User> findAll();
}
