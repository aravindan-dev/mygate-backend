package com.mygate.visitor.repository;

import com.mygate.visitor.entity.UserPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPushTokenRepository extends JpaRepository<UserPushToken, Long> {
    
    // Find by user ID
    List<UserPushToken> findByUserId(String userId);
    
    // Find by push token
    Optional<UserPushToken> findByPushToken(String pushToken);
    
    // Find by user ID and device type
    Optional<UserPushToken> findByUserIdAndDeviceType(String userId, String deviceType);
    
    // Delete by user ID
    void deleteByUserId(String userId);
    
    // Delete by push token
    void deleteByPushToken(String pushToken);
}
