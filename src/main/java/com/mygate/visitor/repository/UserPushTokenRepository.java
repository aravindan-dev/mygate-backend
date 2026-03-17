package com.mygate.visitor.repository;

import com.mygate.visitor.entity.UserPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPushTokenRepository extends JpaRepository<UserPushToken, Long> {
    
    List<UserPushToken> findByUserId(String userId);
    Optional<UserPushToken> findByPushToken(String pushToken);
    Optional<UserPushToken> findByUserIdAndDeviceType(String userId, String deviceType);
    void deleteByUserId(String userId);
    void deleteByPushToken(String pushToken);

    @Query("SELECT t.pushToken FROM UserPushToken t WHERE t.userId = :userId")
    List<String> findTokensByUserId(@Param("userId") String userId);
}
