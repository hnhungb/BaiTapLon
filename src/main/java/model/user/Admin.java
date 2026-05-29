package model.user;

// Quản trị viên hệ thống
public class Admin extends User {

    public Admin(String id, String username, String password, String email) {
        super(id, username, password, email);
    }

    public Admin(String id, String username) {
        this(id, username, "admin123", username + "@auction.com");
    }

    @Override
    public String getRole() { return "ADMIN"; }
}
