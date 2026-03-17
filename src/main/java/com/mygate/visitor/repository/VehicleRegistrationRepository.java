package com.mygate.visitor.repository;

import com.mygate.visitor.entity.VehicleRegistration;
import com.mygate.visitor.entity.PersonType;
import com.mygate.visitor.entity.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRegistrationRepository extends JpaRepository<VehicleRegistration, Long> {
    Optional<VehicleRegistration> findByLicensePlate(String licensePlate);
    List<VehicleRegistration> findByOwnerType(PersonType ownerType);
    List<VehicleRegistration> findByStatus(ApprovalStatus status);
    List<VehicleRegistration> findByOwnerName(String ownerName);
    List<VehicleRegistration> findByOwnerPhone(String ownerPhone);
}