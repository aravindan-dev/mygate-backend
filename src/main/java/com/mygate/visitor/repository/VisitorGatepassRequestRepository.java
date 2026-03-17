package com.mygate.visitor.repository;

import com.mygate.visitor.entity.VisitorGatepassRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitorGatepassRequestRepository extends JpaRepository<VisitorGatepassRequest, Long> {
    
    // Find all requests assigned to a specific staff member
    List<VisitorGatepassRequest> findByAssignedStaffId(String assignedStaffId);
    
    // Find pending requests for a specific staff member
    List<VisitorGatepassRequest> findByAssignedStaffIdAndRequestStatus(String assignedStaffId, String requestStatus);
    
    // Find pending requests for a specific staff member with staff approval pending
    @Query("SELECT v FROM VisitorGatepassRequest v WHERE v.assignedStaffId = :staffId AND v.requestStatus = 'PENDING' AND v.staffApproval = 'PENDING' ORDER BY v.createdAt DESC")
    List<VisitorGatepassRequest> findPendingRequestsForStaff(@Param("staffId") String staffId);
    
    // Find all requests by status
    List<VisitorGatepassRequest> findByRequestStatus(String requestStatus);
    
    // Find requests by department
    List<VisitorGatepassRequest> findByDepartment(String department);
    
    // Find requests by visit date
    List<VisitorGatepassRequest> findByVisitDate(LocalDate visitDate);
    
    // Find requests by staff approval status
    List<VisitorGatepassRequest> findByStaffApproval(String staffApproval);
    
    // Find approved requests with QR code generated
    List<VisitorGatepassRequest> findByStaffApprovalAndQrGenerated(String staffApproval, Boolean qrGenerated);
    
    // Find requests by QR code data
    VisitorGatepassRequest findByQrCodeData(String qrCodeData);
}
