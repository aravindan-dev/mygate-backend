package com.mygate.visitor.repository;

import com.mygate.visitor.entity.Person;
import com.mygate.visitor.entity.PersonType;
import com.mygate.visitor.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByQrCode(String qrCode);
    List<Person> findByType(PersonType type);
    List<Person> findByStatus(ApprovalStatus status);
    List<Person> findByTypeAndStatus(PersonType type, ApprovalStatus status);
    Optional<Person> findByEmail(String email);
    Optional<Person> findByStudentId(String studentId);
    Optional<Person> findByFacultyId(String facultyId);
}