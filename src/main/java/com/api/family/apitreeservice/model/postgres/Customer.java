package com.api.family.apitreeservice.model.postgres;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@SoftDelete
@Entity
@Table(name = "customer", indexes = {@Index(name = "fn_customer_register", columnList = "register")})
public class Customer {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String firstName;
    private String lastName;
    @Column(unique = true, name = "register", nullable = false)
    private String register;
    private String surName;
    private Date birthDate;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private String email;
    private boolean editCustomer = false;
    private LocalDateTime modifiedDate = LocalDateTime.now();
    private Integer isParent;

    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToOne
    @JoinColumn(name = "wife_id")
    @JsonManagedReference
    @JsonIgnore
    private Customer wife;

    @OneToOne
    @JoinColumn(name = "husband_id")
    @JsonManagedReference
    @JsonIgnore
    private Customer husband;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @JsonManagedReference
    @JsonIgnore
    private Customer parent;

    public Customer(@NotNull UserDto userDto, User user) {
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
        this.register = userDto.getRegister();
        this.surName = userDto.getSurName();
        this.birthDate = userDto.getBirthDate();
        this.age = userDto.getAge();
        this.phoneNumber = userDto.getPhoneNumber();
        this.user = user;
        this.gender = Integer.parseInt(String.valueOf(userDto.getRegister()
                .charAt(userDto.getRegister().length() - 2))) % 2 == 0
                ? Constants.WOMEN_GENDER : Constants.MEN_GENDER;
    }

}
