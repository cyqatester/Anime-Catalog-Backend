package com.example.animecatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "app_users", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;
    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String username;
    @NotBlank
    @Column(nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;
    @Column(nullable = false)
    private boolean superAdmin = false;

    public UserAccount() {
    }

    public UserAccount(String n, String u, String p, UserRole r, boolean s) {
        name = n;
        username = u;
        passwordHash = p;
        role = r;
        superAdmin = s;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        name = v;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String v) {
        username = v;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String v) {
        passwordHash = v;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole v) {
        role = v;
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }
}
