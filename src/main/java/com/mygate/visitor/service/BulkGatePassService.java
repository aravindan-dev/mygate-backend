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
public class BulkGatePassService {
    
    private final GatePassRequestRepository gatePassRequestRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HODRepository hodRepository;
    private final QRTableRepository qrTableRepository;
    private final GatePassScanLogRepository scanLogRepository;
    
    // Get students by staff department
    public Map<String, Object> getStudentsByStaffDepartment(String staffCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find staff
            Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
            if (!staffOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Staff not found");
                return response;
            }
            
            Staff staff = staffOpt.get();
            String department = staff.getDepartment();
            
            // Get students from same department
            List<Student> students = studentRepository.findByDepartment(department);
            
            // Convert to simple map
            List<Map<String, String>> studentList = new ArrayList<>();
            for (Student s : students) {
                Map<String, String> studentInfo = new HashMap<>();
                studentInfo.put("regNo", s.getRegNo());
                studentInfo.put("studentName", s.getFullName());
                studentInfo.put("department", s.getDepartment());
                studentInfo.put("email", s.getEmail());
                studentList.add(studentInfo);
            }
            
            response.put("success", true);
            response.put("students", studentList);
            response.put("department", department);
            response.put("count", studentList.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching students: " + e.getMessage());
            log.error("Error fetching students", e);
        }
        
        return response;
    }
    
    // Create bulk gate pass request
    @Transactional
    public Map<String, Object> createBulkGatePassRequest(String staffCode, List<String> studentRegNos,
                                                         String purpose, String reason,
                                                         LocalDateTime exitDateTime, LocalDateTime returnDateTime,
                                                         Boolean includeStaff, String receiverId, String attachmentUri) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate staff
            Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
            if (!staffOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Staff not found");
                return response;
            }
            
            Staff staff = staffOpt.get();
            String department = staff.getDepartment();
            
            // Validate students
            if (studentRegNos == null || studentRegNos.isEmpty()) {
                response.put("success", false);
                response.put("message", "No students selected");
                return response;
            }
            
            // Build eligible members list (students + staff if included)
            Set<String> eligibleMembers = new HashSet<>(studentRegNos);
            boolean includeStaffFlag = includeStaff != null ? includeStaff : false;
            if (includeStaffFlag) {
                eligibleMembers.add(staffCode);
            }
            
