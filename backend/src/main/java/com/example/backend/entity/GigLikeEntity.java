package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "gig_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"gig_id", "user_id"})
)
public class GigLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeID;

    @ManyToOne
    @JoinColumn(name = "gig_id", nullable = false)
    private GigEntity gig;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public GigLikeEntity() {}

    public GigLikeEntity(GigEntity gig, UserEntity user) {
        this.gig = gig;
        this.user = user;
    }

    public Integer getLikeID() { return likeID; }
    public void setLikeID(Integer likeID) { this.likeID = likeID; }

    public GigEntity getGig() { return gig; }
    public void setGig(GigEntity gig) { this.gig = gig; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
