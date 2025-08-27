package com.api.family.apitreeservice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.controller.ImageRepository;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.child.ChildDto;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerCoupleDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerFilter;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Image;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.model.response.Dashboard;
import com.api.family.apitreeservice.repository.CustomerRepository;
import com.api.family.apitreeservice.spec.CustomerSpecs;
import com.api.family.apitreeservice.validator.JwtTokenGenerate;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JwtTokenGenerate jwtTokenGenerate;
    private final ModelMapper modelMapper;
    private final UtilService utilService;
    private final CustomerSpecs customerSpecs;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;

    public Customer create(UserDto userDto, User user) {
        Customer customer = new Customer(userDto, user);
        return customerRepository.save(customer);
    }

    public Customer createChild(ChildDto childDto, User user, Customer parentCustomer) {
        Customer customer = new Customer(childDto, user);
        customer.setParent(parentCustomer);
        return customerRepository.save(customer);
    }

    public boolean findByRegister(String register) {
        return customerRepository.findByRegister(register).isPresent();
    }

    public Image updateImage(MultipartFile file) throws IOException {
        Customer customer = utilService.findByCustomer();
        Map result = cloudinaryService.uploadImage(file, customer.getId());
        // Cloudinary-с ирсэн мэдээлэл
        String publicId = (String) result.get("public_id");
        String url = (String) result.get("secure_url");
        Long imageId = null;
        if (Objects.nonNull(customer.getProfileImage())) {
            imageId = customer.getProfileImage().getId();
        }

        Image image = new Image();
        image.setPublicId(publicId);
        image.setUrl(url);
        var saveImage = imageRepository.save(image);
        customer.setProfileImage(saveImage);
        customerRepository.save(customer);
        if (imageId != null) {
            var deleteImage = imageRepository.findById(imageId);
            if (deleteImage.isPresent()) {
                imageRepository.delete(deleteImage.get());
                cloudinaryService.deleteImage(deleteImage.get().getPublicId());
            }
        }
        return saveImage;
    }

    public CoupleDto updateInfo(@NotNull CustomerDto customerDto) {
        Customer customer = utilService.findByCustomer();
        customer.setSurName(customerDto.getSurName());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setModifiedDate(LocalDateTime.now());
        var saveCustomer = customerRepository.save(customer);
        return modelMapper.map(saveCustomer, CoupleDto.class);
    }

    public void updateProfileData(@NotNull UserDto userDto) {
        User user = jwtTokenGenerate.getUser();
        Customer customer = utilService.findByCustomer();
        if (StringUtils.isEmpty(customer.getEmail()) || !customer.getEmail().equalsIgnoreCase(userDto.getEmail())) {
            utilService.checkIfCustomerInEmail(userDto.getEmail(), Boolean.FALSE,
                    Errors.NOT_CUSTOMER_EMAIL);
            customer.setEmail(userDto.getEmail());
        }
        customer.setBirthDate(userDto.getBirthDate());
        customer.setRegister(userDto.getRegister());
        customer.setModifiedDate(LocalDateTime.now());
        customer.setPhoneNumber(userDto.getPhoneNumber());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setModifiedDate(LocalDateTime.now());
        customerRepository.save(customer);
    }

    public CoupleDto findByCouple() {
        Customer customer = utilService.findByCustomer();
        return modelMapper.map(customer, CoupleDto.class);
    }

    public List<CustomerDto> getCoupleAll() {
        Customer customer = utilService.findByCustomer();
        List<Customer> customers = customerRepository.findByGender(Constants.WOMEN_GENDER
                .equals(customer.getGender()) ? Constants.MEN_GENDER : Constants.WOMEN_GENDER);
        if (CollectionUtils.isEmpty(customers))
            return new ArrayList<>();
        customers = customers.stream().filter(c -> c.getUser().isEnabled()).toList();
        return customers.stream().map(x -> modelMapper.map(x, CustomerDto.class)).toList();
    }

    @Transactional
    public CoupleDto saveCouple(@NotNull CustomerCoupleDto customerParentDto) {
        Customer customer = utilService.findByCustomer();
        Customer findCustomer = customerRepository.findById(customerParentDto.getCoupleId().longValue())
                .orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
        this.checkCouple(customer, findCustomer);
        var savedHusband = customerRepository.save(customer);
        if (customer.getGender().equals("0")) {
            findCustomer.setHusband(savedHusband);
        } else {
            findCustomer.setWife(savedHusband);
        }
        customerRepository.save(findCustomer);
        return modelMapper.map(savedHusband, CoupleDto.class);
    }

    public void checkCouple(Customer customer, Customer findCustomer) {
        if (customer.getGender().equals("0")) {
            Optional<Customer> optionalWife = customerRepository.findByWife(findCustomer);
            if (optionalWife.isPresent())
                throw new CustomException(Errors.ACTIVE_WIFE);
            customer.setWife(findCustomer);
        } else {
            Optional<Customer> optionalHusBand = customerRepository.findByHusband(findCustomer);
            if (optionalHusBand.isPresent())
                throw new CustomException(Errors.ACTIVE_WIFE);
            customer.setHusband(findCustomer);
        }
    }

    public Dashboard dashboard() {
        Dashboard dashboard = new Dashboard();
        List<Customer> customers = customerRepository.findAll();
        dashboard.setTotal(customers.size());
        long active = customers.stream().filter(c -> c.getUser().isEnabled()).count();

        dashboard.setPendingCount((int) (customers.size() - active));
        dashboard.setActiveCount((int) active);
        return dashboard;
    }

    public Page<CoupleDto> findByActiveAllCustomers(@Valid CustomerFilter filter) {
        Specification<Customer> spec = Specification.where(null);
        // if (StringUtils.isNotBlank(filter.getPhoneNumber())) {
        // spec =
        // spec.and(customerSpecs.customerContainsEnabled(filter.getPhoneNumber(), ""));
        // }
        // if(StringUtils.isNotBlank(filter.getLastName())) {
        spec = spec.and(customerSpecs.customerContainsEnabled(filter.getPhoneNumber(), filter.getLastName(),
                filter.getFirstName()));
        // }
        Sort.Direction direction = filter.getIsSortAscending() == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "birthDate");
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        var list = customerRepository.findAll(spec, pageable);
        return list.map(x -> modelMapper.map(x, CoupleDto.class));

    }
}
