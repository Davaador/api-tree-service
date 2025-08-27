package com.api.family.apitreeservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.api.family.apitreeservice.configuration.FileObjectProperties;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;

@Service
@Validated
public class FileObjectService {

    private final Path objectStorageLocation;

    public FileObjectService(FileObjectProperties objectStorageLocation) {

        this.objectStorageLocation = Paths.get(objectStorageLocation.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.objectStorageLocation);
        } catch (IOException e) {
            throw new CustomException(Errors.NOT_PENDING_USERS);
        }
    }

}
