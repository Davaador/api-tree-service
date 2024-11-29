package com.api.family.apitreeservice.model.postgres;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "family_role")
@Setter(AccessLevel.NONE)
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private List<User> users;

    public Role(String name) {
        this.name = name;
    }
}
