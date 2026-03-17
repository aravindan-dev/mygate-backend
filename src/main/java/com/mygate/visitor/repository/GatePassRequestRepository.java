package com.mygate.visitor.repository;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.entity.GatePassRequest.RequestStatus;
import com.mygate.visitor.entity.GatePassRequest.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GatePassRequestRepository extends JpaRepository<GatePassRequest, Long> {
    
    // Find by registration number
    List<GatePassRequest> findByRegNoOrderByCreatedAtDesc(String regNo);
    
    // Find by status
    List<GatePassRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);
    
    // Find by staff approval status
    List<GatePassRequest> findByStaffApproval(ApprovalStatus staffApproval);
    
    // Find by HOD approval status
    List<GatePassRequest> findByHodApproval(ApprovalStatus hodApproval);
    
    // Find by assigned staff code
    List<GatePassRequest> findByAssignedStaffCodeOrderByCreatedAtDesc(String assignedStaffCode);
    
    // Find by assigned HOD code
    List<GatePassRequest> findByAssignedHodCodeOrderByCreatedAtDesc(String assignedHodCode);
    
    // Find by assigned HR code
    List<GatePassRequest> findByAssignedHrCodeOrderByCreatedAtDesc(String assignedHrCode);
    
    // Find pending requests for staff approval
    List<GatePassRequest> findByAssignedStaffCodeAndStaffApprovalOrderByCreatedAtDesc(
        String assignedStaffCode, ApprovalStatus staffApproval);
    
    // Find pending requests for HOD approval
    List<GatePassRequest> findByAssignedHodCodeAndHodApprovalOrderByCreatedAtDesc(
        String assignedHodCode, ApprovalStatus hodApproval);
    
    // Find pending requests for HR approval
    List<GatePassRequest> findByAssignedHrCodeAndHrApprovalOrderByCreatedAtDesc(
        String assignedHrCode, ApprovalStatus hrApproval);
    
    // Find by department
    List<GatePassRequest> findByDepartmentOrderByCreatedAtDesc(String department);
    
    // Find by pass type
    List<GatePassRequest> findByPassTypeOrderByCreatedAtDesc(String passType);
    
    // Count by status
    long countByStatus(RequestStatus status);
    
    // Count pending staff approvals
    long countByStaffApproval(ApprovalStatus staffApproval);
    
    // Count pending HOD approvals
    long countByHodApproval(ApprovalStatus hodApproval);
    
    // Count by assigned staff code and staff approval
    long countByAssignedStaffCodeAndStaffApproval(String assignedStaffCode, ApprovalStatus staffApproval);
    
    // Count by assigned HOD code and HOD approval
    long countByAssignedHodCodeAndHodApproval(String assignedHodCode, ApprovalStatus hodApproval);
    
    // Count by assigned HR code and HR approval
    long countByAssignedHrCodeAndHrApproval(String assignedHrCode, ApprovalStatus hrApproval);
    
    // Find by regNo and passType
    List<GatePassRequest> findByRegNoAndPassTypeOrderByCreatedAtDesc(String regNo, String passType);
    
    // Find by regNo and userType (for staff's own requests)
    List<GatePassRequest> findByRegNoAndUserTypeOrderByCreatedAtDesc(String regNo, String userType);
    
    // Find by userType, passType and HR approval status (for HOD bulk passes)
    List<GatePassRequest> findByUserTypeAndPassTypeAndHrApprovalOrderByCreatedAtDesc(
        String userType, String passType, ApprovalStatus hrApproval);
    
    // Find by assigned staff code, user type and status (for visitor requests)
    List<GatePassRequest> findByAssignedStaffCodeAndUserTypeAndStatus(
        String assignedStaffCode, String userType, RequestStatus status);
    
    // Find by assigned staff code and user type (for all visitor requests)
    List<GatePassRequest> findByAssignedStaffCodeAndUserType(
        String assignedStaffCode, String userType);
    
    // Find by requested by staff code and pass type (for staff bulk passes)
    List<GatePassRequest> findByRequestedByStaffCodeAndPassTypeOrderByCreatedAtDesc(
        String requestedByStaffCode, String passType);
    
    // Find by QR owner ID (for bulk passes where user is the receiver)
    List<GatePassRequest> findByQrOwnerIdOrderByCreatedAtDesc(String qrOwnerId);
}
