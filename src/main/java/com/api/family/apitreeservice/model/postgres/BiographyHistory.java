package com.api.family.apitreeservice.model.postgres;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

@Data
@Table(name = "biography_history", indexes = {
        @Index(name = "fn_biography_history_biography", columnList = "biography_id"),
        @Index(name = "fn_biography_history_created", columnList = "created_at")
})
@NoArgsConstructor
@Entity
@SoftDelete
public class BiographyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "biography_content", columnDefinition = "TEXT")
    private String biographyContent;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "change_description")
    private String changeDescription;

    private LocalDateTime createdAt;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "biography_id", referencedColumnName = "id")
    private Biography biography;

    public BiographyHistory(Biography biography, String changeDescription) {
        this.biography = biography;
        this.biographyContent = biography.getDetailBiography();
        this.changeDescription = changeDescription;
        this.createdAt = LocalDateTime.now();
    }
}
