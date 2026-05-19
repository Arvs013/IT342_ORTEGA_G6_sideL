package com.example.backend.repository;

import com.example.backend.entity.ProviderApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProviderApplicationRepository extends JpaRepository<ProviderApplicationEntity, Integer> {

    List<ProviderApplicationEntity> findByStatus(String status);

    List<ProviderApplicationEntity> findByUser_UserIDAndStatus(Integer userId, String status);
}
