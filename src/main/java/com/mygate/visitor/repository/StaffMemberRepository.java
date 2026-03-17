package com.mygate.visitor.repository;

import com.mygate.visitor.entity.StaffMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffMemberRepository extends JpaRepository<StaffMember, String> {
    Optional<StaffMember> findByStaffCode(String staffCode);
    List<StaffMember> findByDepartment(String department);
}
