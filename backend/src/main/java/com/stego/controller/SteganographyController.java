package com.stego.controller;

import com.stego.dto.StegoDTO;
import com.stego.service.SteganographyService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class SteganographyController {

    private final SteganographyService service;

    // explicit constructor for dependency injection
    public SteganographyController(SteganographyService service) {
        this.service = service;
    }

    /**
     * Endpoint 1: Encode a message into an image (upload original + message)
     * POST /api/images/encode
     */
    @PostMapping(value = "/images/encode", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.ImageResponse>> encodeImage(
            @RequestPart("file") MultipartFile file,
            @RequestPart("message") String message,
            @RequestPart(value = "description", required = false) String description) {

        StegoDTO.EncodeRequest request = new StegoDTO.EncodeRequest(message, description);
        StegoDTO.ImageResponse response = service.encodeImage(file, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StegoDTO.ApiResponse.created("Image encoded successfully", response));
    }

    /**
     * Endpoint 2: Decode message from a stored encoded image by ID
     * POST /api/images/{id}/decode
     */
    @PostMapping("/images/{id}/decode")
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.DecodeResponse>> decodeById(
            @PathVariable Long id) {
        StegoDTO.DecodeResponse response = service.decodeImage(id);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Message decoded successfully", response));
    }

    /**
     * Endpoint 3: Decode message from an uploaded image directly
     * POST /api/images/decode/upload
     */
    @PostMapping(value = "/images/decode/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.DecodeResponse>> decodeUpload(
            @RequestPart("file") MultipartFile file) {
        StegoDTO.DecodeResponse response = service.decodeUploadedImage(file);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Message decoded from uploaded image", response));
    }

    /**
     * Endpoint 4: Get all images with pagination
     * GET /api/images?page=0&size=10&sort=createdAt,desc
     */
    @GetMapping("/images")
    public ResponseEntity<StegoDTO.ApiResponse<Page<StegoDTO.ImageResponse>>> getAllImages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<StegoDTO.ImageResponse> result = service.getAllImages(pageable);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Images retrieved successfully", result));
    }

    /**
     * Endpoint 5: Get image by ID
     * GET /api/images/{id}
     */
    @GetMapping("/images/{id}")
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.ImageResponse>> getImageById(
            @PathVariable Long id) {
        StegoDTO.ImageResponse response = service.getImageById(id);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Image retrieved", response));
    }

    /**
     * Endpoint 6: Filter images by status
     * GET /api/images/status/{status}?page=0&size=10
     */
    @GetMapping("/images/status/{status}")
    public ResponseEntity<StegoDTO.ApiResponse<Page<StegoDTO.ImageResponse>>> getByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                StegoDTO.ApiResponse.success("Images by status", service.getImagesByStatus(status, pageable)));
    }

    /**
     * Endpoint 7: Search images by keyword
     * GET /api/images/search?keyword=myimage&page=0&size=10
     */
    @GetMapping("/images/search")
    public ResponseEntity<StegoDTO.ApiResponse<Page<StegoDTO.ImageResponse>>> searchImages(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                StegoDTO.ApiResponse.success("Search results", service.searchImages(keyword, pageable)));
    }

    /**
     * Endpoint 8: Update image description/status
     * PUT /api/images/{id}
     */
    @PutMapping("/images/{id}")
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.ImageResponse>> updateImage(
            @PathVariable Long id,
            @RequestBody StegoDTO.UpdateRequest request) {
        StegoDTO.ImageResponse response = service.updateImage(id, request);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Image updated successfully", response));
    }

    /**
     * Endpoint 9: Delete image by ID
     * DELETE /api/images/{id}
     */
    @DeleteMapping("/images/{id}")
    public ResponseEntity<StegoDTO.ApiResponse<Void>> deleteImage(@PathVariable Long id) {
        service.deleteImage(id);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Image deleted successfully", null));
    }

    /**
     * Endpoint 10: Download encoded image
     * GET /api/images/{id}/download
     */
    @GetMapping("/images/{id}/download")
    public ResponseEntity<Resource> downloadEncodedImage(@PathVariable Long id) {
        File file = service.getEncodedFile(id);
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"encoded_" + id + ".png\"")
                .body(resource);
    }

    /**
     * Endpoint 11: Get system statistics
     * GET /api/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<StegoDTO.ApiResponse<StegoDTO.StatsResponse>> getStats() {
        return ResponseEntity.ok(StegoDTO.ApiResponse.success("Statistics", service.getStats()));
    }

    /**
     * Endpoint 12: Get latest 5 images
     * GET /api/images/latest
     */
    @GetMapping("/images/latest")
    public ResponseEntity<StegoDTO.ApiResponse<List<StegoDTO.ImageResponse>>> getLatest() {
        return ResponseEntity.ok(
                StegoDTO.ApiResponse.success("Latest images", service.getLatestImages()));
    }

    /**
     * Endpoint 13: Check image capacity (max message length)
     * POST /api/images/capacity
     */
    @PostMapping(value = "/images/capacity", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StegoDTO.ApiResponse<Integer>> getCapacity(
            @RequestPart("file") MultipartFile file) {
        int capacity = service.getMaxMessageCapacity(file);
        return ResponseEntity.ok(StegoDTO.ApiResponse.success(
                "Maximum characters that can be hidden: " + capacity, capacity));
    }

    /**
     * Endpoint 14: Health check
     * GET /api/health
     */
    @GetMapping("/health")
   public ResponseEntity<StegoDTO.ApiResponse<String>> healthCheck() {
		return ResponseEntity.ok(StegoDTO.ApiResponse.success("API is Running...", "OK"));
	}
}