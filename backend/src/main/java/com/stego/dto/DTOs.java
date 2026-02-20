package com.stego.dto;

import java.time.LocalDateTime;

// ===== Request DTOs =====

class EncodeRequestDTO {
    private String message;
    private String description;

    public EncodeRequestDTO() {}
    public EncodeRequestDTO(String message, String description) {
        this.message = message;
        this.description = description;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

class UpdateDescriptionDTO {
    private String description;

    public UpdateDescriptionDTO() {}
    public UpdateDescriptionDTO(String description) { this.description = description; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

// ===== Response DTOs =====

class StegoImageResponseDTO {
    private Long id;
    private String originalFileName;
    private String encodedFileName;
    private String description;
    private String status;
    private String hiddenMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String encodedImageUrl;

    public StegoImageResponseDTO() {}

    public StegoImageResponseDTO(Long id, String originalFileName, String encodedFileName, String description,
                                  String status, String hiddenMessage, LocalDateTime createdAt,
                                  LocalDateTime updatedAt, String encodedImageUrl) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.encodedFileName = encodedFileName;
        this.description = description;
        this.status = status;
        this.hiddenMessage = hiddenMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.encodedImageUrl = encodedImageUrl;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String originalFileName;
        private String encodedFileName;
        private String description;
        private String status;
        private String hiddenMessage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String encodedImageUrl;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder originalFileName(String v) { this.originalFileName = v; return this; }
        public Builder encodedFileName(String v) { this.encodedFileName = v; return this; }
        public Builder description(String v) { this.description = v; return this; }
        public Builder status(String v) { this.status = v; return this; }
        public Builder hiddenMessage(String v) { this.hiddenMessage = v; return this; }
        public Builder createdAt(LocalDateTime v) { this.createdAt = v; return this; }
        public Builder updatedAt(LocalDateTime v) { this.updatedAt = v; return this; }
        public Builder encodedImageUrl(String v) { this.encodedImageUrl = v; return this; }

        public StegoImageResponseDTO build() {
            return new StegoImageResponseDTO(id, originalFileName, encodedFileName, description, status, hiddenMessage, createdAt, updatedAt, encodedImageUrl);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public String getEncodedFileName() { return encodedFileName; }
    public void setEncodedFileName(String encodedFileName) { this.encodedFileName = encodedFileName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHiddenMessage() { return hiddenMessage; }
    public void setHiddenMessage(String hiddenMessage) { this.hiddenMessage = hiddenMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getEncodedImageUrl() { return encodedImageUrl; }
    public void setEncodedImageUrl(String encodedImageUrl) { this.encodedImageUrl = encodedImageUrl; }
}

class DecodeResponseDTO {
    private Long imageId;
    private String decodedMessage;
    private String originalFileName;
    private LocalDateTime decodedAt;

    public DecodeResponseDTO() {}
    public DecodeResponseDTO(Long imageId, String decodedMessage, String originalFileName, LocalDateTime decodedAt) {
        this.imageId = imageId;
        this.decodedMessage = decodedMessage;
        this.originalFileName = originalFileName;
        this.decodedAt = decodedAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long imageId;
        private String decodedMessage;
        private String originalFileName;
        private LocalDateTime decodedAt;

        public Builder imageId(Long v) { this.imageId = v; return this; }
        public Builder decodedMessage(String v) { this.decodedMessage = v; return this; }
        public Builder originalFileName(String v) { this.originalFileName = v; return this; }
        public Builder decodedAt(LocalDateTime v) { this.decodedAt = v; return this; }
        public DecodeResponseDTO build() { return new DecodeResponseDTO(imageId, decodedMessage, originalFileName, decodedAt); }
    }

    public Long getImageId() { return imageId; }
    public void setImageId(Long imageId) { this.imageId = imageId; }
    public String getDecodedMessage() { return decodedMessage; }
    public void setDecodedMessage(String decodedMessage) { this.decodedMessage = decodedMessage; }
    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }
    public LocalDateTime getDecodedAt() { return decodedAt; }
    public void setDecodedAt(LocalDateTime decodedAt) { this.decodedAt = decodedAt; }
}

class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private int statusCode;

    public ApiResponseDTO() {}
    public ApiResponseDTO(boolean success, String message, T data, int statusCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    public static <T> ApiResponseDTOBuilder<T> builder() { return new ApiResponseDTOBuilder<>(); }

    public static class ApiResponseDTOBuilder<T> {
        private boolean success;
        private String message;
        private T data;
        private int statusCode;

        public ApiResponseDTOBuilder<T> success(boolean v) { this.success = v; return this; }
        public ApiResponseDTOBuilder<T> message(String v) { this.message = v; return this; }
        public ApiResponseDTOBuilder<T> data(T v) { this.data = v; return this; }
        public ApiResponseDTOBuilder<T> statusCode(int v) { this.statusCode = v; return this; }
        public ApiResponseDTO<T> build() { return new ApiResponseDTO<>(success, message, data, statusCode); }
    }

    public static <T> ApiResponseDTO<T> success(String message, T data) {
        ApiResponseDTOBuilder<T> b = new ApiResponseDTOBuilder<>();
        b.success(true).message(message).data(data).statusCode(200);
        return b.build();
    }

    public static <T> ApiResponseDTO<T> error(String message, int statusCode) {
        ApiResponseDTOBuilder<T> b = new ApiResponseDTOBuilder<>();
        b.success(false).message(message).statusCode(statusCode);
        return b.build();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public int getStatusCode() { return statusCode; }
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
}

class StatsDTO {
    private long totalImages;
    private long encodedImages;
    private long decodedImages;
    private long failedImages;

    public StatsDTO() {}
    public StatsDTO(long totalImages, long encodedImages, long decodedImages, long failedImages) {
        this.totalImages = totalImages;
        this.encodedImages = encodedImages;
        this.decodedImages = decodedImages;
        this.failedImages = failedImages;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalImages;
        private long encodedImages;
        private long decodedImages;
        private long failedImages;

        public Builder totalImages(long v) { this.totalImages = v; return this; }
        public Builder encodedImages(long v) { this.encodedImages = v; return this; }
        public Builder decodedImages(long v) { this.decodedImages = v; return this; }
        public Builder failedImages(long v) { this.failedImages = v; return this; }
        public StatsDTO build() { return new StatsDTO(totalImages, encodedImages, decodedImages, failedImages); }
    }

    public long getTotalImages() { return totalImages; }
    public void setTotalImages(long totalImages) { this.totalImages = totalImages; }
    public long getEncodedImages() { return encodedImages; }
    public void setEncodedImages(long encodedImages) { this.encodedImages = encodedImages; }
    public long getDecodedImages() { return decodedImages; }
    public void setDecodedImages(long decodedImages) { this.decodedImages = decodedImages; }
    public long getFailedImages() { return failedImages; }
    public void setFailedImages(long failedImages) { this.failedImages = failedImages; }
}