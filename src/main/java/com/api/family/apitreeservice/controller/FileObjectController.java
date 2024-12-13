package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.file.FileObjectDto;
import com.api.family.apitreeservice.service.FileObjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/file")
public class FileObjectController {

    private final FileObjectService fileObjectService;

    @PostMapping
    public FileObjectDto uploadFile(@RequestParam("file") MultipartFile file) {
        return fileObjectService.save(file);
    }

    @GetMapping("/resource/{fileName:.+}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileObjectService.loadFileAsResource(fileName);
        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new CustomException(Errors.UNKNOWN_FILE_TYPE, e);
        }

        try {
            byte[] file = resource.getContentAsByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(file);
        } catch (IOException e) {
            throw new CustomException(Errors.FAILED_CONVERTING_FILE_TO_BYTE);
        }
    }
}
