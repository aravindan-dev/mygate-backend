package com.mygate.visitor.service;

import com.mygate.visitor.entity.Visitor;
import com.mygate.visitor.entity.Staff;
import com.mygate.visitor.repository.VisitorRepository;
import com.mygate.visitor.repository.StaffRepository;
import com.mygate.visitor.util.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VisitorGatepassService {
    
    @Autowired
    private VisitorRepository visitorRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    /**
     * Create a new visitor gatepass request with validation
     * Ensures multi-device visibility through immediate database commit
     */
    @Transactional
    public Visitor createRequest(Visitor request) throws Exception {
        // Validate staff exists in main staff table
        Optional<Staff> staffOpt = staffRepository.findByStaffCode(request.getStaffCode());
        if (!staffOpt.isPresent()) {
            throw new Exception("Staff member not found with ID: " + request.getStaffCode());
        }
        
        Staff staff = staffOpt.get();
        
        // Validate department match using DepartmentMapper
        if (staff.getDepartment() != null && request.getDepartment() != null && 
            !DepartmentMapper.isSameDepartment(staff.getDepartment(), request.getDepartment())) {
            throw new Exception("Department mismatch. Staff belongs to " + staff.getDepartment() + 
                              " but request is for " + request.getDepartment());
        }
        
        // Set initial status
        request.setStatus("PENDING");
        
        // Save to database - transaction will commit immediately after method returns
        Visitor savedRequest = visitorRepository.save(request);
        
        // Force flush to ensure immediate database write
        visitorRepository.flush();
        
        System.out.println("✓ Visitor gatepass request created: ID=" + savedRequest.getId() + 
                         ", Visitor=" + savedRequest.getName() + 
                         ", Staff=" + request.getStaffCode());
        
        return savedRequest;
    }
    
    /**
     * Get all pending requests for a specific staff member
     * Always reads fresh from database - no caching
     */
    @Transactional(readOnly = true)
    public List<Visitor> getPendingRequestsForStaff(String staffId) {
        return visitorRepository.findByStaffCodeAndStatus(staffId, "PENDING");
    }
    
    /**
     * Approve a visitor request and optionally generate QR code
     */
    @Transactional
    public Visitor approveRequest(Long requestId) throws Exception {
        Optional<Visitor> requestOpt = visitorRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new Exception("Request not found with ID: " + requestId);
        }
        
        Visitor request = requestOpt.get();
        
        // Update approval status
        request.setStatus("APPROVED");
        request.setApprovedAt(LocalDateTime.now());
        
        // Generate QR code
        String qrCode = generateQRCode(request);
        request.setQrCode(qrCode);
        
        Visitor savedRequest = visitorRepository.save(request);
        visitorRepository.flush();
        
        System.out.println("✓ Request approved: ID=" + requestId + ", QR=" + qrCode);
        
        return savedRequest;
    }
    
    /**
     * Reject a visitor request with reason
     */
    @Transactional
    public Visitor rejectRequest(Long requestId, String rejectionReason) throws Exception {
        Optional<Visitor> requestOpt = visitorRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new Exception("Request not found with ID: " + requestId);
        }
        
        Visitor request = requestOpt.get();
        
        // Update rejection status
        request.setStatus("REJECTED");
        request.setRejectionReason(rejectionReason);
        request.setRejectedAt(LocalDateTime.now());
        
        Visitor savedRequest = visitorRepository.save(request);
        visitorRepository.flush();
        
        System.out.println("✗ Request rejected: ID=" + requestId + ", Reason=" + rejectionReason);
        
        return savedRequest;
    }
    
    /**
     * Generate QR code in format: VS/visitor_id/random
     */
    private String generateQRCode(Visitor request) {
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "VS/" + request.getId() + "/" + randomPart;
    }
    
    /**
     * Get request by ID
     */
    @Transactional(readOnly = true)
    public Optional<Visitor> getRequestById(Long id) {
        return visitorRepository.findById(id);
    }
    
    /**
     * Get all requests for a staff member (all statuses)
     */
    @Transactional(readOnly = true)
    public List<Visitor> getAllRequestsForStaff(String staffId) {
        return visitorRepository.findByStaffCode(staffId);
    }
}
