package com.mygate.visitor.repository;

import com.mygate.visitor.entity.RailwayExitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RailwayExitLogRepository extends JpaRepository<RailwayExitLog, Long> {
    
    List<RailwayExitLog> findByUserIdOrderByExitTimeDesc(String userId);
    
    @Query("SELECT e FROM RailwayExitLog e WHERE DATE(e.exitTime) = CURRENT_DATE ORDER BY e.exitTime DESC")
    List<RailwayExitLog> findTodaysExits();
    
    @Query("SELECT COUNT(e) FROM RailwayExitLog e WHERE DATE(e.exitTime) = CURRENT_DATE")
    Long countTodaysExits();
}
