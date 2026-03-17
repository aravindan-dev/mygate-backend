package com.mygate.visitor.service;

import com.mygate.visitor.entity.GatePassRequest;
import com.mygate.visitor.entity.Notification;
import com.mygate.visitor.repository.NotificationRepository;
import com.mygate.visitor.repository.StudentRepository;
import com.mygate.visitor.repository.StaffRepository;
import com.mygate.visitor.repository.HODRepository;
import com.mygate.visitor.repository.HRRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;
    private final HODRepository hodRepository;
    private final HRRepository hrRepository;
    
    public NotificationService(
            NotificationRepository notificationRepository,
            StudentRepository studentRepository,
            StaffRepository staffRepository,
            HODRepository hodRepository,
            HRRepository hrRepository) {
        this.notificationRepository = notificationRepository;
        this.studentRepository = studentRepository;
        this.staffRepository = staffRepository;
        this.hodRepository = hodRepository;
        this.hrRepository = hrRepository;
    }
    
    // ==================== STUDENT GATE PASS NOTIFICATIONS ====================
    
    /**
     * Notify staff when student submits a gate pass request
     */
    @Transactional
    public void notifyStaffOfNewStudentRequest(GatePassRequest request) {
        try {
            String studentName = request.getStudentName();
            String staffCode = request.getAssignedStaffCode();
            
            String title = "New Gate Pass Request";
            String message = String.format("New Gate Pass Request from %s. Please review the request.", studentName);
            
            Notification notification = new Notification(
                staffCode,
                title,
                message,
                Notification.NotificationType.GATE_PASS,
                Notification.NotificationPriority.HIGH,
                "/staff/pending-approvals"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to staff {} for new student request from {}", staffCode, studentName);
            
        } catch (Exception e) {
            log.error("Error sending notification to staff", e);
        }
    }
    
    /**
     * Notify student when staff approves their request
     */
    @Transactional
    public void notifyStudentOfStaffApproval(GatePassRequest request) {
        try {
            String regNo = request.getRegNo();
            
            String title = "Request Approved by Staff";
            String message = "Your Gate Pass Request has been approved by Staff and is waiting for HOD approval.";
            
            Notification notification = new Notification(
                regNo,
                title,
                message,
                Notification.NotificationType.APPROVAL,
                Notification.NotificationPriority.NORMAL,
                "/student/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to student {} for staff approval", regNo);
            
        } catch (Exception e) {
            log.error("Error sending notification to student", e);
        }
    }
    
    /**
     * Notify HOD when staff approves a request
     */
    @Transactional
    public void notifyHODOfStaffApproval(GatePassRequest request) {
        try {
            String hodCode = request.getAssignedHodCode();
            String studentName = request.getStudentName();
            
            String title = "Request Awaiting Your Approval";
            String message = String.format("A Gate Pass Request from %s approved by Staff is waiting for your approval.", studentName);
            
            Notification notification = new Notification(
                hodCode,
                title,
                message,
                Notification.NotificationType.GATE_PASS,
                Notification.NotificationPriority.HIGH,
                "/hod/pending-approvals"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to HOD {} for staff-approved request", hodCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to HOD", e);
        }
    }
    
    /**
     * Notify student when staff rejects their request
     */
    @Transactional
    public void notifyStudentOfStaffRejection(GatePassRequest request) {
        try {
            String regNo = request.getRegNo();
            
            String title = "Request Rejected by Staff";
            String message = "Your Gate Pass Request was rejected by Staff. Please check remarks.";
            
            Notification notification = new Notification(
                regNo,
                title,
                message,
                Notification.NotificationType.REJECTION,
                Notification.NotificationPriority.HIGH,
                "/student/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to student {} for staff rejection", regNo);
            
        } catch (Exception e) {
            log.error("Error sending notification to student", e);
        }
    }
    
    /**
     * Notify student when HOD approves their request (QR ready)
     */
    @Transactional
    public void notifyStudentOfHODApproval(GatePassRequest request) {
        try {
            String regNo = request.getRegNo();
            
            String title = "Gate Pass Approved!";
            String message = "Your Gate Pass Request has been approved by HOD. Your QR Gate Pass is ready.";
            
            Notification notification = new Notification(
                regNo,
                title,
                message,
                Notification.NotificationType.APPROVAL,
                Notification.NotificationPriority.URGENT,
                "/student/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to student {} for HOD approval (QR ready)", regNo);
            
        } catch (Exception e) {
            log.error("Error sending notification to student", e);
        }
    }
    
    /**
     * Notify student when HOD rejects their request
     */
    @Transactional
    public void notifyStudentOfHODRejection(GatePassRequest request) {
        try {
            String regNo = request.getRegNo();
            
            String title = "Request Rejected by HOD";
            String message = "Your Gate Pass Request was rejected by HOD. Please check remarks.";
            
            Notification notification = new Notification(
                regNo,
                title,
                message,
                Notification.NotificationType.REJECTION,
                Notification.NotificationPriority.HIGH,
                "/student/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to student {} for HOD rejection", regNo);
            
        } catch (Exception e) {
            log.error("Error sending notification to student", e);
        }
    }
    
    // ==================== STAFF GATE PASS NOTIFICATIONS ====================
    
    /**
     * Notify HOD when staff submits a gate pass request (self or bulk)
     */
    @Transactional
    public void notifyHODOfNewStaffRequest(GatePassRequest request) {
        try {
            String hodCode = request.getAssignedHodCode();
            String staffName = request.getStudentName(); // Staff name is stored here
            String passType = "BULK".equals(request.getPassType()) ? "Bulk Gate Pass" : "Gate Pass";
            
            String title = "New Staff Request";
            String message = String.format("New %s Request from Staff %s. Please review.", passType, staffName);
            
            Notification notification = new Notification(
                hodCode,
                title,
                message,
                Notification.NotificationType.GATE_PASS,
                Notification.NotificationPriority.HIGH,
                "/hod/pending-approvals"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to HOD {} for new staff request", hodCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to HOD", e);
        }
    }
    
    /**
     * Notify staff when HOD approves their request (QR ready)
     */
    @Transactional
    public void notifyStaffOfHODApproval(GatePassRequest request) {
        try {
            String staffCode = request.getRegNo(); // Staff code is stored in regNo
            String passType = "BULK".equals(request.getPassType()) ? "Bulk Gate Pass" : "Gate Pass";
            
            String title = passType + " Approved!";
            String message = String.format("Your %s Request has been approved by HOD. Your QR pass is ready.", passType);
            
            Notification notification = new Notification(
                staffCode,
                title,
                message,
                Notification.NotificationType.APPROVAL,
                Notification.NotificationPriority.URGENT,
                "/staff/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to staff {} for HOD approval (QR ready)", staffCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to staff", e);
        }
    }
    
    /**
     * Notify staff when HOD rejects their request
     */
    @Transactional
    public void notifyStaffOfHODRejection(GatePassRequest request) {
        try {
            String staffCode = request.getRegNo(); // Staff code is stored in regNo
            String passType = "BULK".equals(request.getPassType()) ? "Bulk Gate Pass" : "Gate Pass";
            
            String title = "Request Rejected";
            String message = String.format("Your %s Request was rejected by HOD. Please check remarks.", passType);
            
            Notification notification = new Notification(
                staffCode,
                title,
                message,
                Notification.NotificationType.REJECTION,
                Notification.NotificationPriority.HIGH,
                "/staff/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to staff {} for HOD rejection", staffCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to staff", e);
        }
    }
    
    // ==================== BULK PASS RECEIVER NOTIFICATIONS ====================
    
    /**
     * Notify bulk pass receivers when QR is generated (for SEG passes)
     */
    @Transactional
    public void notifyBulkPassReceivers(GatePassRequest request, List<String> receiverIds) {
        try {
            String staffName = request.getStudentName(); // Staff name who created the pass
            
            for (String receiverId : receiverIds) {
                String title = "Bulk Gate Pass Issued";
                String message = String.format("A Gate Pass QR has been issued to you by %s.", staffName);
                
                Notification notification = new Notification(
                    receiverId,
                    title,
                    message,
                    Notification.NotificationType.BULK_PASS,
                    Notification.NotificationPriority.URGENT,
                    "/my-requests"
                );
                
                notificationRepository.save(notification);
                log.info("📧 Notification sent to receiver {} for bulk pass", receiverId);
            }
            
        } catch (Exception e) {
            log.error("Error sending notifications to bulk pass receivers", e);
        }
    }
    
    /**
     * Notify single receiver when QR is generated (for SEG passes with single receiver)
     */
    @Transactional
    public void notifyBulkPassReceiver(GatePassRequest request) {
        try {
            String receiverId = request.getQrOwnerId();
            if (receiverId == null || receiverId.isEmpty()) {
                log.warn("No receiver ID found for bulk pass request {}", request.getId());
                return;
            }
            
            String staffName = request.getStudentName(); // Staff name who created the pass
            
            String title = "Bulk Gate Pass Issued";
            String message = String.format("A Gate Pass QR has been issued to you by %s.", staffName);
            
            Notification notification = new Notification(
                receiverId,
                title,
                message,
                Notification.NotificationType.BULK_PASS,
                Notification.NotificationPriority.URGENT,
                "/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to receiver {} for bulk pass", receiverId);
            
        } catch (Exception e) {
            log.error("Error sending notification to bulk pass receiver", e);
        }
    }
    
    // ==================== HOD GATE PASS NOTIFICATIONS ====================
    
    /**
     * Notify HR when HOD submits a self gate pass request
     */
    @Transactional
    public void notifyHROfNewHODRequest(GatePassRequest request) {
        try {
            String hrCode = request.getAssignedHrCode();
            String hodName = request.getStudentName(); // HOD name is stored here
            
            String title = "New HOD Request";
            String message = String.format("HOD %s has requested a Gate Pass. Please review.", hodName);
            
            Notification notification = new Notification(
                hrCode,
                title,
                message,
                Notification.NotificationType.GATE_PASS,
                Notification.NotificationPriority.HIGH,
                "/hr/pending-approvals"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to HR {} for new HOD request", hrCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to HR", e);
        }
    }
    
    /**
     * Notify HOD when HR approves their request (QR ready)
     */
    @Transactional
    public void notifyHODOfHRApproval(GatePassRequest request) {
        try {
            String hodCode = request.getRegNo(); // HOD code is stored in regNo
            
            String title = "Gate Pass Approved!";
            String message = "Your Gate Pass Request has been approved by HR. Your QR pass is ready.";
            
            Notification notification = new Notification(
                hodCode,
                title,
                message,
                Notification.NotificationType.APPROVAL,
                Notification.NotificationPriority.URGENT,
                "/hod/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to HOD {} for HR approval (QR ready)", hodCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to HOD", e);
        }
    }
    
    /**
     * Notify HOD when HR rejects their request
     */
    @Transactional
    public void notifyHODOfHRRejection(GatePassRequest request) {
        try {
            String hodCode = request.getRegNo(); // HOD code is stored in regNo
            
            String title = "Request Rejected";
            String message = "Your Gate Pass Request was rejected by HR. Please check remarks.";
            
            Notification notification = new Notification(
                hodCode,
                title,
                message,
                Notification.NotificationType.REJECTION,
                Notification.NotificationPriority.HIGH,
                "/hod/my-requests"
            );
            
            notificationRepository.save(notification);
            log.info("📧 Notification sent to HOD {} for HR rejection", hodCode);
            
        } catch (Exception e) {
            log.error("Error sending notification to HOD", e);
        }
    }
    
    /**
     * Notify receivers when HOD creates a bulk pass (no approval required)
     */
    @Transactional
    public void notifyHODBulkPassReceivers(GatePassRequest request, List<String> receiverIds) {
        try {
            String hodName = request.getStudentName(); // HOD name who created the pass
            
            for (String receiverId : receiverIds) {
                String title = "Bulk Gate Pass Issued";
                String message = String.format("A Gate Pass QR has been issued to you by HOD %s.", hodName);
                
                Notification notification = new Notification(
                    receiverId,
                    title,
                    message,
                    Notification.NotificationType.BULK_PASS,
                    Notification.NotificationPriority.URGENT,
                    "/my-requests"
                );
                
                notificationRepository.save(notification);
                log.info("📧 Notification sent to receiver {} for HOD bulk pass", receiverId);
            }
            
        } catch (Exception e) {
            log.error("Error sending notifications to HOD bulk pass receivers", e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get all notifications for a user
     */
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Get unread notifications count for a user
     */
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }
    
    // ==================== LEGACY METHODS (For backward compatibility) ====================
    
    /**
     * Create visitor notification (for security personnel)
     * Legacy method for visitor entry/exit notifications
     */
    @Transactional
    public void createVisitorNotification(String securityId, String type, String message, 
                                         String visitorName, String visitorType) {
        try {
            Notification notification = new Notification(securityId, type, message, visitorName, visitorType);
            notificationRepository.save(notification);
            log.info("📧 Visitor notification sent to security {}", securityId);
        } catch (Exception e) {
            log.error("Error creating visitor notification", e);
        }
    }
    
    /**
     * Create user notification (generic method for late entry, etc.)
     */
    @Transactional
    public void createUserNotification(String userId, String title, String message, 
                                      String notificationType, String priority) {
        try {
            Notification.NotificationType type = Notification.NotificationType.valueOf(notificationType);
            Notification.NotificationPriority priorityEnum = Notification.NotificationPriority.valueOf(priority);
            
            Notification notification = new Notification(
                userId,
                title,
                message,
                type,
                priorityEnum,
                null
            );
            
            notificationRepository.save(notification);
            log.info("📧 User notification sent to {}", userId);
        } catch (Exception e) {
            log.error("Error creating user notification", e);
        }
    }
    
    /**
     * Get notifications by security ID (legacy method)
     */
    public List<Notification> getNotificationsBySecurityId(String securityId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(securityId);
    }
}

