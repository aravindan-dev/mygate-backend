package com.mygate.visitor.repository;

import com.mygate.visitor.entity.QRTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRTableRepository extends JpaRepository<QRTable, Long> {
    
    // Legacy methods
    Optional<QRTable> findByQrCode(String qrCode);
    Optional<QRTable> findByUserTypeAndUserIdAndEntry(String userType, String userId, String entry);
    Optional<QRTable> findByUserTypeAndUserIdAndExit(String userType, String userId, String exit);
    
    // For group pass lifecycle - lookup by token stored in qr_code field
    default Optional<QRTable> findByToken(String token) {
        return findByQrCode(token);
    }
    
    // New methods for bulk pass system
    Optional<QRTable> findByPassRequestId(Long passRequestId);
    Optional<QRTable> findByQrString(String qrString);
    Optional<QRTable> findByManualEntryCode(String manualEntryCode);
    
    // Find by status
    List<QRTable> findByStatus(String status);
    
    // Find by requested by staff code
    List<QRTable> findByRequestedByStaffCodeOrderByCreatedAtDesc(String requestedByStaffCode);
    
    // Find by pass type
    List<QRTable> findByPassTypeOrderByCreatedAtDesc(String passType);
    
    // Find active QR codes
    List<QRTable> findByStatusOrderByCreatedAtDesc(String status);
}
