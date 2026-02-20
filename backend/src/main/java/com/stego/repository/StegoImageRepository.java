package com.stego.repository;

import com.stego.entity.StegoImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StegoImageRepository extends JpaRepository<StegoImage, Long> {

    Page<StegoImage> findAll(Pageable pageable);

    List<StegoImage> findByStatus(String status);

    Page<StegoImage> findByStatus(String status, Pageable pageable);

    @Query("SELECT s FROM StegoImage s WHERE s.originalFileName LIKE %:keyword% OR s.description LIKE %:keyword%")
    Page<StegoImage> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    long countByStatus(String status);

    @Query("SELECT s FROM StegoImage s ORDER BY s.createdAt DESC")
    List<StegoImage> findLatest5();
}
