package com.stego.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stego_images")
public class StegoImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String encodedFileName;

    @Column(nullable = false, length = 5000)
    private String hiddenMessage;

    @Column(nullable = false)
    private String uploadPath;

    @Column(nullable = false)
    private String encodedPath;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String status; // ENCODED, DECODED, FAILED

    public StegoImage() {}

    public StegoImage(Long id, String originalFileName, String encodedFileName, String hiddenMessage,
                      String uploadPath, String encodedPath, LocalDateTime createdAt, LocalDateTime updatedAt,
                      String description, String status) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.encodedFileName = encodedFileName;
        this.hiddenMessage = hiddenMessage;
        this.uploadPath = uploadPath;
        this.encodedPath = encodedPath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = description;
        this.status = status;
    }

    // Builder implementation
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String originalFileName;
        private String encodedFileName;
        private String hiddenMessage;
        private String uploadPath;
        private String encodedPath;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String description;
        private String status;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder originalFileName(String v) { this.originalFileName = v; return this; }
        public Builder encodedFileName(String v) { this.encodedFileName = v; return this; }
        public Builder hiddenMessage(String v) { this.hiddenMessage = v; return this; }
        public Builder uploadPath(String v) { this.uploadPath = v; return this; }
        public Builder encodedPath(String v) { this.encodedPath = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder status(String v) { this.status = v; return this; }

        public StegoImage build() {
            return new StegoImage(id, originalFileName, encodedFileName, hiddenMessage,
                    uploadPath, encodedPath, createdAt, updatedAt, description, status);
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getEncodedFileName() { return encodedFileName; }
    public void setEncodedFileName(String encodedFileName) { this.encodedFileName = encodedFileName; }
    public String getHiddenMessage() { return hiddenMessage; }
    public void setHiddenMessage(String hiddenMessage) { this.hiddenMessage = hiddenMessage; }
    public String getUploadPath() { return uploadPath; }
    public void setUploadPath(String uploadPath) { this.uploadPath = uploadPath; }
    public String getEncodedPath() { return encodedPath; }
    public void setEncodedPath(String encodedPath) { this.encodedPath = encodedPath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "ENCODED";
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}