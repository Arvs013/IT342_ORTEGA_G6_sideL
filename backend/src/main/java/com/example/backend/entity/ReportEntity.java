package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportID;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @ManyToOne
    @JoinColumn(name = "reported_user_id", nullable = false)
    private UserEntity reportedUser;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @Column(nullable = false, length = 80)
    private String reason;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String details;

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ReportEntity() {}

    public Integer getReportID() { return reportID; }
    public void setReportID(Integer reportID) { this.reportID = reportID; }

    public UserEntity getReporter() { return reporter; }
    public void setReporter(UserEntity reporter) { this.reporter = reporter; }

    public UserEntity getReportedUser() { return reportedUser; }
    public void setReportedUser(UserEntity reportedUser) { this.reportedUser = reportedUser; }

    public BookingEntity getBooking() { return booking; }
    public void setBooking(BookingEntity booking) { this.booking = booking; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
