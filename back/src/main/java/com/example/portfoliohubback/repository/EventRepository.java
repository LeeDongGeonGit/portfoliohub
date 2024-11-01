package com.example.portfoliohubback.repository;

import com.example.portfoliohubback.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e " +
            "WHERE e.user.id = :userId " +
            "AND FUNCTION('MONTH', e.startTime) = :month")
    List<EventEntity> findByUserIdAndMonth(String userId, int month);

    @Query("SELECT e FROM EventEntity e " +
            "WHERE e.user.id = :userId " +
            "AND FUNCTION('MONTH', e.startTime) = :month " +
            "AND FUNCTION('DAY', e.startTime) = :day")
    List<EventEntity> findByUserIdAndMonthAndDay(String userId,
                                                 @Param("month") int month,
                                                 @Param("day") int day);
}
