package com.mygate.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId; // regNo, staffCode, hodCode, or securityId
    
    @Column(name = "security_id")
    private String securityId; // For backward compatibility
    
    @Column(length = 200)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType; // APPROVAL, REJECTION, INFO, URGENT, ENTRY, EXIT
    
    @Column(nullable = false)
    private String type; // For backward compatibility: ENTRY or EXIT
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private NotificationPriority priority; // LOW, NORMAL, HIGH, URGENT
    
    @Column(name = "action_route")
    private String actionRoute; // Screen to navigate to
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // For backward compatibility
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "visitor_name")
    private String visitorName;
    
    @Column(name = "visitor_type")
    private String visitorType;
    
    @Column(name = "deep_link")
    private String deepLink;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
    
    // Enums
    public enum NotificationType {
        APPROVAL,
        REJECTION,
        INFO,
        URGENT,
        ENTRY,
        EXIT,
        GATE_PASS,
        BULK_PASS
    }
    
    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
    
    // Constructor for backward compatibility (visitor notifications)
    public Notification(String securityId, String type, String message, String visitorName, String visitorType) {
        this.userId = securityId;
        this.securityId = securityId;
        this.type = type;
        this.message = message;
        this.visitorName = visitorName;
        this.visitorType = visitorType;
        this.notificationType = "ENTRY".equals(type) ? NotificationType.ENTRY : NotificationType.EXIT;
        this.priority = NotificationPriority.NORMAL;
        this.isRead = false;
    }
    
    // Constructor for gate pass notifications
    public Notification(String userId, String title, String message, 
                       NotificationType notificationType, NotificationPriority priority, String actionRoute) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.type = notificationType.name(); // For backward compatibility
        this.priority = priority;
        this.actionRoute = actionRoute;
        this.isRead = false;
    }
}
