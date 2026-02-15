package com.club.events_dashboard.service;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.club.events_dashboard.dto.CloudinaryUploadResultDTO;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public CloudinaryUploadResultDTO uploadFile(MultipartFile file, String folder) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        Map<String, Object> uploadResult =
                cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "resource_type", "auto"
                        )
                );

        return new CloudinaryUploadResultDTO(
                (String) uploadResult.get("secure_url"),
                (String) uploadResult.get("public_id")
        );
    }

    public void deleteFile(String publicId) throws Exception {
        if (publicId == null || publicId.isBlank()) return;
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
