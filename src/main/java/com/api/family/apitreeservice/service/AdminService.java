package com.api.family.apitreeservice.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.postgres.AdminCustomer;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Role;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.repository.AdminCustomerRepository;
import java.util.List;
import java.util.stream.Collectors;
import com.api.family.apitreeservice.model.dto.customer.CustomerSummaryDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final UtilService utilService;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final AdminCustomerRepository adminCustomerRepository;

    public AdminCreateDto createCustomer(@Valid AdminCreateDto adminCreateDto) {
        log.info("Creating admin customer");
        Customer customer = utilService.findByCustomer();
        utilService.checkAdmin();
        userService.checkIfDuplicate(adminCreateDto.getPhoneNumber(), adminCreateDto.getRegister());
        userService.matchPasswords(adminCreateDto.getPassword(), adminCreateDto.getConfirmPassword());
        Customer lastCustomer = null;
        if (adminCreateDto.getLastNameId() != null) {
            lastCustomer = userService.getByCustomer(adminCreateDto.getLastNameId());
            adminCreateDto.setParent(lastCustomer);
        }
        var role = new Role();
        log.info("customer age: {}", adminCreateDto.getAge());
        if (adminCreateDto.getAge() < 18) {
            role = roleService.getRoleByName(RoleEnumString.CHILD.getValue());
        } else {
            role = roleService.getRoleByName(userService.checkRoleUser(adminCreateDto.getPhoneNumber()).getValue());
        }
        var user = new User(passwordEncoder.encode(adminCreateDto.getPassword()), adminCreateDto.getPhoneNumber(),
                Boolean.TRUE);
        user.addRole(role);
        Customer savedCustomer = userService.createAdminCustomer(adminCreateDto, user, lastCustomer);
        var mappedAdminCustomerDto = modelMapper.map(savedCustomer, AdminCreateDto.class);
        mappedAdminCustomerDto.setId(savedCustomer.getId());
        this.createAdminCustomer(customer.getId(), savedCustomer);
        log.info("finished create admin Customer");
        return mappedAdminCustomerDto;

    }

    public void createAdminCustomer(Integer adminId, Customer customer) {
        AdminCustomer adminCustomer = new AdminCustomer();
        adminCustomer.setAdmin_id(adminId);
        adminCustomer.setNewCustomer(customer);
        adminCustomerRepository.save(adminCustomer);

    }

    public List<CustomerSummaryDto> listRegisteredCustomersByCurrentAdmin() {
        Customer current = utilService.findByCustomer();
        utilService.checkAdmin();
        return adminCustomerRepository.findCustomersByAdminId(current.getId())
                .stream()
                .map(c -> {
                    CustomerSummaryDto dto = new CustomerSummaryDto();
                    dto.setId(c.getId());
                    dto.setFirstName(c.getFirstName());
                    dto.setLastName(c.getLastName());
                    dto.setPhoneNumber(c.getPhoneNumber());
                    dto.setSurName(c.getSurName());
                    dto.setEmail(c.getEmail());
                    dto.setAge(c.getAge());
                    dto.setRegister(c.getRegister());
                    dto.setBirthDate(c.getBirthDate());
                    dto.setIsDeceased(c.getIsDeceased());
                    dto.setDeceasedDate(c.getDeceasedDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public CustomerSummaryDto getCustomerById(Integer id) {
        log.info("Getting customer by id: {}", id);
        utilService.checkAdmin();

        Customer current = utilService.findByCustomer();
        Customer customer = userService.getByCustomer(id);

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Check if the customer belongs to the current admin
        boolean belongsToAdmin = adminCustomerRepository.findCustomersByAdminId(current.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(id));

        if (!belongsToAdmin) {
            throw new RuntimeException("Customer not found or access denied");
        }

        CustomerSummaryDto dto = new CustomerSummaryDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setSurName(customer.getSurName());
        dto.setEmail(customer.getEmail());
        dto.setAge(customer.getAge());
        dto.setRegister(customer.getRegister());
        dto.setBirthDate(customer.getBirthDate());
        dto.setIsDeceased(customer.getIsDeceased());
        dto.setDeceasedDate(customer.getDeceasedDate());
        dto.setIsParent(customer.getIsParent());
        dto.setLastNameId(customer.getParent() != null ? customer.getParent().getId() : null);

        log.info("Successfully retrieved customer with id: {}", id);
        return dto;
    }

    public AdminCreateDto updateCustomer(Integer id, AdminCreateDto adminCreateDto) {
        log.info("Updating admin customer with id: {}", id);
        utilService.checkAdmin();

        // Check if customer exists and belongs to current admin
        Customer customerToUpdate = userService.getByCustomer(id);
        if (customerToUpdate == null) {
            throw new RuntimeException("Customer not found");
        }

        // Update customer fields
        customerToUpdate.setFirstName(adminCreateDto.getFirstName());
        customerToUpdate.setLastName(adminCreateDto.getLastName());
        customerToUpdate.setSurName(adminCreateDto.getSurName());
        customerToUpdate.setPhoneNumber(adminCreateDto.getPhoneNumber());
        customerToUpdate.setEmail(adminCreateDto.getEmail());
        customerToUpdate.setRegister(adminCreateDto.getRegister());
        customerToUpdate.setBirthDate(adminCreateDto.getBirthDate());
        customerToUpdate.setAge(adminCreateDto.getAge());
        customerToUpdate.setIsDeceased(adminCreateDto.getIsDeceased() != null ? adminCreateDto.getIsDeceased() : false);
        customerToUpdate.setDeceasedDate(adminCreateDto.getDeceasedDate());

        // Update isParent field
        if (adminCreateDto.getIsParent() != null) {
            customerToUpdate.setIsParent(adminCreateDto.getIsParent());
        } else {
            customerToUpdate.setIsParent(null);
        }

        // Update parent relationship
        if (adminCreateDto.getLastNameId() != null) {
            Customer parentCustomer = userService.getByCustomer(adminCreateDto.getLastNameId());
            if (parentCustomer != null) {
                customerToUpdate.setParent(parentCustomer);
            } else {
                log.warn("Parent customer with id {} not found, setting parent to null",
                        adminCreateDto.getLastNameId());
                customerToUpdate.setParent(null);
            }
        } else {
            // If lastNameId is null, clear the parent relationship
            customerToUpdate.setParent(null);
        }

        Customer savedCustomer = userService.updateCustomer(customerToUpdate);
        var mappedAdminCustomerDto = modelMapper.map(savedCustomer, AdminCreateDto.class);
        mappedAdminCustomerDto.setId(savedCustomer.getId());

        log.info("Finished updating admin customer");
        return mappedAdminCustomerDto;
    }

    public void deleteCustomer(Integer id) {
        log.info("Deleting admin customer with id: {}", id);
        utilService.checkAdmin();

        // Check if customer exists and belongs to current admin
        Customer customerToDelete = userService.getByCustomer(id);
        if (customerToDelete == null) {
            throw new RuntimeException("Customer not found");
        }

        userService.deleteCustomer(id);
        log.info("Finished deleting admin customer");
    }

}
