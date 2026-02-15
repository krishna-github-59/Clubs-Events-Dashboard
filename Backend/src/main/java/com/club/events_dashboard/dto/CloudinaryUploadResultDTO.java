package com.club.events_dashboard.dto;

public class CloudinaryUploadResultDTO {

    private String url;
    private String publicId;

    public CloudinaryUploadResultDTO(String url, String publicId) {
        this.url = url;
        this.publicId = publicId;
    }

    public String getUrl() {
        return url;
    }

    public String getPublicId() {
        return publicId;
    }
}
