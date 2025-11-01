package com.misha.springbootnewswagger.repositories;

import com.misha.springbootnewswagger.entities.SitterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SitterRepository extends JpaRepository<SitterEntity, Long> {

    @Query(value = "SELECT * FROM sitter s " +
            "WHERE ST_Distance_Sphere(s.location, ST_GeomFromText(:centerPoint, 4326)) <= :radius",
            nativeQuery = true)
    List<SitterEntity> findSittersWithinRadius(@Param("centerPoint") String centerPoint, @Param("radius") double radius);

    boolean existsByEmail(String email);

    Optional<SitterEntity> findByEmail(String email);

    @Query("""
        SELECT s
        FROM SitterEntity s
        WHERE 
          (:contactName IS NULL 
               OR TRIM(:contactName) = '' 
               OR LOWER(s.contactName) LIKE LOWER(CONCAT('%', :contactName, '%')))
          AND (:companyName IS NULL 
               OR TRIM(:companyName) = '' 
               OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))
          AND (:charges IS NULL 
               OR s.chargesPerHour <= :charges)
    """)
    Page<SitterEntity> filterSitters(
            @Param("contactName") String contactName,
            @Param("companyName") String companyName,
            @Param("charges") Double charges,
            Pageable pageable
    );
}

