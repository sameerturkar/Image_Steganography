package com.stego.util;

import com.stego.dto.StegoDTO;
import com.stego.entity.StegoImage;
import org.springframework.stereotype.Component;

@Component
public class StegoMapper {

    public StegoDTO.ImageResponse toImageResponse(StegoImage entity, String baseUrl) {
        return StegoDTO.ImageResponse.builder()
                .id(entity.getId())
                .originalFileName(entity.getOriginalFileName())
                .encodedFileName(entity.getEncodedFileName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .hiddenMessage(entity.getHiddenMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .encodedImageUrl(baseUrl + "/api/images/" + entity.getId() + "/download")
                .build();
    }

    public StegoDTO.ImageResponse toImageResponse(StegoImage entity) {
        return toImageResponse(entity, "https://image-steganography-o6mn.onrender.com");
    }
}
