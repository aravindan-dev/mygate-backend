package com.mygate.visitor.repository;

import com.mygate.visitor.entity.StaffDirectory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffDirectoryRepository extends JpaRepository<StaffDirectory, String> {
    
    Optional<StaffDirectory> findByStaffId(String staffId);
    
    @Query("SELECT s FROM StaffDirectory s WHERE s.staffId IN :staffIds")
    List<StaffDirectory> findByStaffIdIn(@Param("staffIds") List<String> staffIds);
}
