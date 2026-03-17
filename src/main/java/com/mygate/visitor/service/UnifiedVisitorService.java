package com.mygate.visitor.service;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.entity.Staff;
import com.mygate.visitor.repository.GatePassRequestRepository;
import com.mygate.visitor.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Unified Visitor Service using Gatepass table
 * Replaces old Visitor table with unified GatePassRequest entity
 */
@Service
public class UnifiedVisitorService {
    
    private static final Logger log = LoggerFactory.getLogger(UnifiedVisitorService.class);
    
    @Autowired
    private GatePassRequestRepository gatePassRequestRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private GatePassRequestService gatePassRequestService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Create visitor request from website
     * Maps visitor data to GatePassRequest with user_type="VISITOR"
     */
    @Transactional
    public GatePassRequest createVisitorRequest(
            String visitorName,
            String visitorEmail,
            String visitorPhone,
            String department,
            String staffCode,
            String purpose,
            String reason,
            Integer numberOfPeople,
            String vehicleNumber) throws Exception {
        
        log.info("Creating visitor request: {} for staff: {}", visitorName, staffCode);
        
        // Validate staff exists
        Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
        if (!staffOpt.isPresent()) {
            throw new Exception("Staff member not found: " + staffCode);
        }
        
        Staff staff = staffOpt.get();
        
        // Create GatePassRequest with VISITOR type
        GatePassRequest request = new GatePassRequest();
        request.setUserType("VISITOR");
        request.setRegNo(visitorEmail); // Use email as unique identifier
        request.setStudentName(visitorName); // Visitor name
        request.setDepartment(department);
        request.setAssignedStaffCode(staffCode);
        request.setPurpose(purpose);
        
        // Build comprehensive reason field
        StringBuilder fullReason = new StringBuilder();
        if (reason != null && !reason.isEmpty()) {
            fullReason.append(reason);
        }
        if (visitorPhone != null) {
            fullReason.append("\nPhone: ").append(visitorPhone);
        }
        if (vehicleNumber != null && !vehicleNumber.isEmpty()) {
            fullReason.append("\nVehicle: ").append(vehicleNumber);
        }
        request.setReason(fullReason.toString());
        
        // Set counts
        request.setStudentCount(numberOfPeople != null ? numberOfPeople : 1);
        request.setPassType("SINGLE");
        
        // Set approval workflow
        request.setStatus(GatePassRequest.RequestStatus.PENDING);
        request.setStaffApproval(GatePassRequest.ApprovalStatus.PENDING);
        request.setHodApproval(GatePassRequest.ApprovalStatus.PENDING);
        request.setHrApproval(GatePassRequest.ApprovalStatus.PENDING);
        
        // Save request
        GatePassRequest savedRequest = gatePassRequestRepository.save(request);
        gatePassRequestRepository.flush();
        
        log.info("✅ Visitor request created: ID={}, Visitor={}, Staff={}", 
            savedRequest.getId(), visitorName, staffCode);
        
        // Send approval request email to staff
        try {
            emailService.sendApprovalRequestEmail(
                staff.getEmail(),
                staff.getStaffName(),
                visitorName,
                visitorEmail,
                visitorPhone,
                purpose,
                numberOfPeople,
                department,
                savedRequest.getId()
            );
            log.info("📧 Approval request email sent to: {}", staff.getEmail());
        } catch (Exception e) {
            log.error("⚠️ Failed to send approval email: {}", e.getMessage());
        }
        
        return savedRequest;
    }
    
    /**
     * Create visitor request from security dashboard
     * Same as website but tracks who registered
     */
    @Transactional
    public GatePassRequest createVisitorRequestBySecurity(
            String securityId,
            String visitorName,
            String visitorEmail,
            String visitorPhone,
            String department,
            String staffCode,
            String purpose,
            Integer numberOfPeople,
            String vehicleNumber) throws Exception {
        
        log.info("🔐 Security {} registering visitor: {}", securityId, visitorName);
        
        // Create request same as website
        GatePassRequest request = createVisitorRequest(
            visitorName, visitorEmail, visitorPhone, department,
            staffCode, purpose, 
            "Registered by Security: " + securityId,
            numberOfPeople, vehicleNumber
        );
        
        return request;
    }
    
