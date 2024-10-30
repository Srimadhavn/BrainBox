package com.chatportal.model;

public class User {
    private int id;
    private String username;
    private String role;
    private String password;

    public User() {
    }
    public User(int id, String username, String role, String password) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.password = password;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getPassword() { return password; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(String role) { this.role = role; }
    public void setPassword(String password) { this.password = password; }
}
