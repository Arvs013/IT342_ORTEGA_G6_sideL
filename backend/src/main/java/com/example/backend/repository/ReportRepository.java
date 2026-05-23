package com.example.backend.repository;

import com.example.backend.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Integer> {
    List<ReportEntity> findAllByOrderByCreatedAtDesc();
    List<ReportEntity> findByReporter_UserIDOrReportedUser_UserIDOrderByCreatedAtDesc(Integer reporterId, Integer reportedUserId);
}
