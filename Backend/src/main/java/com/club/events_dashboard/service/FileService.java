package com.club.events_dashboard.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String uploadFile(MultipartFile file) throws Exception;
    void deleteFile(String fileUrl) throws Exception;
}
