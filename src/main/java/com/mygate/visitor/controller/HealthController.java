package com.mygate.visitor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Endpoint for Wi-Fi Connectivity Testing
 * 
 * Use this endpoint to verify backend is reachable over Wi-Fi LAN
 * Test with: curl http://<wifi-ip>:8080/api/health
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Smart Gate API");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("message", "Backend is running and accessible");
        
        return ResponseEntity.ok(response);
    }
}
