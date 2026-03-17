package com.mygate.visitor.repository;

import com.mygate.visitor.entity.SecurityPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityPersonnelRepository extends JpaRepository<SecurityPersonnel, Long> {
    Optional<SecurityPersonnel> findBySecurityId(String securityId);
    
    // Case-insensitive security ID lookup
    @Query("SELECT s FROM SecurityPersonnel s WHERE LOWER(s.securityId) = LOWER(:securityId)")
    Optional<SecurityPersonnel> findBySecurityIdIgnoreCase(@Param("securityId") String securityId);
    
    Optional<SecurityPersonnel> findByQrCode(String qrCode);
    Optional<SecurityPersonnel> findByEmail(String email);
}
