package com.example.backend.repository;

import com.example.backend.entity.GigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GigRepository extends JpaRepository<GigEntity, Integer> {

    // Clients browse gigs filtering by category (e.g., PLUMBING, GADGETS, CAR)
    List<GigEntity> findByCategory(String category);

    List<GigEntity> findByStatus(String status);

    List<GigEntity> findByCategoryAndStatus(String category, String status);

    // Providers view their own custom uploaded services inside their dashboard
    List<GigEntity> findByProvider_UserID(Integer providerId);
}
