package com.api.family.apitreeservice.model.postgres;

import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@SoftDelete
@Entity
@Table(name = "customer_otp", indexes = { @Index(name = "fn_customer_otp_customer", columnList = "customer_id"),
        @Index(name = "idx_customer_otp_status", columnList = "status"),
        @Index(name = "idx_customer_otp_email", columnList = "email") })
@Builder
@AllArgsConstructor
public class CustomerOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
    private String otpCode;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;
    private String status;
    private Integer count;
}
