package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hod_bulk_gate_pass_requests")
public class HODBulkGatePassRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hod_code", nullable = false, length = 50)
    private String hodCode;

    @Column(name = "purpose", nullable = false, length = 200)
    private String purpose;

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "exit_date_time", nullable = false)
    private LocalDateTime exitDateTime;

    @Column(name = "return_date_time", nullable = false)
    private LocalDateTime returnDateTime;

    @Column(name = "include_hod", nullable = false)
    private Boolean includeHOD = false;

    @Column(name = "receiver_id", length = 50)
    private String receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", length = 10)
    private ReceiverType receiverType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING_HR;

    @Column(name = "approved_by_hr", length = 50)
    private String approvedByHr;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<HODBulkPassParticipant> participants = new ArrayList<>();

    // Enums
    public enum RequestStatus {
        PENDING_HR,
        APPROVED,
        REJECTED
    }

    public enum ReceiverType {
        student,
        staff,
        hod
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public HODBulkGatePassRequest() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHodCode() {
        return hodCode;
    }

    public void setHodCode(String hodCode) {
        this.hodCode = hodCode;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getExitDateTime() {
        return exitDateTime;
    }

    public void setExitDateTime(LocalDateTime exitDateTime) {
        this.exitDateTime = exitDateTime;
    }

    public LocalDateTime getReturnDateTime() {
        return returnDateTime;
    }

    public void setReturnDateTime(LocalDateTime returnDateTime) {
        this.returnDateTime = returnDateTime;
    }

    public Boolean getIncludeHOD() {
        return includeHOD;
    }

    public void setIncludeHOD(Boolean includeHOD) {
        this.includeHOD = includeHOD;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public ReceiverType getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(ReceiverType receiverType) {
        this.receiverType = receiverType;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getApprovedByHr() {
        return approvedByHr;
    }

    public void setApprovedByHr(String approvedByHr) {
        this.approvedByHr = approvedByHr;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<HODBulkPassParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<HODBulkPassParticipant> participants) {
        this.participants = participants;
    }

    // Helper methods
    public void addParticipant(HODBulkPassParticipant participant) {
        participants.add(participant);
        participant.setRequest(this);
    }

    public void removeParticipant(HODBulkPassParticipant participant) {
        participants.remove(participant);
        participant.setRequest(null);
    }

    public int getParticipantCount() {
        return participants.size();
    }
}
