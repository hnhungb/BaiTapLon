package model.user;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    // danh sách user
    private List<User> users = new ArrayList<>();
    // user đang đăng nhập
    private User currentUser;
    // REGISTER
    public boolean register(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        // username không được rỗng
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty");
            return false;
        }
        // password phải từ 6 kí tự
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
    // LOGIN
    public User login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("Login success");
                return user;
            }
        }
        System.out.println("Wrong username or password");
        return null;
    }
    // DISPLAY USERS
    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users found");
            return;
        }
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
    // lấy current user
    public User getCurrentUser() {
        return currentUser;
    }
}