            // VALIDATION: If includeStaff = false, receiverId is REQUIRED
            if (!includeStaffFlag && (receiverId == null || receiverId.trim().isEmpty())) {
                response.put("success", false);
                response.put("message", "Receiver selection required when staff is not included");
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
            if (includeStaffFlag) {
                qrOwnerId = staffCode; // QR goes to creator (staff)
            } else {
                qrOwnerId = receiverId; // QR goes to selected receiver
            }
            
            // Fetch and validate all students
            List<Student> students = new ArrayList<>();
            for (String regNo : studentRegNos) {
                Optional<Student> studentOpt = studentRepository.findByRegNo(regNo);
                if (!studentOpt.isPresent()) {
                    response.put("success", false);
                    response.put("message", "Student not found: " + regNo);
                    return response;
                }
                
                Student student = studentOpt.get();
                
                // Validate department match
                if (!department.equals(student.getDepartment())) {
                    response.put("success", false);
                    response.put("message", "Student " + regNo + " is not from your department");
                    return response;
                }
                
                students.add(student);
            }
            
            // Find HOD for the department
            List<HOD> hodList = hodRepository.findByDepartment(department);
            String hodCode = (!hodList.isEmpty() && hodList.get(0).getIsActive()) ? hodList.get(0).getHodCode() : null;
            
            // Determine bulk type
            String bulkType = includeStaffFlag ? "BULK_INCLUDE_STAFF" : "BULK_EXCLUDE_STAFF";
            
            // Build student and staff lists for QR generation
            String studentListStr = String.join(",", studentRegNos);
            String staffListStr = includeStaffFlag ? staffCode : "";
            
            // Create gate pass request
            GatePassRequest gatePassRequest = new GatePassRequest();
            gatePassRequest.setRegNo(staff.getStaffCode());
            gatePassRequest.setRequestedByStaffCode(staff.getStaffCode());
            gatePassRequest.setRequestedByStaffName(staff.getStaffName());
            gatePassRequest.setStudentName("Bulk Pass - " + students.size() + " students");
            gatePassRequest.setDepartment(department);
            gatePassRequest.setPassType("BULK");
            gatePassRequest.setBulkType(bulkType);
            gatePassRequest.setIncludeStaff(includeStaffFlag);
            gatePassRequest.setQrOwnerId(qrOwnerId); // Store QR owner
            gatePassRequest.setReceiverId(receiverId); // Store receiver selection
            gatePassRequest.setStudentCount(students.size());
            gatePassRequest.setStudentList(studentListStr); // Store comma-separated student list
            gatePassRequest.setStaffList(staffListStr); // Store comma-separated staff list
            gatePassRequest.setPurpose(purpose);
            gatePassRequest.setReason(reason);
            gatePassRequest.setRequestDate(LocalDateTime.now());
            gatePassRequest.setRequestSubmittedAt(LocalDateTime.now());
            gatePassRequest.setExitDateTime(exitDateTime);
            gatePassRequest.setReturnDateTime(returnDateTime);
            gatePassRequest.setAttachmentUri(attachmentUri);
            gatePassRequest.setStatus(GatePassRequest.RequestStatus.PENDING_HOD);
            gatePassRequest.setStaffApproval(GatePassRequest.ApprovalStatus.APPROVED); // Auto-approved by staff
            gatePassRequest.setHodApproval(GatePassRequest.ApprovalStatus.PENDING);
            gatePassRequest.setAssignedHodCode(hodCode);
            gatePassRequest.setStaffApprovedBy(staff.getStaffCode());
            gatePassRequest.setStaffApprovalDate(LocalDateTime.now());
            gatePassRequest.setUserType("STAFF");
            
            // Save gate pass request
            GatePassRequest savedRequest = gatePassRequestRepository.save(gatePassRequest);
            
            // NOTE: Student mappings removed - using consolidated Gatepass table only
            // All student data is stored in the Gatepass table itself
            // Individual student tracking is not needed for the simplified system
            
            response.put("success", true);
            response.put("message", "Bulk gate pass request created successfully");
            response.put("requestId", savedRequest.getId());
            response.put("studentCount", students.size());
            response.put("includeStaff", includeStaffFlag);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating bulk gate pass: " + e.getMessage());
            log.error("Error creating bulk gate pass", e);
        }
        
