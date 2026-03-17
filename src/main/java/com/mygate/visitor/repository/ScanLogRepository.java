package com.mygate.visitor.repository;

import com.mygate.visitor.entity.ScanLog;
import com.mygate.visitor.entity.PersonType;
import com.mygate.visitor.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScanLogRepository extends JpaRepository<ScanLog, Long> {
    List<ScanLog> findByPersonType(PersonType personType);
    List<ScanLog> findByStatus(ApprovalStatus status);
    List<ScanLog> findByAccessGranted(Boolean accessGranted);
    List<ScanLog> findByScannedBy(String scannedBy);
    List<ScanLog> findByScanTimeBetween(LocalDateTime start, LocalDateTime end);
    List<ScanLog> findByQrCode(String qrCode);
}