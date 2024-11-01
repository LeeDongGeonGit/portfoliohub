package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.TeamBulletinBoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamBullentinBoardRepository extends JpaRepository<TeamBulletinBoardEntity, Long>{
    Page<TeamBulletinBoardEntity> findAll(Pageable pageable);
    Page<TeamBulletinBoardEntity> findByUserId(String userId, Pageable pageable);

    Page<TeamBulletinBoardEntity> findByProjectDevelopmentFieldContaining(String term, Pageable pageable);

}
