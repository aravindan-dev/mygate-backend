package com.mygate.visitor.repository;

import com.mygate.visitor.entity.GatePassScanLog;
import com.mygate.visitor.entity.GatePassScanLog.ScanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GatePassScanLogRepository extends JpaRepository<GatePassScanLog, Long> {
    
    // Find by pass ID
    List<GatePassScanLog> findByPassIdOrderByScannedAtDesc(Long passId);
    
    // Find by scan type
    List<GatePassScanLog> findByScanTypeOrderByScannedAtDesc(ScanType scanType);
    
    // Find by scanned by (security personnel)
    List<GatePassScanLog> findByScannedByOrderByScannedAtDesc(String scannedBy);
    
    // Check if pass already scanned for specific type
    boolean existsByPassIdAndScanType(Long passId, ScanType scanType);
    
    // Find by date range
    List<GatePassScanLog> findByScannedAtBetweenOrderByScannedAtDesc(
        LocalDateTime start, LocalDateTime end);
    
    // Count scans by type
    long countByScanType(ScanType scanType);
    
    // Count scans by security personnel
    long countByScannedBy(String scannedBy);
}
