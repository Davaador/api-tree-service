package com.api.family.apitreeservice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.api.family.apitreeservice.controller.ImageRepository;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Image;
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
    }

    public Image uploadImage(MultipartFile file, Customer customer) throws IOException {
        Map result = this.uploadImage(file, customer.getId());
        String publicId = (String) result.get("public_id");
        String url = (String) result.get("secure_url");
        Image newImage = new Image();
        if (Objects.nonNull(customer.getProfileImage())) {
            var customerImage = imageRepository.findById(customer.getProfileImage().getId());
            if (customerImage.isPresent()) {
                newImage = customerImage.get();
                this.deleteImage(newImage.getPublicId());
                newImage.setPublicId(publicId);
                newImage.setUrl(url);
                newImage.setUploadedAt(LocalDateTime.now());
                return imageRepository.save(newImage);
            }
        }
        newImage.setPublicId(publicId);
        newImage.setUrl(url);
        return imageRepository.save(newImage);
    }

    public void deleteImages(Image image) {
        var customerImage = imageRepository.findById(image.getId());
        if (customerImage.isPresent()) {
            this.deleteImage(customerImage.get().getPublicId());
            imageRepository.delete(customerImage.get());
        }
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
