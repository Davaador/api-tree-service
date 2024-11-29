package com.api.family.apitreeservice.model.postgres;

import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@Data
@Table(name = "biography", indexes = {@Index(name = "fn_biography_customer", columnList = "customer_id")})
@NoArgsConstructor
@Entity
@SoftDelete
public class Biography {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    @Column(name = "biography", columnDefinition = "TEXT")
    private String detailBiography;

    public Biography(BiographyDto biographyDto) {
        this.detailBiography = biographyDto.getDetailBiography();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
