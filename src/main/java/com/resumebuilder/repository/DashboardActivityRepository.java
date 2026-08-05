package com.resumebuilder.repository;

import com.resumebuilder.entity.DashboardActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface DashboardActivityRepository extends JpaRepository<DashboardActivity, UUID> {

    @Query("SELECT a FROM DashboardActivity a WHERE a.user.id = :userId AND a.createdAt >= :startDate ORDER BY a.createdAt DESC")
    Page<DashboardActivity> findByUserIdAndCreatedAtAfter(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(a) FROM DashboardActivity a WHERE a.user.id = :userId AND a.createdAt >= :startDate")
    long countByUserIdAndCreatedAtAfter(@Param("userId") UUID userId, @Param("startDate") LocalDateTime startDate);
}

