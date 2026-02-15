package com.club.events_dashboard.service.implementation;

import com.cloudinary.Cloudinary;
import com.club.events_dashboard.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileServiceImpl implements FileService {

    private final Cloudinary cloudinary;

    public FileServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    private final String UPLOAD_DIR = "uploads/";

    @Override
    public String uploadFile(MultipartFile file) throws IOException {

        // 1. Create uploads folder if not exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generate a unique file name
        String fileName = java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // 3. Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 4. Return publicly accessible URL
        return "/uploads/" + fileName;
    }

    @Override
    public void deleteFile(String fileUrl) throws Exception {
        if (fileUrl == null || fileUrl.trim().isEmpty()) return;

        // if fileUrl is like "/uploads/uuid_filename.ext" or "uploads/..."
        String normalized = fileUrl;
        if (normalized.startsWith("/")) normalized = normalized.substring(1);
        Path filePath = Paths.get(normalized);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new NoSuchFileException(filePath.toString());
        }
    }
}
