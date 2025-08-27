package com.api.family.apitreeservice.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.family.apitreeservice.controller.ImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final ImageRepository imageRepository;

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret, ImageRepository imageRepository) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
        this.imageRepository = imageRepository;
    }

    public Map uploadImage(MultipartFile file, Integer id) throws IOException {
        String folderName = "clients/" + id;
        Map options = ObjectUtils.asMap(
                "folder", folderName);
        return cloudinary.uploader().upload(file.getBytes(), options);
        // return uploadResult.get("secure_url").toString(); // HTTPS зурагны хаяг
    }

    public String deleteImage(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return result.get("result").toString(); // "ok", "not found", гэх мэт
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