    /**
     * Get pending visitor requests for a staff member
     */
    @Transactional(readOnly = true)
    public List<GatePassRequest> getPendingVisitorRequestsForStaff(String staffCode) {
        return gatePassRequestRepository.findByAssignedStaffCodeAndUserTypeAndStatus(
            staffCode, "VISITOR", GatePassRequest.RequestStatus.PENDING
        );
    }
    
    /**
     * Get all visitor requests for a staff member
     */
    @Transactional(readOnly = true)
    public List<GatePassRequest> getAllVisitorRequestsForStaff(String staffCode) {
        return gatePassRequestRepository.findByAssignedStaffCodeAndUserType(
            staffCode, "VISITOR"
        );
    }
    
    /**
     * Approve visitor request and generate QR with manual code
     */
    @Transactional
    public GatePassRequest approveVisitorRequest(Long requestId, String staffCode) throws Exception {
        log.info("Approving visitor request: {} by staff: {}", requestId, staffCode);
        
        Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new Exception("Request not found: " + requestId);
        }
        
        GatePassRequest request = requestOpt.get();
        
        // Verify it's a visitor request
        if (!"VISITOR".equals(request.getUserType())) {
            throw new Exception("Not a visitor request");
        }
        
        // Verify staff is assigned
        if (!staffCode.equals(request.getAssignedStaffCode())) {
            throw new Exception("Staff not authorized to approve this request");
        }
        
        // Update approval status
        request.setStaffApproval(GatePassRequest.ApprovalStatus.APPROVED);
        request.setStaffApprovedBy(staffCode);
        request.setStaffApprovalDate(LocalDateTime.now());
        request.setStatus(GatePassRequest.RequestStatus.APPROVED);
        
        // Save first
        GatePassRequest savedRequest = gatePassRequestRepository.save(request);
        gatePassRequestRepository.flush();
        
        // Generate QR code with manual code
        gatePassRequestService.generateSinglePassQRCode(savedRequest);
        
        // Reload to get QR and manual code
        savedRequest = gatePassRequestRepository.findById(requestId).get();
        
        log.info("✅ Visitor request approved: ID={}, QR={}, Manual={}", 
            requestId, savedRequest.getQrCode(), savedRequest.getManualCode());
        
        // Send QR code email to visitor
        try {
            Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                emailService.sendVisitorPassEmail(
                    savedRequest.getRegNo(), // visitor email
                    savedRequest.getStudentName(), // visitor name
                    savedRequest.getQrCode(),
                    savedRequest.getManualCode(),
                    staff.getStaffName(),
                    savedRequest.getDepartment(),
                    savedRequest.getRequestDate() != null ? 
                        savedRequest.getRequestDate().toString() : LocalDateTime.now().toString(),
                    ""
                );
                log.info("📧 Visitor pass email sent to: {}", savedRequest.getRegNo());
            }
        } catch (Exception e) {
            log.error("⚠️ Failed to send visitor pass email: {}", e.getMessage());
        }
        
        return savedRequest;
    }
    
    /**
     * Reject visitor request
     */
    @Transactional
    public GatePassRequest rejectVisitorRequest(Long requestId, String staffCode, String reason) throws Exception {
        log.info("Rejecting visitor request: {} by staff: {}", requestId, staffCode);
        
        Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new Exception("Request not found: " + requestId);
        }
        
        GatePassRequest request = requestOpt.get();
        
        // Verify it's a visitor request
        if (!"VISITOR".equals(request.getUserType())) {
            throw new Exception("Not a visitor request");
        }
        
        // Verify staff is assigned
        if (!staffCode.equals(request.getAssignedStaffCode())) {
            throw new Exception("Staff not authorized to reject this request");
        }
        
        // Update rejection status
        request.setStaffApproval(GatePassRequest.ApprovalStatus.REJECTED);
        request.setStatus(GatePassRequest.RequestStatus.REJECTED);
        request.setRejectedBy(staffCode);
        request.setRejectionReason(reason);
        request.setRejectedAt(LocalDateTime.now());
        
        GatePassRequest savedRequest = gatePassRequestRepository.save(request);
        gatePassRequestRepository.flush();
        
        log.info("✗ Visitor request rejected: ID={}, Reason={}", requestId, reason);
        
        return savedRequest;
    }
    
    /**
     * Get visitor request by ID
     */
    @Transactional(readOnly = true)
    public Optional<GatePassRequest> getVisitorRequestById(Long id) {
        return gatePassRequestRepository.findById(id)
            .filter(r -> "VISITOR".equals(r.getUserType()));
    }
}
