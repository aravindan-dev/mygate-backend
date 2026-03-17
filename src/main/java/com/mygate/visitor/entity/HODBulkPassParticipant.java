package com.mygate.visitor.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hod_bulk_pass_participants")
public class HODBulkPassParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private HODBulkGatePassRequest request;

    @Column(name = "participant_id", nullable = false, length = 50)
    private String participantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant_type", nullable = false, length = 10)
    private ParticipantType participantType;

    @Column(name = "participant_name", nullable = false, length = 200)
    private String participantName;

    @Column(name = "is_receiver", nullable = false)
    private Boolean isReceiver = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Enum
    public enum ParticipantType {
        student,
        staff,
        hod
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public HODBulkPassParticipant() {
    }

    public HODBulkPassParticipant(String participantId, ParticipantType participantType, 
                                   String participantName, Boolean isReceiver) {
        this.participantId = participantId;
        this.participantType = participantType;
        this.participantName = participantName;
        this.isReceiver = isReceiver;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HODBulkGatePassRequest getRequest() {
        return request;
    }

    public void setRequest(HODBulkGatePassRequest request) {
        this.request = request;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public ParticipantType getParticipantType() {
        return participantType;
    }

    public void setParticipantType(ParticipantType participantType) {
        this.participantType = participantType;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public Boolean getIsReceiver() {
        return isReceiver;
    }

    public void setIsReceiver(Boolean isReceiver) {
        this.isReceiver = isReceiver;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
