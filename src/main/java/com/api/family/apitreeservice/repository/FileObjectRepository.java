package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.response.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {
}
