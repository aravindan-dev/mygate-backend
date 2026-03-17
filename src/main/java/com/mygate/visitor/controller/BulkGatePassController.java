package com.mygate.visitor.controller;

import com.mygate.visitor.service.BulkGatePassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bulk-pass")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BulkGatePassController {
    
    private final BulkGatePassService bulkGatePassService;
    
    // Get students by staff department
    @GetMapping("/students/{staffCode}")
    public ResponseEntity<Map<String, Object>> getStudentsByDepartment(@PathVariable String staffCode) {
        try {
            Map<String, Object> response = bulkGatePassService.getStudentsByStaffDepartment(staffCode);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching students by department", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Create bulk gate pass request
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createBulkGatePass(@RequestBody Map<String, Object> requestData) {
        try {
            String staffCode = (String) requestData.get("staffCode");
            @SuppressWarnings("unchecked")
            List<String> studentRegNos = (List<String>) requestData.get("students");
            String purpose = (String) requestData.get("purpose");
            String reason = (String) requestData.get("reason");
            String attachmentUri = (String) requestData.get("attachmentUri");
            Boolean includeStaff = (Boolean) requestData.get("includeStaff");
            String receiverId = (String) requestData.get("receiverId");
            
            // Parse exit and return date times
            LocalDateTime exitDateTime = null;
            LocalDateTime returnDateTime = null;
            
            try {
                String exitDateTimeStr = (String) requestData.get("exitDateTime");
                if (exitDateTimeStr != null && !exitDateTimeStr.isEmpty()) {
                    exitDateTime = LocalDateTime.parse(exitDateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                }
            } catch (Exception e) {
                log.warn("Error parsing exitDateTime", e);
            }
            
            try {
                String returnDateTimeStr = (String) requestData.get("returnDateTime");
                if (returnDateTimeStr != null && !returnDateTimeStr.isEmpty()) {
                    returnDateTime = LocalDateTime.parse(returnDateTimeStr, DateTimeFormatter.ISO_DATE_TIME);
                }
            } catch (Exception e) {
                log.warn("Error parsing returnDateTime", e);
            }
            
            Map<String, Object> response = bulkGatePassService.createBulkGatePassRequest(
                staffCode, studentRegNos, purpose, reason, exitDateTime, returnDateTime, includeStaff, receiverId, attachmentUri);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating bulk gate pass", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Get bulk gate pass details with student list
    @GetMapping("/details/{requestId}")
    public ResponseEntity<Map<String, Object>> getBulkGatePassDetails(@PathVariable Long requestId) {
        try {
            Map<String, Object> response = bulkGatePassService.getBulkGatePassDetails(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching bulk gate pass details", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Get bulk gate pass details with full student information (for viewing)
    @GetMapping("/{requestId}/students")
    public ResponseEntity<Map<String, Object>> getBulkPassStudentDetails(@PathVariable Long requestId) {
        try {
            Map<String, Object> response = bulkGatePassService.getBulkPassStudentDetails(requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching bulk pass student details", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Validate manual entry code (alternative to QR scanning)
    @GetMapping("/validate/manual")
    public ResponseEntity<Map<String, Object>> validateManualEntryCode(@RequestParam String code) {
        try {
            Map<String, Object> response = bulkGatePassService.validateManualEntryCode(code);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error validating manual entry code", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage(), "valid", false));
        }
    }
    
    // Validate QR string
    @GetMapping("/validate/qr")
    public ResponseEntity<Map<String, Object>> validateQRString(@RequestParam String qrString) {
        try {
            Map<String, Object> response = bulkGatePassService.validateQRString(qrString);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error validating QR string", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage(), "valid", false));
        }
    }
    
    // Record ENTRY scan (one-time only)
    @PostMapping("/scan/entry")
    public ResponseEntity<Map<String, Object>> recordEntryScan(@RequestBody Map<String, Object> scanData) {
        try {
            String identifier = scanData.get("identifier") != null ? 
                scanData.get("identifier").toString() : 
                scanData.get("qrString").toString(); // Support both field names
            String scannedBy = scanData.getOrDefault("scannedBy", "").toString();
            
            Map<String, Object> response = bulkGatePassService.recordEntryScan(identifier, scannedBy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording entry scan", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
    
    // Record EXIT scan (one-time only, marks as COMPLETED)
    @PostMapping("/scan/exit")
    public ResponseEntity<Map<String, Object>> recordExitScan(@RequestBody Map<String, Object> scanData) {
        try {
            String identifier = scanData.get("identifier") != null ? 
                scanData.get("identifier").toString() : 
                scanData.get("qrString").toString(); // Support both field names
            String scannedBy = scanData.getOrDefault("scannedBy", "").toString();
            
            Map<String, Object> response = bulkGatePassService.recordExitScan(identifier, scannedBy);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error recording exit scan", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("success", false, "message", "Error: " + e.getMessage()));
        }
    }
}
