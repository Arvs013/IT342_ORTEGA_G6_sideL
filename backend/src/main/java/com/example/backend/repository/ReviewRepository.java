package com.example.backend.repository;

import com.example.backend.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

    List<ReviewEntity> findByGig_GigID(Integer gigId);

    List<ReviewEntity> findByGig_Provider_UserID(Integer providerId);

    boolean existsByGig_GigID(Integer gigId);
}
