package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.postgres.Biography;
import com.api.family.apitreeservice.model.postgres.BiographyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiographyHistoryRepository extends JpaRepository<BiographyHistory, Long> {

    List<BiographyHistory> findByBiographyOrderByCreatedAtDesc(Biography biography);

    @Query("SELECT COUNT(bh) FROM BiographyHistory bh WHERE bh.biography = :biography")
    Long countByBiography(@Param("biography") Biography biography);

    @Query("SELECT bh FROM BiographyHistory bh WHERE bh.biography = :biography ORDER BY bh.createdAt DESC")
    List<BiographyHistory> findLatestVersions(@Param("biography") Biography biography);
}