        return response;
    }
    
    // Get bulk gate pass details with student list
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
            
            // NOTE: Student list tracking removed - using consolidated Gatepass table only
            // Student details are stored in the Gatepass table, not in a separate table
            List<Map<String, String>> studentList = new ArrayList<>();
            // Empty list since we don't track individual students in separate table
            
            // Get QR table data (handle multiple results by taking the latest)
            List<QRTable> qrTables = qrTableRepository.findAll().stream()
                .filter(qr -> qr.getPassRequestId() != null && qr.getPassRequestId().equals(requestId))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> qrData = new HashMap<>();
            if (!qrTables.isEmpty()) {
                QRTable qrTable = qrTables.get(0); // Get the latest one
                qrData.put("qrString", qrTable.getQrString());
                qrData.put("manualEntryCode", qrTable.getManualEntryCode());
                qrData.put("status", qrTable.getStatus());
                qrData.put("entryScannedAt", qrTable.getEntryScannedAt());
                qrData.put("exitScannedAt", qrTable.getExitScannedAt());
            }
            
            // Build response
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("id", request.getId());
            requestData.put("passType", request.getPassType());
            requestData.put("requestedByStaffCode", request.getRequestedByStaffCode());
            requestData.put("requestedByStaffName", request.getRequestedByStaffName());
            requestData.put("department", request.getDepartment());
            requestData.put("purpose", request.getPurpose());
            requestData.put("reason", request.getReason());
            requestData.put("exitDateTime", request.getExitDateTime());
            requestData.put("returnDateTime", request.getReturnDateTime());
            requestData.put("status", request.getStatus());
            requestData.put("staffApproval", request.getStaffApproval());
            requestData.put("hodApproval", request.getHodApproval());
            requestData.put("studentCount", request.getStudentCount() != null ? request.getStudentCount() : 0);
            requestData.put("students", studentList); // Empty list - student tracking removed
            requestData.put("qrCode", request.getQrCode());
            requestData.put("qrData", qrData);
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
    
    // Get bulk pass with full student details (for viewing)
    public Map<String, Object> getBulkPassStudentDetails(Long requestId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(requestId);
            if (!requestOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Gate pass request not found");
                return response;
            }
            
            GatePassRequest request = requestOpt.get();
            
            // Build requester info
            Map<String, Object> requesterInfo = new HashMap<>();
            requesterInfo.put("name", request.getRequestedByStaffName() != null ? request.getRequestedByStaffName() : request.getStudentName());
            requesterInfo.put("code", request.getRequestedByStaffCode() != null ? request.getRequestedByStaffCode() : request.getRegNo());
            requesterInfo.put("role", request.getUserType() != null ? request.getUserType() : "STAFF");
            requesterInfo.put("department", request.getDepartment());
            
            // Build request info
            Map<String, Object> requestInfo = new HashMap<>();
            requestInfo.put("id", request.getId());
            requestInfo.put("passType", request.getPassType());
            requestInfo.put("bulkType", request.getBulkType());
            requestInfo.put("includeStaff", request.getIncludeStaff());
            requestInfo.put("studentCount", request.getStudentCount());
            requestInfo.put("purpose", request.getPurpose());
            requestInfo.put("reason", request.getReason());
            requestInfo.put("exitDateTime", request.getExitDateTime());
            requestInfo.put("returnDateTime", request.getReturnDateTime());
            requestInfo.put("status", request.getStatus());
            requestInfo.put("hodApproval", request.getHodApproval());
            requestInfo.put("qrGenerated", request.getQrCode() != null);
            requestInfo.put("requestDate", request.getRequestDate());
            
            // Get student details from student_list field
            List<Map<String, Object>> studentList = new ArrayList<>();
            if (request.getStudentList() != null && !request.getStudentList().isEmpty()) {
                String[] studentRegNos = request.getStudentList().split(",");
                for (String regNo : studentRegNos) {
                    Optional<Student> studentOpt = studentRepository.findByRegNo(regNo.trim());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        Map<String, Object> studentInfo = new HashMap<>();
                        studentInfo.put("regNo", student.getRegNo());
                        studentInfo.put("studentName", student.getFullName());
                        studentInfo.put("fullName", student.getFullName());
                        studentInfo.put("department", student.getDepartment());
                        studentInfo.put("email", student.getEmail());
                        studentInfo.put("phone", student.getPhone());
                        // Add QR status (all students in bulk pass share same QR status)
                        studentInfo.put("qrUsed", request.getQrUsed() != null ? request.getQrUsed() : false);
                        studentList.add(studentInfo);
                    }
                }
            }
            
            response.put("success", true);
            response.put("requester", requesterInfo);
            response.put("request", requestInfo);
            response.put("students", studentList);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching bulk pass student details: " + e.getMessage());
            log.error("Error fetching bulk pass student details", e);
        }
        
        return response;
    }
    
    // Validate manual entry code
    public Map<String, Object> validateManualEntryCode(String manualEntryCode) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find QR by manual entry code
            Optional<QRTable> qrTableOpt = qrTableRepository.findByManualEntryCode(manualEntryCode);
            if (!qrTableOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Invalid entry code");
                response.put("valid", false);
                return response;
            }
            
            QRTable qrTable = qrTableOpt.get();
            
            // Check if QR is active
            if (!"ACTIVE".equals(qrTable.getStatus())) {
                response.put("success", false);
                response.put("message", "Entry code is " + qrTable.getStatus());
                response.put("valid", false);
                return response;
            }
            
            // Fetch gate pass from database
            Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(qrTable.getPassRequestId());
            if (!requestOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Gate pass not found in database");
                response.put("valid", false);
                return response;
            }
            
            GatePassRequest request = requestOpt.get();
            
            // Check if approved
            if (request.getHodApproval() != GatePassRequest.ApprovalStatus.APPROVED) {
                response.put("success", false);
                response.put("message", "Gate pass not approved by HOD");
                response.put("valid", false);
                return response;
            }
            
            // Get student details from student_list field
            List<Map<String, String>> studentList = new ArrayList<>();
            if (request.getStudentList() != null && !request.getStudentList().isEmpty()) {
                String[] studentRegNos = request.getStudentList().split(",");
                for (String regNo : studentRegNos) {
                    Optional<Student> studentOpt = studentRepository.findByRegNo(regNo.trim());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        Map<String, String> info = new HashMap<>();
                        info.put("regNo", student.getRegNo());
                        info.put("studentName", student.getFullName());
                        info.put("department", student.getDepartment());
                        studentList.add(info);
                    }
                }
            }
            
            // Build success response
            response.put("success", true);
            response.put("valid", true);
            response.put("message", "✅ Valid Entry Code");
            response.put("passRequestId", qrTable.getPassRequestId());
            response.put("passType", qrTable.getPassType());
            response.put("includeStaff", qrTable.getIncludeStaff());
            response.put("staffCode", qrTable.getRequestedByStaffCode());
            response.put("manualEntryCode", qrTable.getManualEntryCode());
            response.put("qrString", qrTable.getQrString());
            
            // Get staff details
            Optional<Staff> staffOpt = staffRepository.findByStaffCode(qrTable.getRequestedByStaffCode());
            if (staffOpt.isPresent()) {
                response.put("staffName", staffOpt.get().getStaffName());
            }
            
            response.put("department", request.getDepartment());
            response.put("purpose", request.getPurpose());
            response.put("reason", request.getReason());
            response.put("studentCount", qrTable.getStudentCount());
            response.put("students", studentList);
            response.put("exitDateTime", request.getExitDateTime());
            response.put("returnDateTime", request.getReturnDateTime());
            response.put("status", qrTable.getStatus());
            response.put("entryScannedAt", qrTable.getEntryScannedAt());
            response.put("exitScannedAt", qrTable.getExitScannedAt());
            response.put("hodApproval", request.getHodApproval());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error validating entry code: " + e.getMessage());
            response.put("valid", false);
            log.error("Error validating entry code", e);
        }
        
        return response;
    }
    
    // Validate QR string
    public Map<String, Object> validateQRString(String qrString) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find QR in qr_table
            Optional<QRTable> qrTableOpt = qrTableRepository.findByQrString(qrString);
            if (!qrTableOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "QR code not found in system");
                response.put("valid", false);
                return response;
            }
            
            QRTable qrTable = qrTableOpt.get();
            
            // Check if QR is active
            if (!"ACTIVE".equals(qrTable.getStatus())) {
                response.put("success", false);
                response.put("message", "QR code is " + qrTable.getStatus());
                response.put("valid", false);
                return response;
            }
            
            // Fetch gate pass from database
            Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(qrTable.getPassRequestId());
            if (!requestOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "Gate pass not found in database");
                response.put("valid", false);
                return response;
            }
            
            GatePassRequest request = requestOpt.get();
            
            // Check if approved
            if (request.getHodApproval() != GatePassRequest.ApprovalStatus.APPROVED) {
                response.put("success", false);
                response.put("message", "Gate pass not approved by HOD");
                response.put("valid", false);
                return response;
            }
            
            // Get student details from student_list field
            List<Map<String, String>> studentList = new ArrayList<>();
            if (request.getStudentList() != null && !request.getStudentList().isEmpty()) {
                String[] studentRegNos = request.getStudentList().split(",");
                for (String regNo : studentRegNos) {
                    Optional<Student> studentOpt = studentRepository.findByRegNo(regNo.trim());
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        Map<String, String> info = new HashMap<>();
                        info.put("regNo", student.getRegNo());
                        info.put("studentName", student.getFullName());
                        info.put("department", student.getDepartment());
                        studentList.add(info);
                    }
                }
            }
            
            // Build success response
            response.put("success", true);
            response.put("valid", true);
            response.put("message", "✅ Valid Gate Pass");
            response.put("passRequestId", qrTable.getPassRequestId());
            response.put("passType", qrTable.getPassType());
            response.put("includeStaff", qrTable.getIncludeStaff());
            response.put("staffCode", qrTable.getRequestedByStaffCode());
            
            // Get staff details
            Optional<Staff> staffOpt = staffRepository.findByStaffCode(qrTable.getRequestedByStaffCode());
            if (staffOpt.isPresent()) {
                response.put("staffName", staffOpt.get().getStaffName());
            }
            
            response.put("department", request.getDepartment());
            response.put("purpose", request.getPurpose());
            response.put("reason", request.getReason());
            response.put("studentCount", qrTable.getStudentCount());
            response.put("students", studentList);
            response.put("exitDateTime", request.getExitDateTime());
            response.put("returnDateTime", request.getReturnDateTime());
            response.put("status", qrTable.getStatus());
            response.put("entryScannedAt", qrTable.getEntryScannedAt());
            response.put("exitScannedAt", qrTable.getExitScannedAt());
            response.put("hodApproval", request.getHodApproval());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error validating QR: " + e.getMessage());
            response.put("valid", false);
            log.error("Error validating QR", e);
        }
        
        return response;
    }
    
    // Record entry scan (one-time only)
    @Transactional
    public Map<String, Object> recordEntryScan(String identifier, String scannedBy) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find QR in qr_table (try both qrString and manualEntryCode)
            Optional<QRTable> qrTableOpt = qrTableRepository.findByQrString(identifier);
            if (!qrTableOpt.isPresent()) {
                qrTableOpt = qrTableRepository.findByManualEntryCode(identifier);
            }
            
            if (!qrTableOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "QR code or entry code not found");
                return response;
            }
            
            QRTable qrTable = qrTableOpt.get();
            
            // Check if already scanned for entry
            if (qrTable.getEntryScannedAt() != null) {
                response.put("success", false);
                response.put("message", "❌ Entry already scanned at: " + qrTable.getEntryScannedAt());
                response.put("alreadyScanned", true);
                return response;
            }
            
            // Check if QR is active
            if (!"ACTIVE".equals(qrTable.getStatus())) {
                response.put("success", false);
                response.put("message", "QR code is " + qrTable.getStatus());
                return response;
            }
            
            // Record entry scan
            qrTable.setEntryScannedAt(LocalDateTime.now());
            qrTable.setEntryScannedBy(scannedBy);
            qrTableRepository.save(qrTable);
            
            response.put("success", true);
            response.put("message", "✅ Entry scan recorded successfully");
            response.put("entryScannedAt", qrTable.getEntryScannedAt());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error recording entry scan: " + e.getMessage());
            log.error("Error recording entry scan", e);
        }
        
        return response;
    }
    
    // Record exit scan (one-time only, marks as COMPLETED)
    @Transactional
    public Map<String, Object> recordExitScan(String identifier, String scannedBy) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Find QR in qr_table (try both qrString and manualEntryCode)
            Optional<QRTable> qrTableOpt = qrTableRepository.findByQrString(identifier);
            if (!qrTableOpt.isPresent()) {
                qrTableOpt = qrTableRepository.findByManualEntryCode(identifier);
            }
            
            if (!qrTableOpt.isPresent()) {
                response.put("success", false);
                response.put("message", "QR code or entry code not found");
                return response;
            }
            
            QRTable qrTable = qrTableOpt.get();
            
            // Check if entry was scanned first
            if (qrTable.getEntryScannedAt() == null) {
                response.put("success", false);
                response.put("message", "❌ Entry must be scanned before exit");
                return response;
            }
            
            // Check if already scanned for exit
            if (qrTable.getExitScannedAt() != null) {
                response.put("success", false);
                response.put("message", "❌ Exit already scanned at: " + qrTable.getExitScannedAt());
                response.put("alreadyScanned", true);
                return response;
            }
            
            // Record exit scan and mark as COMPLETED
            qrTable.setExitScannedAt(LocalDateTime.now());
            qrTable.setStatus("COMPLETED");
            qrTable.setExitScannedBy(scannedBy);
            qrTableRepository.save(qrTable);
            
            response.put("success", true);
            response.put("message", "✅ Exit scan recorded successfully - Pass marked as COMPLETED");
            response.put("exitScannedAt", qrTable.getExitScannedAt());
            response.put("status", "COMPLETED");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error recording exit scan: " + e.getMessage());
            log.error("Error recording exit scan", e);
        }
        
        return response;
    }
}
