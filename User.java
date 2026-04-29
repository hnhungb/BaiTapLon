package com.auction.shared.models;

import java.io.Serializable;
import java.util.UUID;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected String id;
    protected String username;
    protected String password;
    protected String email;
    protected UserRole role;
    
    public User(String username, String password, String email, UserRole role) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    
    public String getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    
    @Override
    public String toString() {
        return String.format("User{id='%s', username='%s', role=%s}", id, username, role);
    }
}