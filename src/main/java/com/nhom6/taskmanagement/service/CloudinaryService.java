package com.nhom6.taskmanagement.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nhom6.taskmanagement.config.AppConfig;
import com.nhom6.taskmanagement.exception.CustomException;

public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", AppConfig.getProperty("cloudinary.cloud_name"),
                "api_key", AppConfig.getProperty("cloudinary.api_key"),
                "api_secret", AppConfig.getProperty("cloudinary.api_secret")));
    }

    public String uploadFile(File file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new CustomException("Không thể tải tệp lên Cloudinary", e);
        }
    }

    public void deleteFile(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new CustomException("Không thể xóa tệp từ Cloudinary", e);
        }
    }
}