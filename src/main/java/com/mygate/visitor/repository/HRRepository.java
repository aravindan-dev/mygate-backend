package com.mygate.visitor.repository;

import com.mygate.visitor.entity.HR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HRRepository extends JpaRepository<HR, Long> {
    Optional<HR> findByHrCode(String hrCode);
    Optional<HR> findByEmail(String email);
    boolean existsByHrCode(String hrCode);
    boolean existsByEmail(String email);
    java.util.List<HR> findByIsActive(boolean isActive);
}
