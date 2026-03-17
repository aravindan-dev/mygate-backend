package com.mygate.visitor.repository;

import com.mygate.visitor.entity.HOD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HODRepository extends JpaRepository<HOD, Long> {
    Optional<HOD> findByHodCode(String hodCode);
    List<HOD> findByDepartment(String department);
    List<HOD> findByIsActive(Boolean isActive);
}
