package model.user;

public abstract class User {

    protected String id;
    protected String username;
    protected String password;
    protected String email;

    public User(String id, String username, String password, String email) {
        this.id       = id;
        this.username = username;
        this.password = password;
        this.email    = email;
    }

    public abstract String getRole();

    // Getters
    public String getId()       { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail()    { return email; }

    // Setters
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email)       { this.email = email; }

    @Override
    public String toString() {
        return "ID: " + id + " | Username: " + username
                + " | Role: " + getRole() + " | Email: " + email;
    }
}
