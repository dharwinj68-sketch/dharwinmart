package com.dharwinmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Entity representing an application user (ADMIN, BUYER, SELLER).
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Role is required")
    @Column(nullable = false)
    private String role; // "ADMIN", "BUYER", "SELLER"

    @Column(nullable = false)
    private boolean enabled = true;

    private String phone;

    @Column(length = 500)
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.enabled = true;
    }

    public User(String username, String password, String email, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role != null ? role.toUpperCase() : "BUYER";
        this.enabled = true;
        this.createdAt = LocalDateTime.now();
    }

    public User(String username, String password, String email, String fullName, String role, String phone, String address) {
        this(username, password, email, fullName, role);
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : "BUYER";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isSeller() {
        return "SELLER".equalsIgnoreCase(this.role);
    }

    public boolean isBuyer() {
        return "BUYER".equalsIgnoreCase(this.role);
    }
}
