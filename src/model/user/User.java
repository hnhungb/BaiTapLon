package model.user;

public abstract class User {
    protected String id;
    protected String username;
    protected String password;

    public User(String id, String username, String password ) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public abstract String getRole();

    public String getId() {
        return id;
    }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }
    @Override
    public String toString() {
        return "ID: " + id + ", Username: " + username + ", Role: " + getRole();
    }
}
