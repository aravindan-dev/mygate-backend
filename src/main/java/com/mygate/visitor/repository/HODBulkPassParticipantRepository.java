package com.mygate.visitor.repository;

import com.mygate.visitor.entity.HODBulkPassParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HODBulkPassParticipantRepository extends JpaRepository<HODBulkPassParticipant, Long> {

    // Find all participants for a request
    List<HODBulkPassParticipant> findByRequestId(Long requestId);

    // Find receiver for a request
    HODBulkPassParticipant findByRequestIdAndIsReceiverTrue(Long requestId);

    // Delete all participants for a request
    void deleteByRequestId(Long requestId);
}
