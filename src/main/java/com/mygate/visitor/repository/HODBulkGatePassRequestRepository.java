package com.mygate.visitor.repository;

import com.mygate.visitor.entity.HODBulkGatePassRequest;
import com.mygate.visitor.entity.HODBulkGatePassRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HODBulkGatePassRequestRepository extends JpaRepository<HODBulkGatePassRequest, Long> {

    // Find all requests by HOD code
    List<HODBulkGatePassRequest> findByHodCodeOrderByCreatedAtDesc(String hodCode);

    // Find requests by HOD code and status
    List<HODBulkGatePassRequest> findByHodCodeAndStatusOrderByCreatedAtDesc(String hodCode, RequestStatus status);

    // Find all pending requests (for HR broadcast)
    List<HODBulkGatePassRequest> findByStatusOrderByCreatedAtDesc(RequestStatus status);

    // Find request by ID with participants
    @Query("SELECT r FROM HODBulkGatePassRequest r LEFT JOIN FETCH r.participants WHERE r.id = :id")
    Optional<HODBulkGatePassRequest> findByIdWithParticipants(@Param("id") Long id);

    // Count pending requests
    long countByStatus(RequestStatus status);

    // Count pending requests for specific HOD
    long countByHodCodeAndStatus(String hodCode, RequestStatus status);
}
