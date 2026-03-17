package com.mygate.visitor.controller;

import com.mygate.visitor.entity.QRTable;
import com.mygate.visitor.repository.QRTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/qr-codes")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Autowired
    private QRTableRepository qrTableRepository;

    /**
     * Get all QR codes
     */
    @GetMapping
    public ResponseEntity<List<QRTable>> getAllQRCodes() {
        try {
            List<QRTable> qrCodes = qrTableRepository.findAll();
            return ResponseEntity.ok(qrCodes);
        } catch (Exception e) {
            System.err.println("Error fetching all QR codes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all active QR codes
     */
    @GetMapping("/active")
    public ResponseEntity<List<QRTable>> getActiveQRCodes() {
        try {
            List<QRTable> allQRCodes = qrTableRepository.findAll();
            List<QRTable> activeQRCodes = allQRCodes.stream()
                .filter(qr -> "ACTIVE".equalsIgnoreCase(qr.getStatus()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(activeQRCodes);
        } catch (Exception e) {
            System.err.println("Error fetching active QR codes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get QR code by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<QRTable> getQRCodeById(@PathVariable Long id) {
        try {
            return qrTableRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            System.err.println("Error fetching QR code by ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get QR codes by pass request ID
     */
    @GetMapping("/pass-request/{requestId}")
    public ResponseEntity<List<QRTable>> getQRCodesByPassRequestId(@PathVariable Long requestId) {
        try {
            List<QRTable> allQRCodes = qrTableRepository.findAll();
            List<QRTable> requestQRCodes = allQRCodes.stream()
                .filter(qr -> qr.getPassRequestId() != null && qr.getPassRequestId().equals(requestId))
                .collect(Collectors.toList());
            return ResponseEntity.ok(requestQRCodes);
        } catch (Exception e) {
            System.err.println("Error fetching QR codes by pass request ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get QR codes by user type
     */
    @GetMapping("/user-type/{userType}")
    public ResponseEntity<List<QRTable>> getQRCodesByUserType(@PathVariable String userType) {
        try {
            List<QRTable> allQRCodes = qrTableRepository.findAll();
            List<QRTable> userTypeQRCodes = allQRCodes.stream()
                .filter(qr -> userType.equalsIgnoreCase(qr.getUserType()))
                .collect(Collectors.toList());
            return ResponseEntity.ok(userTypeQRCodes);
        } catch (Exception e) {
            System.err.println("Error fetching QR codes by user type: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete QR code by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQRCode(@PathVariable Long id) {
        try {
            if (qrTableRepository.existsById(id)) {
                qrTableRepository.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error deleting QR code: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
