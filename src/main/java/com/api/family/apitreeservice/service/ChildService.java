package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.child.ChildDto;
import com.api.family.apitreeservice.model.dto.customer.ParentDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.repository.CustomerRepository;
import com.api.family.apitreeservice.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Log4j2
public class ChildService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerService customerService;
    private final UtilService utilService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserDto createChild(@NotNull ChildDto childDto) {
        log.info("Creating child");
        Customer customer = utilService.findByCustomer();
        this.checkIfDuplicateRegister(childDto.getRegister());
        if (customer.getGender().equals(Constants.WOMEN_GENDER)) {
            if (Objects.isNull(customer.getHusband()))
                throw new CustomException(Errors.NOT_HUSBAND);
            customer = customer.getHusband();
        }

        var role = roleService.getRoleByName(RoleEnumString.CHILD.getValue());
        var user = new User(passwordEncoder.encode(Constants.CHILD_PASSWORD), Constants.CHILD_PHONE, Boolean.TRUE);
        user.addRole(role);
        Customer newCustomer = customerService.createChild(childDto, user, customer);
        var userDto = modelMapper.map(newCustomer.getUser(), UserDto.class);
        userDto.setId(customer.getUser().getId());
        return userDto;
    }

    public void checkIfDuplicateRegister(String register) {
        if (StringUtils.isNotBlank(register) && customerService.findByRegister(register)) {
            throw new CustomException(Errors.DUPLICATED_REGISTER);
        }
    }

    public List<ParentDto> getChildInfos() {
        log.info("Get child infos");
        Customer customer = utilService.findByCustomer();
        if (customer.getGender().equals(Constants.WOMEN_GENDER)) {
            if (Objects.isNull(customer.getHusband()))
                throw new CustomException(Errors.NOT_HUSBAND);
            customer = customer.getHusband();
        }

        List<Customer> childLists = customerRepository.findByParentAndIsParent(customer, 0);
        if (CollectionUtils.isEmpty(childLists))
            return new ArrayList<>();
        log.info("Get info child lists finished");

        return childLists.stream().map(child -> modelMapper.map(child, ParentDto.class))
                .sorted(Comparator.comparing(ParentDto::getBirthDate)).toList();

    }
}
