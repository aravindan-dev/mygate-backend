package com.mygate.visitor.service;

import com.mygate.visitor.entity.*;
import com.mygate.visitor.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntryExitService {
    
    private final RailwayEntryRepository railwayEntryRepository;
    private final RailwayExitLogRepository railwayExitLogRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final GatePassRequestRepository gatePassRequestRepository;
    
    // DISABLED: Students use EXIT-ONLY gate passes, no entry tracking
    // Entry tracking is only for visitors (VG)
    /*
    @Transactional
    public RailwayEntry recordStudentEntry(String regNo, String location, String deviceId, String ipAddress) {
        log.info("Recording student entry: {}", regNo);
        
        Optional<Student> studentOpt = studentRepository.findByRegNo(regNo);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found with registration number: " + regNo);
        }
        
        Student student = studentOpt.get();
        
        RailwayEntry entry = new RailwayEntry();
        entry.setUserType("STUDENT");
        entry.setUserId(regNo);
        entry.setScannedBy("Mobile App");
        entry.setScanLocation(location);
        entry.setTimestamp(LocalDateTime.now());
        
        RailwayEntry saved = railwayEntryRepository.save(entry);
        log.info("Student entry recorded: {} at {}", regNo, location);
        
        return saved;
    }
    */
    
    // Record student exit
    @Transactional
    public RailwayExitLog recordStudentExit(String regNo, String location, String purpose,
                                      String destination, String deviceId, String ipAddress) {
        log.info("Recording student exit: {}", regNo);
        
        Optional<Student> studentOpt = studentRepository.findByRegNo(regNo);
        if (studentOpt.isEmpty()) {
            throw new RuntimeException("Student not found with registration number: " + regNo);
        }
        
        Student student = studentOpt.get();
        
        RailwayExitLog exit = new RailwayExitLog();
        exit.setUserType("STUDENT");
        exit.setUserId(regNo);
        exit.setLocation(location);
        exit.setVerifiedBy("Mobile App");
        exit.setExitTime(LocalDateTime.now());
        
        RailwayExitLog saved = railwayExitLogRepository.save(exit);
        log.info("Student exit recorded: {} at {}", regNo, location);
        
        return saved;
    }
    
    // DISABLED: Staff use EXIT-ONLY gate passes, no entry tracking
    // Entry tracking is only for visitors (VG)
    /*
    @Transactional
    public RailwayEntry recordStaffEntry(String staffCode, String location, String deviceId, String ipAddress) {
        log.info("Recording staff entry: {}", staffCode);
        
        Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
        if (staffOpt.isEmpty()) {
            throw new RuntimeException("Staff not found with code: " + staffCode);
        }
        
        Staff staff = staffOpt.get();
        
        RailwayEntry entry = new RailwayEntry();
        entry.setUserType("STAFF");
        entry.setUserId(staffCode);
        entry.setScannedBy("Mobile App");
        entry.setScanLocation(location);
        entry.setTimestamp(LocalDateTime.now());
        
        RailwayEntry saved = railwayEntryRepository.save(entry);
        log.info("Staff entry recorded: {} at {}", staffCode, location);
        
        return saved;
    }
    */
    
    // Record staff exit
    @Transactional
    public RailwayExitLog recordStaffExit(String staffCode, String location, String purpose,
                                    String destination, String deviceId, String ipAddress) {
        log.info("Recording staff exit: {}", staffCode);
        
        Optional<Staff> staffOpt = staffRepository.findByStaffCode(staffCode);
        if (staffOpt.isEmpty()) {
            throw new RuntimeException("Staff not found with code: " + staffCode);
        }
        
        Staff staff = staffOpt.get();
        
        RailwayExitLog exit = new RailwayExitLog();
        exit.setUserType("STAFF");
        exit.setUserId(staffCode);
        exit.setLocation(location);
        exit.setVerifiedBy("Mobile App");
        exit.setExitTime(LocalDateTime.now());
        
        RailwayExitLog saved = railwayExitLogRepository.save(exit);
        log.info("Staff exit recorded: {} at {}", staffCode, location);
        
        return saved;
    }
    
    // Record exit with gate pass (using QR code)
    @Transactional
    public RailwayExitLog recordExitWithGatePass(Long gatePassId, String location, String deviceId, String ipAddress) {
        log.info("Recording exit with gate pass: {}", gatePassId);
        
        Optional<GatePassRequest> requestOpt = gatePassRequestRepository.findById(gatePassId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Gate pass not found");
        }
        
        GatePassRequest request = requestOpt.get();
        
        RailwayExitLog exit = new RailwayExitLog();
        exit.setUserType(request.getUserType());
        exit.setUserId(request.getRegNo());
        exit.setQrId(gatePassId);
        exit.setLocation(location);
        exit.setVerifiedBy("Scanner App");
        exit.setExitTime(LocalDateTime.now());
        
        RailwayExitLog saved = railwayExitLogRepository.save(exit);
        log.info("Exit with gate pass recorded: {}", gatePassId);
        
        return saved;
    }
    
    // Get user entry history (combined entries and exits)
    // Get user entry history (combined entries and exits) with unified format
        public List<Map<String, Object>> getUserEntryHistory(String userId) {
            List<Map<String, Object>> history = new java.util.ArrayList<>();

            // Add all entries (late entries)
            List<RailwayEntry> entries = railwayEntryRepository.findByUserIdOrderByTimestampDesc(userId);
            for (RailwayEntry entry : entries) {
                Map<String, Object> record = new java.util.HashMap<>();
                record.put("id", entry.getId());
                record.put("type", "ENTRY");
                record.put("timestamp", entry.getTimestamp());
                record.put("entryTime", entry.getTimestamp());
                record.put("gate", entry.getScanLocation() != null ? entry.getScanLocation() : "Main Gate");
                record.put("scanLocation", entry.getScanLocation());
                record.put("purpose", entry.getScanLocation() != null && entry.getScanLocation().contains("Late") ? "Late Arrival" : "Campus Entry");
                record.put("personName", entry.getPersonName());
                record.put("department", entry.getDepartment());
                record.put("scannedBy", entry.getScannedBy());
                history.add(record);
            }

            // Add all exits (gate pass exits)
            List<RailwayExitLog> exits = railwayExitLogRepository.findByUserIdOrderByExitTimeDesc(userId);
            for (RailwayExitLog exit : exits) {
                Map<String, Object> record = new java.util.HashMap<>();
                record.put("id", exit.getId());
                record.put("type", "EXIT");
                record.put("timestamp", exit.getExitTime());
                record.put("exitTime", exit.getExitTime());
                record.put("gate", exit.getLocation() != null ? exit.getLocation() : (exit.getScanLocation() != null ? exit.getScanLocation() : "Main Gate"));
                record.put("scanLocation", exit.getScanLocation());
                record.put("purpose", exit.getPurpose() != null ? exit.getPurpose() : "Campus Exit");
                record.put("destination", exit.getDestination());
                record.put("personName", exit.getPersonName());
                record.put("department", exit.getDepartment());
                record.put("verifiedBy", exit.getVerifiedBy());
                record.put("qrId", exit.getQrId());
                history.add(record);
            }

            // Sort by timestamp descending (most recent first)
            history.sort((a, b) -> {
                LocalDateTime timeA = (LocalDateTime) a.get("timestamp");
                LocalDateTime timeB = (LocalDateTime) b.get("timestamp");
                return timeB.compareTo(timeA);
            });

            return history;
        }
    
    // Get latest entry for user
    public Optional<RailwayEntry> getLatestEntryForUser(String userId) {
        List<RailwayEntry> entries = railwayEntryRepository.findByUserIdOrderByTimestampDesc(userId);
        return entries.isEmpty() ? Optional.empty() : Optional.of(entries.get(0));
    }
    
    // Check if user is currently inside
    public boolean isUserCurrentlyInside(String userId) {
        List<RailwayEntry> entries = railwayEntryRepository.findByUserIdOrderByTimestampDesc(userId);
        List<RailwayExitLog> exits = railwayExitLogRepository.findByUserIdOrderByExitTimeDesc(userId);
        
        if (entries.isEmpty()) {
            return false;
        }
        
        if (exits.isEmpty()) {
            return true;
        }
        
        // Compare latest entry and exit timestamps
        LocalDateTime latestEntry = entries.get(0).getTimestamp();
        LocalDateTime latestExit = exits.get(0).getExitTime();
        
        return latestEntry.isAfter(latestExit);
    }
    
    // Get today's entries
    public List<RailwayEntry> getTodaysEntries() {
        return railwayEntryRepository.findTodaysEntries();
    }
    
    // Get today's entry count
    public Long getTodaysEntryCount() {
        return railwayEntryRepository.countTodaysEntries();
    }
    
    // Get today's exit count
    public Long getTodaysExitCount() {
        return railwayExitLogRepository.countTodaysExits();
    }
}
