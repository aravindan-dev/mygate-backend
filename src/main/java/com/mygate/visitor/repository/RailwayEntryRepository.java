package com.mygate.visitor.repository;

import com.mygate.visitor.entity.RailwayEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RailwayEntryRepository extends JpaRepository<RailwayEntry, Long> {
    
    List<RailwayEntry> findByUserIdOrderByTimestampDesc(String userId);
    
    @Query("SELECT e FROM RailwayEntry e WHERE DATE(e.timestamp) = CURRENT_DATE ORDER BY e.timestamp DESC")
    List<RailwayEntry> findTodaysEntries();
    
    @Query("SELECT COUNT(e) FROM RailwayEntry e WHERE DATE(e.timestamp) = CURRENT_DATE")
    Long countTodaysEntries();
}
