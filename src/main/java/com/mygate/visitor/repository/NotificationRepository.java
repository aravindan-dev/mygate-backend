package com.mygate.visitor.repository;

import com.mygate.visitor.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Legacy methods (backward compatibility)
    List<Notification> findBySecurityIdOrderByTimestampDesc(String securityId);
    long countBySecurityIdAndIsReadFalse(String securityId);
    
    // New methods for all users
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(String userId);
    long countByUserIdAndIsReadFalse(String userId);
    
    // Find by notification type
    List<Notification> findByNotificationTypeOrderByCreatedAtDesc(Notification.NotificationType notificationType);
    
    // Find by priority
    List<Notification> findByPriorityOrderByCreatedAtDesc(Notification.NotificationPriority priority);
    
    // Find unread notifications
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();
    
    // Delete by user ID
    void deleteByUserId(String userId);
}
