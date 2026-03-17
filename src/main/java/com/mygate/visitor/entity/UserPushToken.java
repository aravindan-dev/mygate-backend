package com.mygate.visitor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_push_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPushToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId; // regNo, staffCode, hodCode, or securityId
    
    @Column(name = "push_token", nullable = false, unique = true)
    private String pushToken; // Expo push token
    
    @Column(name = "device_type")
    private String deviceType; // ANDROID, IOS, WEB
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    // Constructor for easy creation
    public UserPushToken(String userId, String pushToken, String deviceType) {
        this.userId = userId;
        this.pushToken = pushToken;
        this.deviceType = deviceType;
    }
}
