package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "gigs")
public class GigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gig_id")
    private Integer gigID;

    // Connects the gig to the specific provider account
    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private UserEntity provider;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    // Categories: "PLUMBING", "ELECTRONICS", "APPLIANCES", "GADGETS", "MOTORCYCLE", "CAR"
    @Column(nullable = false, length = 50)
    private String category;

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    private Long likeCount = 0L;

    @Transient
    private Boolean likedByCurrentUser = false;

    public GigEntity() {}

    public Integer getGigID() { return gigID; }
    public void setGigID(Integer gigID) { this.gigID = gigID; }

    public UserEntity getProvider() { return provider; }
    public void setProvider(UserEntity provider) { this.provider = provider; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getLikeCount() { return likeCount; }
    public void setLikeCount(Long likeCount) { this.likeCount = likeCount; }

    public Boolean getLikedByCurrentUser() { return likedByCurrentUser; }
    public void setLikedByCurrentUser(Boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
