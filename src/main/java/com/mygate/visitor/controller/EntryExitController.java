package com.mygate.visitor.controller;

import com.mygate.visitor.entity.RailwayEntry;
import com.mygate.visitor.entity.RailwayExitLog;
import com.mygate.visitor.service.EntryExitService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/entry-exit")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class EntryExitController {
    
    private final EntryExitService entryExitService;
    
    // Record student entry - DISABLED: Students use EXIT-ONLY gate passes
    // Entry tracking is only for visitors (VG)
    @PostMapping("/student/entry")
    public ResponseEntity<Map<String, Object>> recordStudentEntry(
        @RequestBody Map<String, String> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Student entry recording is disabled. Students use EXIT-ONLY gate passes.");
        return ResponseEntity.badRequest().body(response);
    }
    
    // Record student exit
    @PostMapping("/student/exit")
    public ResponseEntity<Map<String, Object>> recordStudentExit(
        @RequestBody Map<String, String> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String regNo = request.get("regNo");
            String location = request.getOrDefault("location", "Main Gate");
            String purpose = request.getOrDefault("purpose", "Personal");
            String destination = request.getOrDefault("destination", "");
            String deviceId = request.getOrDefault("deviceId", "MOBILE_APP");
            String ipAddress = getClientIpAddress(httpRequest);
            
            RailwayExitLog exit = entryExitService.recordStudentExit(
                regNo, location, purpose, destination, deviceId, ipAddress);
            
            response.put("success", true);
            response.put("message", "Student exit recorded successfully");
            response.put("data", exit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error recording student exit", e);
            response.put("success", false);
            response.put("message", "Error recording exit: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Record staff entry - DISABLED: Staff use EXIT-ONLY gate passes
    // Entry tracking is only for visitors (VG)
    @PostMapping("/staff/entry")
    public ResponseEntity<Map<String, Object>> recordStaffEntry(
        @RequestBody Map<String, String> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Staff entry recording is disabled. Staff use EXIT-ONLY gate passes.");
        return ResponseEntity.badRequest().body(response);
    }
    
    // Record staff exit
    @PostMapping("/staff/exit")
    public ResponseEntity<Map<String, Object>> recordStaffExit(
        @RequestBody Map<String, String> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String staffCode = request.get("staffCode");
            String location = request.getOrDefault("location", "Main Gate");
            String purpose = request.getOrDefault("purpose", "Official");
            String destination = request.getOrDefault("destination", "");
            String deviceId = request.getOrDefault("deviceId", "MOBILE_APP");
            String ipAddress = getClientIpAddress(httpRequest);
            
            RailwayExitLog exit = entryExitService.recordStaffExit(
                staffCode, location, purpose, destination, deviceId, ipAddress);
            
            response.put("success", true);
            response.put("message", "Staff exit recorded successfully");
            response.put("data", exit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error recording staff exit", e);
            response.put("success", false);
            response.put("message", "Error recording exit: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Record exit with gate pass (using QR code)
    @PostMapping("/gate-pass")
    public ResponseEntity<Map<String, Object>> recordExitWithGatePass(
        @RequestBody Map<String, Object> request,
        HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long gatePassId = Long.valueOf(request.get("gatePassId").toString());
            String location = (String) request.getOrDefault("location", "Main Gate");
            String deviceId = (String) request.getOrDefault("deviceId", "SCANNER_APP");
            String ipAddress = getClientIpAddress(httpRequest);
            
            RailwayExitLog exit = entryExitService.recordExitWithGatePass(gatePassId, location, deviceId, ipAddress);
            
            response.put("success", true);
            response.put("message", "Exit with gate pass recorded successfully");
            response.put("userId", exit.getUserId());
            response.put("userType", exit.getUserType());
            response.put("exitTime", exit.getExitTime());
            response.put("data", exit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error recording exit with gate pass", e);
            response.put("success", false);
            response.put("message", "Error recording exit: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get user entry history
    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getUserHistory(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Map<String, Object>> history = entryExitService.getUserEntryHistory(userId);
            
            response.put("success", true);
            response.put("history", history);
            response.put("count", history.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching user history", e);
            response.put("success", false);
            response.put("message", "Error fetching history: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get user status (currently inside or outside)
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<RailwayEntry> latestEntry = entryExitService.getLatestEntryForUser(userId);
            boolean isInside = entryExitService.isUserCurrentlyInside(userId);
            
            response.put("success", true);
            response.put("userId", userId);
            response.put("isInside", isInside);
            response.put("latestEntry", latestEntry.orElse(null));
            response.put("lastAction", isInside ? "ENTRY" : "EXIT");
            response.put("lastLocation", latestEntry.map(RailwayEntry::getScanLocation).orElse("NONE"));
            response.put("lastTimestamp", latestEntry.map(RailwayEntry::getTimestamp).orElse(null));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching user status", e);
            response.put("success", false);
            response.put("message", "Error fetching status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get today's entries
    @GetMapping("/today")
    public ResponseEntity<Map<String, Object>> getTodaysEntries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<RailwayEntry> entries = entryExitService.getTodaysEntries();
            
            response.put("success", true);
            response.put("entries", entries);
            response.put("count", entries.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching today's entries", e);
            response.put("success", false);
            response.put("message", "Error fetching entries: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long todayEntries = entryExitService.getTodaysEntryCount();
            Long todayExits = entryExitService.getTodaysExitCount();
            Long currentlyInside = todayEntries - todayExits;
            
            response.put("success", true);
            response.put("todayEntries", todayEntries);
            response.put("todayExits", todayExits);
            response.put("currentlyInside", currentlyInside);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching stats", e);
            response.put("success", false);
            response.put("message", "Error fetching stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Helper method to get client IP address
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
