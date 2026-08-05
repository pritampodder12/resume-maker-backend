package com.resumebuilder.repository;

import com.resumebuilder.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    Page<Resume> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Optional<Resume> findByUserIdAndActiveTrueAndDeletedFalse(UUID userId);

    Optional<Resume> findByIdAndDeletedFalse(UUID id);

    Optional<Resume> findByIdAndUserIdAndDeletedFalse(UUID id, UUID userId);

    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.sections WHERE r.id = :id AND r.deleted = false")
    Optional<Resume> findByIdWithSections(@Param("id") UUID id);

    @Query("SELECT COUNT(r) FROM Resume r WHERE r.user.id = :userId AND r.deleted = false")
    long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId AND r.active = true AND r.deleted = false")
    Optional<Resume> findActiveResumeByUserId(@Param("userId") UUID userId);
}