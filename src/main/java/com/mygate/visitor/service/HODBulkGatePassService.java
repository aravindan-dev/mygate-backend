package com.mygate.visitor.service;

import com.mygate.visitor.entity.*;
import com.mygate.visitor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class HODBulkGatePassService {

    private final GatePassRequestRepository gatePassRequestRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HODRepository hodRepository;
    private final HRRepository hrRepository;
    private final QRTableRepository qrTableRepository;

    
    // Create HOD bulk gate pass request using unified Gatepass table
    @Transactional
    public Map<String, Object> createBulkGatePassRequest(String hodCode, List<String> studentRegNos,
                                                         List<String> staffCodes, String purpose, String reason,
                                                         LocalDateTime exitDateTime, LocalDateTime returnDateTime,
                                                         Boolean includeHOD, String receiverId, String attachmentUri) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate HOD
            Optional<HOD> hodOpt = hodRepository.findByHodCode(hodCode);
            if (!hodOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "HOD not found");
                return response;
            }
            
            HOD hod = hodOpt.get();
            String department = hod.getDepartment();
            
            // Validate at least one participant type is selected
            boolean hasStudents = studentRegNos != null && !studentRegNos.isEmpty();
            boolean hasStaff = staffCodes != null && !staffCodes.isEmpty();
            
            if (!hasStudents && !hasStaff) {
                response.put("success", false);
                response.put("message", "No participants selected");
                return response;
            }
            
            // Build eligible members list
            Set<String> eligibleMembers = new HashSet<>();
            if (hasStudents) {
                eligibleMembers.addAll(studentRegNos);
            }
            if (hasStaff) {
                eligibleMembers.addAll(staffCodes);
            }
            
            boolean includeHODFlag = includeHOD != null ? includeHOD : false;
            if (includeHODFlag) {
                eligibleMembers.add(hodCode);
            }
            
            // VALIDATION: If includeHOD = false, receiverId is REQUIRED
            if (!includeHODFlag && (receiverId == null || receiverId.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "Receiver selection required when HOD is not included");
                return response;
            }
            
            // VALIDATION: If receiverId provided, it must be in eligible members
            if (receiverId != null && !receiverId.trim().isEmpty()) {
                if (!eligibleMembers.contains(receiverId)) {
                    response.put("success", false);
                    response.put("message", "Receiver must be part of the selected group");
                    return response;
                }
            }
            
            // Determine QR owner
            String qrOwnerId;
            if (includeHODFlag) {
                qrOwnerId = hodCode; // QR goes to HOD
            } else {
                qrOwnerId = receiverId; // QR goes to selected receiver
            }
            
            // Validate students if any
            if (hasStudents) {
                for (String regNo : studentRegNos) {
                    Optional<Student> studentOpt = studentRepository.findByRegNo(regNo);
                    if (!studentOpt.isPresent()) {
                        response.put("success", false);
                        response.put("message", "Student not found: " + regNo);
                        return response;
                    }
                }
            }
            
            // Validate staff if any
            if (hasStaff) {
                for (String staffCode : staffCodes) {
                    Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
                    if (!staffOpt.isPresent()) {
                        response.put("success", false);
                        response.put("message", "Staff not found: " + staffCode);
                        return response;
                    }
                }
            }
            
            // Find active HR for approval
            String assignedHrCode = findActiveHR();
            if (assignedHrCode == null) {
                response.put("success", false);
                response.put("message", "No active HR found in the system");
                return response;
            }
            
            // Determine bulk type
            String bulkType = includeHODFlag ? "BULK_INCLUDE_HOD" : "BULK_EXCLUDE_HOD";
            
            // Build student and staff lists for QR generation
            // Note: When includeHOD=true, we use SIG subtype to indicate HOD is included
            // We don't need to add HOD code to staff_list - SIG indicator is sufficient
            String studentListStr = hasStudents ? String.join(",", studentRegNos) : "";
            String staffListStr = hasStaff ? String.join(",", staffCodes) : "";
            
            // Create gate pass request using Gatepass table
            GatePassRequest gatePassRequest = new GatePassRequest();
            gatePassRequest.setRegNo(hodCode);
            gatePassRequest.setRequestedByStaffCode(hodCode);
            gatePassRequest.setRequestedByStaffName(hod.getHodName());
            gatePassRequest.setStudentName("HOD Bulk Pass - " + eligibleMembers.size() + " participants");
            gatePassRequest.setDepartment(department);
            gatePassRequest.setPassType("BULK");
            gatePassRequest.setBulkType(bulkType);
            gatePassRequest.setIncludeStaff(includeHODFlag); // Using includeStaff field for includeHOD
            gatePassRequest.setQrOwnerId(qrOwnerId);
            gatePassRequest.setReceiverId(receiverId);
            gatePassRequest.setStudentCount(hasStudents ? studentRegNos.size() : 0);
            gatePassRequest.setStudentList(studentListStr);
            gatePassRequest.setStaffList(staffListStr);
            gatePassRequest.setPurpose(purpose);
            gatePassRequest.setReason(reason);
            gatePassRequest.setRequestDate(LocalDateTime.now());
            gatePassRequest.setRequestSubmittedAt(LocalDateTime.now());
            gatePassRequest.setExitDateTime(exitDateTime);
            gatePassRequest.setReturnDateTime(returnDateTime);
            gatePassRequest.setAttachmentUri(attachmentUri);
            gatePassRequest.setStatus(GatePassRequest.RequestStatus.PENDING_HR);
            gatePassRequest.setStaffApproval(GatePassRequest.ApprovalStatus.APPROVED); // Auto-approved
            gatePassRequest.setHodApproval(GatePassRequest.ApprovalStatus.APPROVED); // Auto-approved
            gatePassRequest.setHrApproval(GatePassRequest.ApprovalStatus.PENDING);
            gatePassRequest.setAssignedHrCode(assignedHrCode);
            gatePassRequest.setStaffApprovedBy(hodCode);
            gatePassRequest.setStaffApprovalDate(LocalDateTime.now());
            gatePassRequest.setHodApprovedBy(hodCode);
            gatePassRequest.setHodApprovalDate(LocalDateTime.now());
            gatePassRequest.setUserType("HOD");
            
            // Save gate pass request
            GatePassRequest savedRequest = gatePassRequestRepository.save(gatePassRequest);
            
            response.put("success", true);
            response.put("message", "HOD bulk gate pass request created successfully");
            response.put("requestId", savedRequest.getId());
            response.put("participantCount", eligibleMembers.size());
            response.put("includeHOD", includeHODFlag);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating HOD bulk gate pass: " + e.getMessage());
            log.error("Error creating HOD bulk gate pass", e);
        }
        
        return response;
    }
    
    // Get HOD bulk gate pass requests
    public List<GatePassRequest> getHODRequests(String hodCode) {
        log.info("Fetching HOD bulk pass requests for: {}", hodCode);
        return gatePassRequestRepository.findByRegNoAndPassTypeOrderByCreatedAtDesc(hodCode, "BULK");
    }
    
    // Get pending HOD bulk pass requests for HR approval
    public List<GatePassRequest> getPendingForHRApproval() {
        log.info("Fetching pending HOD bulk pass requests for HR approval");
        return gatePassRequestRepository.findByUserTypeAndPassTypeAndHrApprovalOrderByCreatedAtDesc(
            "HOD", "BULK", GatePassRequest.ApprovalStatus.PENDING);
    }
    
    // Get HOD bulk pass details
    public Map<String, Object> getBulkGatePassDetails(Long requestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(requestId);
            if (!requestOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Gate pass request not found");
                return response;
            }
            
            GatePassRequest request = requestOpt.get();
            
            // Get participant details from student_list and staff_list
            List<Map<String, String>> participants = new ArrayList<>();
            
            // Add students
            if (request.getStudentList() != null && !request.getStudentList().isEmpty()) {
                String[] studentRegNos = request.getStudentList().split(",");
                for (String regNo : studentRegNos) {
                    Optional<Student> studentOpt = studentRepository.findByRegNo(regNo.trim());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        Map<String, String> info = new HashMap<>();
                        info.put("id", student.getRegNo());
                        info.put("name", student.getFullName());
                        info.put("type", "student");
                        info.put("department", student.getDepartment());
                        participants.add(info);
                    }
                }
            }
            
            // Add staff
            if (request.getStaffList() != null && !request.getStaffList().isEmpty()) {
                String[] staffCodes = request.getStaffList().split(",");
                for (String code : staffCodes) {
                    String trimmedCode = code.trim();
                    // Check if it's HOD
                    Optional<HOD> hodOpt = hodRepository.findByHodCode(trimmedCode);
                    if (hodOpt.isPresent()) {
                        HOD hod = hodOpt.get();
                        Map<String, String> info = new HashMap<>();
                        info.put("id", hod.getHodCode());
                        info.put("name", hod.getHodName());
                        info.put("type", "hod");
                        info.put("department", hod.getDepartment());
                        participants.add(info);
                    } else {
                        // Check if it's staff
                        Optional<Staff> staffOpt = staffRepository.findByStaffCode(trimmedCode);
                        if (staffOpt.isPresent()) {
                            Staff staff = staffOpt.get();
                            Map<String, String> info = new HashMap<>();
                            info.put("id", staff.getStaffCode());
                            info.put("name", staff.getStaffName());
                            info.put("type", "staff");
                            info.put("department", staff.getDepartment());
                            participants.add(info);
                        }
                    }
                }
            }
            
            // Build response
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", request.getId());
            requestData.put("passType", request.getPassType());
            requestData.put("hodCode", request.getRegNo());
            requestData.put("hodName", request.getRequestedByStaffName());
            requestData.put("department", request.getDepartment());
            requestData.put("purpose", request.getPurpose());
            requestData.put("reason", request.getReason());
            requestData.put("exitDateTime", request.getExitDateTime());
            requestData.put("returnDateTime", request.getReturnDateTime());
            requestData.put("status", request.getStatus());
            requestData.put("hrApproval", request.getHrApproval());
            requestData.put("includeHOD", request.getIncludeStaff()); // Using includeStaff field
            requestData.put("receiverId", request.getReceiverId());
            requestData.put("qrOwnerId", request.getQrOwnerId());
            requestData.put("participantCount", participants.size());
            requestData.put("participants", participants);
            requestData.put("qrCode", request.getQrCode());
            requestData.put("createdAt", request.getCreatedAt());
            
            response.put("success", true);
            response.put("request", requestData);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching gate pass details: " + e.getMessage());
            log.error("Error fetching gate pass details", e);
        }
        
        return response;
    }
    
    // Helper: Find active HR
    private String findActiveHR() {
        List<HR> hrList = hrRepository.findAll().stream()
            .filter(HR::getIsActive)
            .collect(java.util.stream.Collectors.toList());
        if (!hrList.isEmpty()) {
            return hrList.get(0).getHrCode();
        }
        return null;
    }
}
