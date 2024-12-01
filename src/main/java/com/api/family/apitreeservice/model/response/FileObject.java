package com.api.family.apitreeservice.model.response;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@Entity
@NoArgsConstructor
public class FileObject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private String extension;
    private BigInteger size;

    public FileObject(String url, String name, String extension, BigInteger size) {
        this.url = url;
        this.name = name;
        this.extension = extension;
        this.size = size;
    }
}
