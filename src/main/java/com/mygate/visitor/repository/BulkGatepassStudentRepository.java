package com.mygate.visitor.repository;

import com.mygate.visitor.entity.BulkGatepassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkGatepassStudentRepository extends JpaRepository<BulkGatepassStudent, Long> {
    
    // Find all students for a pass request
    List<BulkGatepassStudent> findByPassRequestId(Long passRequestId);
    
    // Find all pass requests for a student
    List<BulkGatepassStudent> findByRegNo(String regNo);
    
    // Count students in a pass
    long countByPassRequestId(Long passRequestId);
    
    // Delete all students for a pass request
    void deleteByPassRequestId(Long passRequestId);
}
