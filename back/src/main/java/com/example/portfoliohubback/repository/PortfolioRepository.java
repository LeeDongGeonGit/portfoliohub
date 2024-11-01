package com.example.portfoliohubback.repository;


import com.example.portfoliohubback.entity.PortfolioEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {
    Page<PortfolioEntity> findByCareer(String career, Pageable pageable);

    Page<PortfolioEntity> findByPosition(String position, Pageable pageable);

    Page<PortfolioEntity> findByUser_Id(String userId, Pageable pageable);

    Page<PortfolioEntity> findByCareerAndPosition(String career, String position, Pageable pageable);

    Page<PortfolioEntity> findByCareerAndUser_Id(String career, String userId, Pageable pageable);

    Page<PortfolioEntity> findByPositionAndUser_Id(String position, String userId, Pageable pageable);

    Page<PortfolioEntity> findByCareerAndPositionAndUser_Id(String career, String position, String userId, Pageable pageable);


    @Query("SELECT p FROM PortfolioEntity p ORDER BY p.count DESC")
    List<PortfolioEntity> findTop4ByOrderByCountDesc(Pageable pageable);
}
