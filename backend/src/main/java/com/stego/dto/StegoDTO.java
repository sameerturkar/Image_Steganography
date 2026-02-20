package com.stego.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class StegoDTO {

    // ---- Request ----

    public static class EncodeRequest {
        @NotBlank(message = "Message cannot be blank")
        @Size(min = 1, max = 10000, message = "Message must be between 1 and 10000 characters")
        private String message;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        public EncodeRequest() {}

        public EncodeRequest(String message, String description) {
            this.message = message;
            this.description = description;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class UpdateRequest {
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        private String status;

        public UpdateRequest() {}
        public UpdateRequest(String description, String status) {
            this.description = description;
            this.status = status;
        }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    // ---- Response ----

    public static class ImageResponse {
        private Long id;
        private String originalFileName;
        private String encodedFileName;
        private String description;
        private String status;
        private String hiddenMessage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String encodedImageUrl;

        public ImageResponse() {}

        public ImageResponse(Long id, String originalFileName, String encodedFileName, String description,
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

            public ImageResponse build() {
                return new ImageResponse(id, originalFileName, encodedFileName, description,
                        status, hiddenMessage, createdAt, updatedAt, encodedImageUrl);
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

    public static class DecodeResponse {
        private Long imageId;
        private String decodedMessage;
        private String originalFileName;
        private LocalDateTime decodedAt;

        public DecodeResponse() {}

        public DecodeResponse(Long imageId, String decodedMessage, String originalFileName, LocalDateTime decodedAt) {
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
            public DecodeResponse build() { return new DecodeResponse(imageId, decodedMessage, originalFileName, decodedAt); }
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

    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private int statusCode;

        public ApiResponse() {}

        public ApiResponse(boolean success, String message, T data, int statusCode) {
            this.success = success;
            this.message = message;
            this.data = data;
            this.statusCode = statusCode;
        }

        public static <T> ApiResponseBuilder<T> builder() { return new ApiResponseBuilder<>(); }

        public static class ApiResponseBuilder<T> {
            private boolean success;
            private String message;
            private T data;
            private int statusCode;

            public ApiResponseBuilder<T> success(boolean v) { this.success = v; return this; }
            public ApiResponseBuilder<T> message(String v) { this.message = v; return this; }
            public ApiResponseBuilder<T> data(T v) { this.data = v; return this; }
            public ApiResponseBuilder<T> statusCode(int v) { this.statusCode = v; return this; }
            public ApiResponse<T> build() { return new ApiResponse<>(success, message, data, statusCode); }
        }

        public static <T> ApiResponse<T> success(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .statusCode(200)
                    .build();
        }

        public static <T> ApiResponse<T> created(String message, T data) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .statusCode(201)
                    .build();
        }

        public static <T> ApiResponse<T> error(String message, int statusCode) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .statusCode(statusCode)
                    .build();
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

    public static class StatsResponse {
        private long totalImages;
        private long encodedCount;
        private long decodedCount;
        private long failedCount;

        public StatsResponse() {}

        public StatsResponse(long totalImages, long encodedCount, long decodedCount, long failedCount) {
            this.totalImages = totalImages;
            this.encodedCount = encodedCount;
            this.decodedCount = decodedCount;
            this.failedCount = failedCount;
        }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private long totalImages;
            private long encodedCount;
            private long decodedCount;
            private long failedCount;

            public Builder totalImages(long v) { this.totalImages = v; return this; }
            public Builder encodedCount(long v) { this.encodedCount = v; return this; }
            public Builder decodedCount(long v) { this.decodedCount = v; return this; }
            public Builder failedCount(long v) { this.failedCount = v; return this; }
            public StatsResponse build() { return new StatsResponse(totalImages, encodedCount, decodedCount, failedCount); }
        }

        public long getTotalImages() { return totalImages; }
        public void setTotalImages(long totalImages) { this.totalImages = totalImages; }
        public long getEncodedCount() { return encodedCount; }
        public void setEncodedCount(long encodedCount) { this.encodedCount = encodedCount; }
        public long getDecodedCount() { return decodedCount; }
        public void setDecodedCount(long decodedCount) { this.decodedCount = decodedCount; }
        public long getFailedCount() { return failedCount; }
        public void setFailedCount(long failedCount) { this.failedCount = failedCount; }
    }
}