package service;
import model.user.*;
import java.util.ArrayList;
import java.util.List;
// Quản lý người dùng - đăng ký, đăng nhập
public class UserService {
    private List<User> users       = new ArrayList<>();
    private User       currentUser = null; // user đang đăng nhập
    // Đăng ký
    public boolean register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username không được để trống");
            return false;
        }
        if (password == null || password.length() < 6) {
            System.out.println("Password phải ít nhất 6 ký tự");
            return false;
        }

        // Kiểm tra trùng username
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                System.out.println("Username đã tồn tại");
                return false;
            }
        }
        users.add(user);
        System.out.println("Đăng ký thành công: " + username);
        return true;
    }

    // Đăng nhập
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Đăng nhập thành công: " + username + " [" + user.getRole() + "]");
                return user;
            }
        }
        System.out.println("Sai username hoặc password");
        return null;
    }
    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    // Tìm user theo username
    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    // ktra vtro
    public boolean isAdmin()  { return currentUser != null && currentUser.getRole().equals("ADMIN"); }
    public boolean isSeller() { return currentUser != null && currentUser.getRole().equals("SELLER"); }
    public boolean isBidder() { return currentUser != null && currentUser.getRole().equals("BIDDER"); }

    public void displayUsers() {
        for (User u : users) System.out.println(u);
    }

    public List<User> getUsers() { return users; }
}
