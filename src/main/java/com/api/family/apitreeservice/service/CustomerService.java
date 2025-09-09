package com.api.family.apitreeservice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.dto.child.BirthOrderDto;
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
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JwtTokenGenerate jwtTokenGenerate;
    private final ModelMapper modelMapper;
    private final UtilService utilService;
    private final CustomerSpecs customerSpecs;
    private final CloudinaryService cloudinaryService;

    // Metrics
    private final Counter customerCreatedCounter;
    private final Counter customerUpdatedCounter;
    private final Counter customerDeletedCounter;
    private final Timer customerServiceTimer;

    public Customer createAdminCustomer(AdminCreateDto adminCreateDto, User user, Customer parentCustomer) {
        Customer customer = new Customer(adminCreateDto, user);
        customer.setParent(parentCustomer);
        return customerRepository.save(customer);
    }

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
        Image images = cloudinaryService.uploadImage(file, customer);
        if (Objects.isNull(customer.getProfileImage())) {
            customer.setProfileImage(images);
            customerRepository.save(customer);
        }
        return images;
    }

    public CoupleDto updateInfo(@NotNull CustomerDto customerDto) {
        Customer customer = utilService.findByCustomer();
        Image oldImage = customer.getProfileImage();
        customer.setSurName(customerDto.getSurName());
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setModifiedDate(LocalDateTime.now());
        if (Objects.isNull(customerDto.getProfileImage()))
            customer.setProfileImage(null);
        var saveCustomer = customerRepository.save(customer);
        if (Objects.nonNull(oldImage) && Objects.isNull(customerDto.getProfileImage()))
            cloudinaryService.deleteImages(oldImage);

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

        // Add birth order information
        int birthOrderInfo = getCurrentUserBirthOrder(customers);
        dashboard.setBirthOrder(birthOrderInfo);
        return dashboard;
    }

    public int getCurrentUserBirthOrder(List<Customer> allCustomers) {
        log.info("Get current user birth order");
        Customer currentCustomer = utilService.findByCustomer();
        // Get all children of the parent
        if (CollectionUtils.isEmpty(allCustomers)) {
            return 0;
        }

        // Sort by birth date to determine birth order
        List<Customer> sortedChildren = allCustomers.stream()
                .sorted(Comparator.comparing(Customer::getBirthDate))
                .toList();

        // Find current user's position in the sorted list
        int birthOrder = 0;
        for (int i = 0; i < sortedChildren.size(); i++) {
            if (sortedChildren.get(i).getId().equals(currentCustomer.getId())) {
                birthOrder = i + 1; // 1st, 2nd, 3rd, etc.
                break;
            }
        }

        log.info("Current user birth order: {} out of {} children", birthOrder, sortedChildren.size());
        return birthOrder;
    }

    public Page<CoupleDto> findByActiveAllCustomers(@Valid CustomerFilter filter) {
        Specification<Customer> spec = customerSpecs.buildCustomerSpecification(
                filter.getPhoneNumber(),
                filter.getLastName(),
                filter.getFirstName(),
                filter.getEmail(),
                filter.getRegister(),
                filter.getGender(),
                filter.getMinAge(),
                filter.getMaxAge(),
                filter.getBirthDateFrom(),
                filter.getBirthDateTo(),
                filter.getIsDeceased(),
                filter.getIsParent());

        Sort.Direction direction = filter.getIsSortAscending() == 1 ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, filter.getSortField());
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
        var list = customerRepository.findAll(spec, pageable);
        return list.map(x -> modelMapper.map(x, CoupleDto.class));
    }

    @Cacheable(value = "customers", key = "#id")
    @Timed("customer.service.findById")
    public Customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
    }

    @CacheEvict(value = "customers", key = "#customer.id")
    @Timed("customer.service.update")
    public Customer updateCustomer(Customer customer) {
        customerUpdatedCounter.increment();
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Integer id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }

    public List<CustomerDto> getAvailableSpouses() {
        List<Customer> customers = customerRepository.findAll();
        if (CollectionUtils.isEmpty(customers))
            return new ArrayList<>();
        // Filter to include enabled users and customers without spouses
        customers = customers.stream()
                .filter(c -> c.getUser().isEnabled())
                .filter(c -> c.getWife() == null && c.getHusband() == null) // Only customers without spouses
                .toList();
        return customers.stream().map(x -> modelMapper.map(x, CustomerDto.class)).toList();
    }

    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }
}
