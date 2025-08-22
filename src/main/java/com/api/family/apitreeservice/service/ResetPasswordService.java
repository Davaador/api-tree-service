package com.api.family.apitreeservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.constants.OtpStatusEnumString;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.user.ResetPasswordDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.CustomerOtp;
import com.api.family.apitreeservice.repository.CustomerOtpRepository;
import com.api.family.apitreeservice.repository.CustomerRepository;
import com.api.family.apitreeservice.validator.Functions;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Validated
@Log4j2
public class ResetPasswordService {

    private final CustomerRepository customerRepository;
    private final CustomerOtpRepository customerOtpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UtilService utilService;

    public void sendToOtp(@Valid ResetPasswordDto resetPasswordDto) {
        Customer customer = utilService.checkIfCustomerInEmail(resetPasswordDto.getEmail(), Boolean.TRUE,
                Errors.NOT_EMAIL);
        if (Objects.isNull(customer))
            throw new CustomException(Errors.NOT_EMAIL);
        List<CustomerOtp> customerOtps = customerOtpRepository.findByCustomerAndStatus(customer,
                OtpStatusEnumString.NEW.getValue());
        if (!CollectionUtils.isEmpty(customerOtps)) {
            customerOtps.forEach(c -> {
                c.setStatus(OtpStatusEnumString.REJECT.getValue());
                customerOtpRepository.save(c);
            });
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(15);
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        CustomerOtp customerOtp = CustomerOtp.builder().email(resetPasswordDto.getEmail()).count(0)
                .createdDate(now).expiryDate(expiryTime).customer(customer).status(OtpStatusEnumString.NEW.getValue())
                .otpCode(passwordEncoder.encode(String.valueOf(otp))).build();
        customerOtpRepository.save(customerOtp);
        log.info("customer otp: {}", String.valueOf(otp));
        emailService.sendSimpleEmail(resetPasswordDto.getEmail(),
                Constants.EMAIL_SUBJECT,
                Constants.EMAIL_BODY.concat(" ").concat(
                        String.valueOf(otp)));

    }

    public String checkCustomerOtp(@Valid ResetPasswordDto resetPasswordDto) {
        Optional<CustomerOtp> opCustomerOtp = customerOtpRepository.findByEmailAndStatus(resetPasswordDto.getEmail(),
                OtpStatusEnumString.NEW.getValue());
        if (opCustomerOtp.isPresent()) {
            CustomerOtp customerOtp = opCustomerOtp.get();
            LocalDateTime now = LocalDateTime.now();
            this.checkCustomerOtpCount(customerOtp);
            if (customerOtp.getExpiryDate().isAfter(now)) {
                Boolean otp = passwordEncoder.matches(resetPasswordDto.getOtp(), customerOtp.getOtpCode());
                if (otp.equals(Boolean.TRUE)) {
                    customerOtp.setStatus(OtpStatusEnumString.SUCCESS.getValue());
                    customerOtpRepository.save(customerOtp);
                    Customer customer = utilService.checkIfCustomerInEmail(resetPasswordDto.getEmail(), Boolean.TRUE,
                            Errors.NOT_EMAIL);
                    String token = UUID.randomUUID().toString();
                    customer.setResetToken(token);
                    customerRepository.save(customer);
                    return token;
                } else {
                    customerOtp.setCount(customerOtp.getCount() + 1);
                    customerOtpRepository.save(customerOtp);
                    throw new CustomException(Errors.NOT_CUSTOMER_OTP_EQUALS);
                }
            } else {
                customerOtp.setStatus(OtpStatusEnumString.EXPIRY.getValue());
                customerOtpRepository.save(customerOtp);
                throw new CustomException(Errors.NOT_CUSTOMER_OTP_EXPIRY);

            }

        } else
            throw new CustomException(Errors.NOT_CUSTOMER_OTP);
    }

    public void resetPassword(@Valid ResetPasswordDto resetPasswordDto) {
        Customer customer = utilService.checkIfCustomerInEmail(resetPasswordDto.getEmail(), Boolean.TRUE,
                Errors.NOT_EMAIL);
        if (Objects.isNull(customer))
            throw new CustomException(Errors.NOT_EMAIL);
        if (resetPasswordDto.getResetToken().equals(customer.getResetToken())) {
            Functions.matchPasswords(resetPasswordDto.getPassword(), resetPasswordDto.getConfirmPassword());
            customer.getUser().setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
            customer.setResetToken(null);
            customerRepository.save(customer);
        } else
            throw new CustomException(Errors.NOT_CUSTOMER_RESET_TOKEN);

    }

    public void checkCustomerOtpCount(CustomerOtp customerOtp) {
        if (customerOtp.getCount() >= 3) {
            customerOtp.setStatus(OtpStatusEnumString.BLOCKED.getValue());
            customerOtpRepository.save(customerOtp);
            throw new CustomException(Errors.NOT_CUSTOMER_OTP_COUNT);
        }
    }
}
