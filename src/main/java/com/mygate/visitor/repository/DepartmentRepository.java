package com.mygate.visitor.repository;

import com.mygate.visitor.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
    Department findByName(String name);
    Department findByCode(String code);
}