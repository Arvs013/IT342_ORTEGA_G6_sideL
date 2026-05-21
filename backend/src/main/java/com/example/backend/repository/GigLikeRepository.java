package com.example.backend.repository;

import com.example.backend.entity.GigLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GigLikeRepository extends JpaRepository<GigLikeEntity, Integer> {

    long countByGig_GigID(Integer gigId);

    boolean existsByGig_GigIDAndUser_UserID(Integer gigId, Integer userId);

    Optional<GigLikeEntity> findByGig_GigIDAndUser_UserID(Integer gigId, Integer userId);
}
