package service;

import model.user.User;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    // danh sách user
    private List<User> users = new ArrayList<>();
    // register
    public boolean register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        //không để username trống và pass dưới 6 kí tự
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Password must be at least 6 characters");
            return false;
        }
        // check username trùng
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("Username already exists");
                return false;
            }
        }
        // thêm user
        users.add(user);
        System.out.println("Register success");
        return true;
    }
    // login
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                System.out.println("Login success");
                return user;
            }
        }
        System.out.println("Wrong username or password");
        return null;
    }
    // display users
    public void displayUsers() {
        for (User user : users) {
            System.out.println(user);
        }
    }
    // kiểm tra admin
    public boolean isAdmin() {
        return currentUser != null
                && currentUser.getRole().equals("ADMIN");
    }
    // kiểm tra seller
    public boolean isSeller() {
        return currentUser != null
                && currentUser.getRole().equals("SELLER");
    }
    // kiểm tra bidder
    public boolean isBidder() {
        return currentUser != null
                && currentUser.getRole().equals("BIDDER");
    }
}