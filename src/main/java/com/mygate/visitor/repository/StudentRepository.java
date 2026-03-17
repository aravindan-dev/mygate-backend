package com.mygate.visitor.repository;

import com.mygate.visitor.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegNo(String regNo);
    
    @Query("SELECT s FROM Student s WHERE s.regNo IN :regNos")
    List<Student> findByRegNoIn(@Param("regNos") List<String> regNos);
    
    // Find students by department
    List<Student> findByDepartment(String department);
}
