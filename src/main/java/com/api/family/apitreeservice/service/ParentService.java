package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.ParentDto;
import com.api.family.apitreeservice.model.dto.user.AddParentDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.repository.CustomerRepository;
import com.api.family.apitreeservice.validator.Functions;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class ParentService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final UtilService utilService;

    public List<CoupleDto> findAllParents() {
        log.info("findAllParents started: ");
        List<Customer> customers = customerRepository.findByAgeGreaterThanEqual(Constants.GREATER_AGE);
        return customers.stream().filter(c -> c.getUser().isEnabled()).map(x -> modelMapper.map(x, CoupleDto.class))
                .toList();
    }

    public CoupleDto addParent(@NotNull AddParentDto addParentDto) {
        log.info("addParent started: ");
        Customer customer = utilService.findByCustomer();

        // Handle parent relationship - either set or clear based on parentId
        if (addParentDto.getParentId() != null && addParentDto.getParentId() != 0) {
            Customer parentCustomer = customerRepository.findById(addParentDto.getParentId().longValue())
                    .orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
            customer.setParent(parentCustomer);
            customer.setIsParent(addParentDto.getIsParent());
        } else {
            // Clear parent relationship when parentId is null or 0
            customer.setParent(null);
            customer.setIsParent(null);
        }

        customer.setSurName(addParentDto.getSurName());
        customer.setBirthDate(addParentDto.getBirthDate());
        customer.setAge(addParentDto.getAge() != null ? addParentDto.getAge()
                : Functions.calculateAgeFromBirthDate(
                        addParentDto.getBirthDate()));
        if (StringUtils.isNotEmpty(addParentDto.getEmail())
                && !addParentDto.getEmail().equalsIgnoreCase(customer.getEmail())) {
            utilService.checkIfCustomerInEmail(addParentDto.getEmail(), Boolean.FALSE, Errors.NOT_CUSTOMER_EMAIL);
            customer.setEmail(addParentDto.getEmail());
        }
        customer.setModifiedDate(LocalDateTime.now());
        customer.setEditCustomer(Boolean.TRUE);
        customer = customerRepository.save(customer);
        return modelMapper.map(customer, CoupleDto.class);
    }

    public ParentDto findParents() {
        log.info("getParents started: ");
        ParentDto parentDto = null;
        List<Customer> customers = customerRepository.findByParent(null);
        Optional<Customer> parent = customers.stream().filter(c -> c.getUser().isEnabled()
                && c.getIsParent() != null && c.getIsParent() == 0).findFirst();
        if (parent.isPresent()) {
            parentDto = modelMapper.map(parent.get(), ParentDto.class);
            // Populate spouse data to avoid recursive references
            Customer parentCustomer = parent.get();
            if (parentCustomer.getWife() != null) {
                // Create a simple spouse DTO without recursive references
                ParentDto wifeDto = new ParentDto();
                wifeDto.setId(parentCustomer.getWife().getId());
                wifeDto.setFirstName(parentCustomer.getWife().getFirstName());
                wifeDto.setLastName(parentCustomer.getWife().getLastName());
                wifeDto.setGender(parentCustomer.getWife().getGender());
                wifeDto.setBirthDate(parentCustomer.getWife().getBirthDate());
                parentDto.setWife(wifeDto);
            }
            if (parentCustomer.getHusband() != null) {
                // Create a simple spouse DTO without recursive references
                ParentDto husbandDto = new ParentDto();
                husbandDto.setId(parentCustomer.getHusband().getId());
                husbandDto.setFirstName(parentCustomer.getHusband().getFirstName());
                husbandDto.setLastName(parentCustomer.getHusband().getLastName());
                husbandDto.setGender(parentCustomer.getHusband().getGender());
                husbandDto.setBirthDate(parentCustomer.getHusband().getBirthDate());
                parentDto.setHusband(husbandDto);
            }
        }

        return parentDto;
    }

    public ParentDto findParentsByParentId(List<Integer> id) {
        log.info("findParentsByParentId started for ID: {}", id);
        List<Customer> fatherCustomers = customerRepository.findByParentAndIsParent(null, 0);
        if (fatherCustomers.isEmpty()) {
            return null;
        }

        ParentDto parentDto = modelMapper.map(fatherCustomers.getFirst(), ParentDto.class);

        // Populate spouse data to avoid recursive references
        Customer father = fatherCustomers.getFirst();
        if (father.getWife() != null) {
            // Create a simple spouse DTO without recursive references
            ParentDto wifeDto = new ParentDto();
            wifeDto.setId(father.getWife().getId());
            wifeDto.setFirstName(father.getWife().getFirstName());
            wifeDto.setLastName(father.getWife().getLastName());
            wifeDto.setGender(father.getWife().getGender());
            wifeDto.setBirthDate(father.getWife().getBirthDate());
            parentDto.setWife(wifeDto);
        }
        if (father.getHusband() != null) {
            // Create a simple spouse DTO without recursive references
            ParentDto husbandDto = new ParentDto();
            husbandDto.setId(father.getHusband().getId());
            husbandDto.setFirstName(father.getHusband().getFirstName());
            husbandDto.setLastName(father.getHusband().getLastName());
            husbandDto.setGender(father.getHusband().getGender());
            husbandDto.setBirthDate(father.getHusband().getBirthDate());
            parentDto.setHusband(husbandDto);
        }

        List<ParentDto> lastParentList;
        Map<Integer, List<ParentDto>> maps = new HashMap<>();
        for (Integer i : id) {
            Optional<Customer> firstCustomer = customerRepository.findById(i);
            if (firstCustomer.isPresent()) {
                List<Customer> childList = customerRepository.findByParentAndIsParent(firstCustomer.get(), 0);
                List<ParentDto> childDtos = childList.stream().map(x -> {
                    ParentDto dto = modelMapper.map(x, ParentDto.class);
                    // Populate spouse data to avoid recursive references
                    if (x.getWife() != null) {
                        // Create a simple spouse DTO without recursive references
                        ParentDto wifeDto = new ParentDto();
                        wifeDto.setId(x.getWife().getId());
                        wifeDto.setFirstName(x.getWife().getFirstName());
                        wifeDto.setLastName(x.getWife().getLastName());
                        wifeDto.setGender(x.getWife().getGender());
                        wifeDto.setBirthDate(x.getWife().getBirthDate());
                        dto.setWife(wifeDto);
                    }
                    if (x.getHusband() != null) {
                        // Create a simple spouse DTO without recursive references
                        ParentDto husbandDto = new ParentDto();
                        husbandDto.setId(x.getHusband().getId());
                        husbandDto.setFirstName(x.getHusband().getFirstName());
                        husbandDto.setLastName(x.getHusband().getLastName());
                        husbandDto.setGender(x.getHusband().getGender());
                        husbandDto.setBirthDate(x.getHusband().getBirthDate());
                        dto.setHusband(husbandDto);
                    }
                    return dto;
                }).toList();
                maps.put(i, childDtos);
            }
        }

        if (!maps.isEmpty()) {
            for (int i = id.size() - 1; i >= 0; i--) {
                if (id.size() - 1 == i) {
                    lastParentList = maps.get(id.get(i));
                } else {
                    int finalI = i + 1;
                    lastParentList = maps.get(id.get(i)).stream().map(x -> {
                        if (x.getId().equals(id.get(finalI))) {
                            x.setChildren(maps.get(id.get(finalI)));
                        }
                        return x;
                    }).toList();
                }
                if (fatherCustomers.getFirst().getId().equals(id.get(i))) {
                    parentDto.setChildren(lastParentList);
                }
            }
        }
        return parentDto;
    }
}
