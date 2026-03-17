package com.mygate.visitor.repository;

import com.mygate.visitor.entity.EntryExit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntryExitRepository extends JpaRepository<EntryExit, Long> {
    
    // Find by user ID
    List<EntryExit> findByUserIdOrderByTimestampDesc(String userId);
    
    // Find by user type
    List<EntryExit> findByUserTypeOrderByTimestampDesc(String userType);
    
    // Find by action
    List<EntryExit> findByActionOrderByTimestampDesc(String action);
    
    // Find latest entry for user
    @Query("SELECT e FROM EntryExit e WHERE e.userId = :userId ORDER BY e.timestamp DESC LIMIT 1")
    Optional<EntryExit> findLatestEntryForUser(@Param("userId") String userId);
    
    // Find today's entries
    @Query("SELECT e FROM EntryExit e WHERE DATE(e.timestamp) = CURRENT_DATE ORDER BY e.timestamp DESC")
    List<EntryExit> findTodaysEntries();
    
    // Count today's entries by action
    @Query("SELECT COUNT(e) FROM EntryExit e WHERE DATE(e.timestamp) = CURRENT_DATE AND e.action = :action")
    Long countTodaysByAction(@Param("action") String action);
    
    // Find by date range
    List<EntryExit> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    
    // Search entries
    @Query("SELECT e FROM EntryExit e WHERE " +
           "e.userId LIKE %:search% OR " +
           "e.userName LIKE %:search% OR " +
           "e.userEmail LIKE %:search% OR " +
           "e.location LIKE %:search%")
    Page<EntryExit> findBySearchTerm(@Param("search") String search, Pageable pageable);
}
