package com.pharmacy.admin.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminEmail;
    private String action;
    private String targetEntity;
    private String targetId;
    private String details;
    private LocalDateTime timestamp = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(Long id, String adminEmail, String action, String targetEntity,
                    String targetId, String details, LocalDateTime timestamp) {
        this.id = id;
        this.adminEmail = adminEmail;
        this.action = action;
        this.targetEntity = targetEntity;
        this.targetId = targetId;
        this.details = details;
        this.timestamp = timestamp;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String adminEmail;
        private String action;
        private String targetEntity;
        private String targetId;
        private String details;
        private LocalDateTime timestamp = LocalDateTime.now();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder adminEmail(String adminEmail) { this.adminEmail = adminEmail; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder targetEntity(String targetEntity) { this.targetEntity = targetEntity; return this; }
        public Builder targetId(String targetId) { this.targetId = targetId; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public AuditLog build() {
            return new AuditLog(id, adminEmail, action, targetEntity, targetId, details, timestamp);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getTargetEntity() { return targetEntity; }
    public void setTargetEntity(String targetEntity) { this.targetEntity = targetEntity; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
