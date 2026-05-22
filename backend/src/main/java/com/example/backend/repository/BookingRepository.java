package com.example.backend.repository;

import com.example.backend.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {

    // Clients fetch their personal service booking histories
    List<BookingEntity> findByClient_UserID(Integer clientId);

    // Providers look up incoming work requests submitted to them
    List<BookingEntity> findByGig_Provider_UserID(Integer providerId);

    List<BookingEntity> findAllByOrderByCreatedAtDesc();

    boolean existsByGig_GigID(Integer gigId);

    List<BookingEntity> findByClient_UserIDAndGig_GigIDAndStatus(Integer clientId, Integer gigId, String status);
}
