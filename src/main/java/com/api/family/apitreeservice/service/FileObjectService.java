package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.configuration.FileObjectProperties;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.file.FileObjectDto;
import com.api.family.apitreeservice.model.response.FileObject;
import com.api.family.apitreeservice.repository.FileObjectRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Validated
public class FileObjectService {

    private final Path objectStorageLocation;
    private final ModelMapper modelMapper;
    private final FileObjectRepository repository;


    public FileObjectService(FileObjectProperties objectStorageLocation, ModelMapper modelMapper, FileObjectRepository repository) {
        this.modelMapper = modelMapper;
        this.repository = repository;

        this.objectStorageLocation = Paths.get(objectStorageLocation.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.objectStorageLocation);
        } catch (IOException e) {
            throw new CustomException(Errors.NOT_PENDING_USERS);
        }
    }


    public FileObjectDto save(@NotNull MultipartFile file) {
        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        assert fileExtension != null;
        String fileName = UUID.randomUUID().toString().concat(".").concat(fileExtension);

        try {
            Path targetLocation = getFilePath(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            var fileObject = new FileObject(fileName, file.getOriginalFilename(), fileExtension, BigInteger.valueOf(file.getSize()));
            fileObject = repository.save(fileObject);
            return modelMapper.map(fileObject, FileObjectDto.class);
        } catch (IOException ex) {
            throw new CustomException(Errors.NOT_PENDING_USERS);
        }

    }

    private Path getFilePath(String fileName) {
        return this.objectStorageLocation.resolve(fileName);
    }

    public FileObject getDaoById(@NotNull Long id) {
        var fileObjectOptional = repository.findById(id);
        if (fileObjectOptional.isEmpty()) {
            throw new CustomException(Errors.FILE_OBJECT_NOT_FOUND);
        }
        return fileObjectOptional.get();
    }

    public void deleteById(@NotNull Long id) {
        var fileObject = getDaoById(id);
        repository.delete(fileObject);

        try {
            Path filePath = getFilePath(fileObject.getUrl()).normalize();
            Files.delete(filePath);
        } catch (IOException e) {
            throw new CustomException(Errors.FAILED_DELETING_FILE, e);
        }
    }

    public Resource loadFileAsResource(@NotNull String filename) {
        try {
            Path filePath = getFilePath(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException(Errors.FILE_OBJECT_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new CustomException(Errors.FILE_OBJECT_NOT_FOUND, e);
        }
    }

}
