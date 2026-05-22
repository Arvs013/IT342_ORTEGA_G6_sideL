package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;

    @Column(nullable = false, length = 50)
    private String firstname;

    @Column(nullable = false, length = 50)
    private String lastname;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(length = 255)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // --- Marketplace Fields ---

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "is_provider", nullable = false)
    private Boolean isProvider = false;

    // Statuses: "NONE", "PENDING", "APPROVED", "REJECTED"
    @Column(name = "provider_status", nullable = false, length = 20)
    private String providerStatus = "NONE";

    @Column(name = "account_status", length = 20)
    private String accountStatus = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAT;

    // ✅ Default constructor
    public UserEntity() {}

    // ✅ Constructor with fields
    public UserEntity(String firstname, String lastname, String email, String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.isAdmin = false;
        this.isProvider = false;
        this.providerStatus = "NONE";
    }

    // ✅ Getters and Setters
    public Integer getUserID() { return userID; }
    public void setUserID(Integer userID) { this.userID = userID; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public Boolean getIsProvider() { return isProvider; }
    public void setIsProvider(Boolean isProvider) { this.isProvider = isProvider; }

    public String getProviderStatus() { return providerStatus; }
    public void setProviderStatus(String providerStatus) { this.providerStatus = providerStatus; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public LocalDateTime getCreatedAT() { return createdAT; }
    public void setCreatedAT(LocalDateTime createdAT) { this.createdAT = createdAT; }

    @PrePersist
    protected void onCreate() {
        this.createdAT = LocalDateTime.now();
    }
}
