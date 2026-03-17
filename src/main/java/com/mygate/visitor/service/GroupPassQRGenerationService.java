package com.mygate.visitor.service;

import com.mygate.visitor.entity.QRTable;
import com.mygate.visitor.repository.QRTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupPassQRGenerationService {
    
    @Autowired
    private QRTableRepository qrTableRepository;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();
    
    @Transactional
    public Map<String, Object> generateGroupPassQR(GroupPassRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Validate request
            validateRequest(request);
            
            // Generate unique token
            String token = generateUniqueToken();
            
            // Build QR string
            String qrString = buildQRString(request, token);
            
            // Determine group type
            String groupType = determineGroupType(request);
            
            // Create QRTable entry - token goes in EXIT column only (like ST/SF)
            QRTable qrTable = new QRTable();
            qrTable.setPassRequestId(null); // No pass request for direct QR generation
            qrTable.setRequestedByStaffCode(request.getInchargeStaffId());
            qrTable.setQrString(qrString);
            qrTable.setManualEntryCode(generateManualCode());
            qrTable.setPassType("BULK");
            qrTable.setIncludeStaff("SIG".equals(request.getSubtype()));
            qrTable.setStudentCount(request.getStudentRolls() != null ? request.getStudentRolls().size() : 0);
            qrTable.setStaffCount(request.getStaffIds() != null ? request.getStaffIds().size() : 0);
            qrTable.setStatus("ACTIVE");
            qrTable.setUserType("GP");
            qrTable.setUserId(request.getInchargeStaffId());
            qrTable.setQrCode(token);
            qrTable.setGroupType(groupType);
            qrTable.setEntry(null); // No entry column for group pass
            qrTable.setExit(token); // Token goes in exit column
            qrTable.setCreatedAt(LocalDateTime.now());
            qrTable.setUpdatedAt(LocalDateTime.now());
            
            // Save to database
            qrTableRepository.save(qrTable);
            
            // Build response
            result.put("success", true);
            result.put("qrString", qrString);
            result.put("token", token);
            result.put("manualCode", qrTable.getManualEntryCode());
            result.put("passRequestId", qrTable.getPassRequestId());
            result.put("groupType", groupType);
            result.put("totalPersons", calculateTotalPersons(request));
            result.put("message", "Group Pass QR generated successfully");
            
            System.out.println("✅ Generated Group Pass QR: " + token + " for " + request.getInchargeStaffId());
            
        } catch (Exception e) {
            System.err.println("Error generating Group Pass QR: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    private void validateRequest(GroupPassRequest request) {
        if (request.getInchargeStaffId() == null || request.getInchargeStaffId().trim().isEmpty()) {
            throw new IllegalArgumentException("Incharge staff ID is required");
        }
        
        if (request.getSubtype() == null || (!request.getSubtype().equals("SIG") && !request.getSubtype().equals("SEG"))) {
            throw new IllegalArgumentException("Subtype must be SIG or SEG");
        }
        
        boolean hasStudents = request.getStudentRolls() != null && !request.getStudentRolls().isEmpty();
        boolean hasStaff = request.getStaffIds() != null && !request.getStaffIds().isEmpty();
        
        if (!hasStudents && !hasStaff) {
            throw new IllegalArgumentException("At least one student or staff member is required");
        }
    }
    
    private String buildQRString(GroupPassRequest request, String token) {
        StringBuilder qr = new StringBuilder("GP|");
        qr.append(request.getInchargeStaffId()).append("|");
        
        boolean hasStudents = request.getStudentRolls() != null && !request.getStudentRolls().isEmpty();
        boolean hasStaff = request.getStaffIds() != null && !request.getStaffIds().isEmpty();
        
        if (hasStudents && hasStaff) {
            // 5-part format: GP|incharge|students|staff|subtype:token
            qr.append(String.join(",", request.getStudentRolls())).append("|");
            qr.append(String.join(",", request.getStaffIds())).append("|");
        } else if (hasStudents) {
            // 4-part format (students only): GP|incharge|students|subtype:token
            qr.append(String.join(",", request.getStudentRolls())).append("|");
        } else if (hasStaff) {
            // 4-part format (staff only): GP|incharge|staff|subtype:token
            qr.append(String.join(",", request.getStaffIds())).append("|");
        } else {
            // Should not happen due to validation, but handle gracefully
            qr.append("|");
        }
        
        // Subtype and token
        qr.append(request.getSubtype()).append(":").append(token);
        
        return qr.toString();
    }
    
    private String determineGroupType(GroupPassRequest request) {
        boolean hasStudents = request.getStudentRolls() != null && !request.getStudentRolls().isEmpty();
        boolean hasStaff = request.getStaffIds() != null && !request.getStaffIds().isEmpty();
        
        if (hasStudents && hasStaff) {
            return "STUDENTS_AND_STAFF";
        } else if (hasStudents) {
            return "STUDENTS_ONLY";
        } else {
            return "STAFF_ONLY";
        }
    }
    
    private int calculateTotalPersons(GroupPassRequest request) {
        int students = request.getStudentRolls() != null ? request.getStudentRolls().size() : 0;
        int staff = request.getStaffIds() != null ? request.getStaffIds().size() : 0;
        
        if ("SIG".equals(request.getSubtype())) {
            return students + staff + 1; // Include incharge
        } else {
            return students + staff; // Exclude incharge
        }
    }
    
    private String generateUniqueToken() {
        String token;
        int attempts = 0;
        do {
            token = generateRandomToken();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Failed to generate unique token after 100 attempts");
            }
        } while (qrTableRepository.findByQrCode(token).isPresent());
        
        return token;
    }
    
    private String generateRandomToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return token.toString();
    }
    
    private String generateManualCode() {
        return String.format("%04d", random.nextInt(10000));
    }
    
    private Long generatePassRequestId() {
        return System.currentTimeMillis();
    }
    
    // Request DTO
    public static class GroupPassRequest {
        private Long passRequestId;
        private String inchargeStaffId;
        private List<String> studentRolls;
        private List<String> staffIds;
        private String subtype; // SIG or SEG
        
        // Getters and Setters
        public Long getPassRequestId() { return passRequestId; }
        public void setPassRequestId(Long passRequestId) { this.passRequestId = passRequestId; }
        
        public String getInchargeStaffId() { return inchargeStaffId; }
        public void setInchargeStaffId(String inchargeStaffId) { this.inchargeStaffId = inchargeStaffId; }
        
        public List<String> getStudentRolls() { return studentRolls; }
        public void setStudentRolls(List<String> studentRolls) { this.studentRolls = studentRolls; }
        
        public List<String> getStaffIds() { return staffIds; }
        public void setStaffIds(List<String> staffIds) { this.staffIds = staffIds; }
        
        public String getSubtype() { return subtype; }
        public void setSubtype(String subtype) { this.subtype = subtype; }
    }
}
