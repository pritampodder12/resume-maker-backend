package com.resumebuilder.repository;

import com.resumebuilder.entity.AtsAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AtsAnalyseRepository extends JpaRepository<AtsAnalysis, UUID> {

    Optional<AtsAnalysis> findByIdAndResumeId(UUID id, UUID resumeId);

    @Query("SELECT a FROM AtsAnalysis a LEFT JOIN FETCH a.keywords WHERE a.id = :id")
    Optional<AtsAnalysis> findByIdWithKeyword(@Param("id") UUID id);
}
