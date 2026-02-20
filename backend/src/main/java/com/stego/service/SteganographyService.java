package com.stego.service;

import com.stego.dto.StegoDTO;
import com.stego.entity.StegoImage;
import com.stego.exception.ResourceNotFoundException;
import com.stego.exception.SteganographyException;
import com.stego.repository.StegoImageRepository;
import com.stego.util.LSBSteganography;
import com.stego.util.StegoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SteganographyService {

    private final StegoImageRepository repository;
    private final LSBSteganography lsbSteganography;
    private final StegoMapper mapper;

    public SteganographyService(StegoImageRepository repository, LSBSteganography lsbSteganography, StegoMapper mapper) {
        this.repository = repository;
        this.lsbSteganography = lsbSteganography;
        this.mapper = mapper;
    }

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    // ---- ENCODE ----

    public StegoDTO.ImageResponse encodeImage(MultipartFile file, StegoDTO.EncodeRequest request) {
        validateImageFile(file);

        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();
        String ext = getExtension(originalFilename);

        try {
            // Resolve base upload directory to an absolute path inside the application working directory
            Path baseUploadDir = Paths.get(System.getProperty("user.dir")).resolve(uploadDir).toAbsolutePath();

            // Ensure directories exist
            Path uploadPath = baseUploadDir.resolve("originals");
            Path encodedPath = baseUploadDir.resolve("encoded");
            Files.createDirectories(uploadPath);
            Files.createDirectories(encodedPath);

            // Save original using stream copy (more reliable under servlet containers)
            String originalSaveName = uuid + "_original." + ext;
            Path originalFilePath = uploadPath.resolve(originalSaveName);
            try (var in = file.getInputStream()) {
                Files.copy(in, originalFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Encode
            String encodedSaveName = uuid + "_encoded.png";
            Path encodedFilePath = encodedPath.resolve(encodedSaveName);

            lsbSteganography.encodeMessage(
                originalFilePath.toAbsolutePath().toString(),
                encodedFilePath.toAbsolutePath().toString(),
                request.getMessage()
            );

            // Save record
            StegoImage entity = StegoImage.builder()
                    .originalFileName(originalFilename)
                    .encodedFileName(encodedSaveName)
                    .hiddenMessage(request.getMessage())
                    .uploadPath(originalFilePath.toAbsolutePath().toString())
                    .encodedPath(encodedFilePath.toAbsolutePath().toString())
                    .description(request.getDescription())
                    .status("ENCODED")
                    .build();

            entity = repository.save(entity);
            return mapper.toImageResponse(entity);

        } catch (IllegalArgumentException e) {
            throw new SteganographyException(e.getMessage());
        } catch (IOException e) {
            throw new SteganographyException("Failed to process image: " + e.getMessage(), e);
        }
    }

    // ---- DECODE ----

    public StegoDTO.DecodeResponse decodeImage(Long id) {
        StegoImage entity = getEntityById(id);

        try {
            String message = lsbSteganography.decodeMessage(entity.getEncodedPath());

            entity.setStatus("DECODED");
            repository.save(entity);

            return StegoDTO.DecodeResponse.builder()
                    .imageId(id)
                    .decodedMessage(message)
                    .originalFileName(entity.getOriginalFileName())
                    .decodedAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            entity.setStatus("FAILED");
            repository.save(entity);
            throw new SteganographyException("Decoding failed: " + e.getMessage(), e);
        }
    }

    public StegoDTO.DecodeResponse decodeUploadedImage(MultipartFile file) {
        validateImageFile(file);
        String uuid = UUID.randomUUID().toString();

        try {
            Path baseUploadDir = Paths.get(System.getProperty("user.dir")).resolve(uploadDir).toAbsolutePath();
            Path tempPath = baseUploadDir.resolve("temp");
            Files.createDirectories(tempPath);
            String tempFilename = uuid + "_temp.png";
            Path tempFilePath = tempPath.resolve(tempFilename);
            try (var in = file.getInputStream()) {
                Files.copy(in, tempFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            String message = lsbSteganography.decodeMessage(tempFilePath.toAbsolutePath().toString());

            // cleanup temp file
            Files.deleteIfExists(tempFilePath);

            return StegoDTO.DecodeResponse.builder()
                    .decodedMessage(message)
                    .originalFileName(file.getOriginalFilename())
                    .decodedAt(LocalDateTime.now())
                    .build();

        } catch (IOException e) {
            throw new SteganographyException("Decoding failed: " + e.getMessage(), e);
        }
    }

    // ---- CRUD ----

    public Page<StegoDTO.ImageResponse> getAllImages(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toImageResponse);
    }

    public StegoDTO.ImageResponse getImageById(Long id) {
        return mapper.toImageResponse(getEntityById(id));
    }

    public Page<StegoDTO.ImageResponse> getImagesByStatus(String status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(mapper::toImageResponse);
    }

    public Page<StegoDTO.ImageResponse> searchImages(String keyword, Pageable pageable) {
        return repository.searchByKeyword(keyword, pageable).map(mapper::toImageResponse);
    }

    public StegoDTO.ImageResponse updateImage(Long id, StegoDTO.UpdateRequest request) {
        StegoImage entity = getEntityById(id);
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getStatus() != null) entity.setStatus(request.getStatus());
        return mapper.toImageResponse(repository.save(entity));
    }

    public void deleteImage(Long id) {
        StegoImage entity = getEntityById(id);

        // Delete files
        deleteFileIfExists(entity.getUploadPath());
        deleteFileIfExists(entity.getEncodedPath());

        repository.delete(entity);
    }

    public StegoDTO.StatsResponse getStats() {
        return StegoDTO.StatsResponse.builder()
                .totalImages(repository.count())
                .encodedCount(repository.countByStatus("ENCODED"))
                .decodedCount(repository.countByStatus("DECODED"))
                .failedCount(repository.countByStatus("FAILED"))
                .build();
    }

    public List<StegoDTO.ImageResponse> getLatestImages() {
        return repository.findLatest5().stream()
                .limit(5)
                .map(mapper::toImageResponse)
                .collect(Collectors.toList());
    }

    public File getEncodedFile(Long id) {
        StegoImage entity = getEntityById(id);
        File file = new File(entity.getEncodedPath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("Encoded file not found on disk.");
        }
        return file;
    }
    

    public int getMaxMessageCapacity(MultipartFile file) {
        validateImageFile(file);
        String uuid = UUID.randomUUID().toString();
        try {
            Path baseUploadDir = Paths.get(System.getProperty("user.dir")).resolve(uploadDir).toAbsolutePath();
            Path tempPath = baseUploadDir.resolve("temp");
            Files.createDirectories(tempPath);
            Path tempFilePath = tempPath.resolve(uuid + "_cap.png");
            try (var in = file.getInputStream()) {
                Files.copy(in, tempFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            int capacity = lsbSteganography.getMaxMessageLength(tempFilePath.toAbsolutePath().toString());
            Files.deleteIfExists(tempFilePath);
            return capacity;
        } catch (IOException e) {
            throw new SteganographyException("Cannot determine capacity: " + e.getMessage());
        }
    }

    // ---- Helpers ----

    private StegoImage getEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please provide a valid image file.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are supported.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "png";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private void deleteFileIfExists(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.exists()) f.delete();
        }
    }
}
