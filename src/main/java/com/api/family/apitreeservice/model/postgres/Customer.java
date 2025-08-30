package com.api.family.apitreeservice.model.postgres;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.SoftDelete;
import org.jetbrains.annotations.NotNull;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.dto.child.ChildDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.validator.Functions;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@SoftDelete
@Entity
@Table(name = "customer", indexes = { @Index(name = "fn_customer_register", columnList = "register") })
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
    @Column(unique = true, name = "email", nullable = true)
    private String email;
    private boolean editCustomer = false;
    private LocalDateTime modifiedDate = LocalDateTime.now();
    private Integer isParent;
    private Boolean isDeceased = false;
    private Date deceasedDate;

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
    @OneToOne(cascade = CascadeType.MERGE)
    private Image profileImage;
    private String resetToken;
    @OneToOne(mappedBy = "newCustomer")
    private AdminCustomer adminCustomer;

    public Customer(@NotNull UserDto userDto, User user) {
        this.firstName = userDto.getFirstName();
        this.lastName = userDto.getLastName();
        this.register = userDto.getRegister();
        this.surName = userDto.getSurName();
        this.birthDate = Functions.getBirthday(userDto.getRegister());
        this.age = userDto.getAge();
        this.phoneNumber = userDto.getPhoneNumber();
        this.user = user;
        this.gender = Integer.parseInt(String.valueOf(userDto.getRegister()
                .charAt(userDto.getRegister().length() - 2))) % 2 == 0
                        ? Constants.WOMEN_GENDER
                        : Constants.MEN_GENDER;
        this.isParent = userDto.getIsParent();
    }

    public Customer(@NotNull ChildDto childDto, User user) {
        this.firstName = childDto.getFirstName();
        this.lastName = childDto.getLastName();
        this.register = childDto.getRegister();
        this.surName = childDto.getSurName();
        this.user = user;
        this.gender = Functions.getGender(childDto.getRegister());
        this.age = Functions.getAge(childDto.getRegister());
        this.isParent = 0;
        this.phoneNumber = Constants.CHILD_PHONE;
        this.editCustomer = Boolean.TRUE;
        this.birthDate = Functions.getBirthday(childDto.getRegister());
    }

    public Customer(@NotNull AdminCreateDto adminCreateDto, User user) {
        this.firstName = adminCreateDto.getFirstName();
        this.lastName = adminCreateDto.getLastName();
        this.register = adminCreateDto.getRegister();
        this.surName = adminCreateDto.getSurName();
        this.birthDate = adminCreateDto.getBirthDate();
        this.age = adminCreateDto.getAge();
        this.phoneNumber = adminCreateDto.getPhoneNumber();
        this.user = user;
        this.gender = Integer.parseInt(String.valueOf(adminCreateDto.getRegister()
                .charAt(adminCreateDto.getRegister().length() - 2))) % 2 == 0
                        ? Constants.WOMEN_GENDER
                        : Constants.MEN_GENDER;
        this.isParent = 0;
        this.editCustomer = Boolean.TRUE;
        this.isDeceased = adminCreateDto.getIsDeceased() != null ? adminCreateDto.getIsDeceased() : false;
        this.deceasedDate = adminCreateDto.getDeceasedDate();
    }

}
