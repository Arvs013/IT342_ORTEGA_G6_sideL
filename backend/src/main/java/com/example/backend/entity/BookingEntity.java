package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Integer bookingID;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private UserEntity client;

    @ManyToOne
    @JoinColumn(name = "gig_id", nullable = false)
    private UserEntity gig;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    // Statuses: "PENDING", "ACCEPTED", "REJECTED", "COMPLETED", "CANCELLED"
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BookingEntity() {}

    public Integer getBookingID() { return bookingID; }
    public void setBookingID(Integer bookingID) { this.bookingID = bookingID; }

    public UserEntity getClient() { return client; }
    public void setClient(UserEntity client) { this.client = client; }

    public UserEntity getGig() { return gig; }
    public void setGig(UserEntity gig) { this.gig = gig; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}