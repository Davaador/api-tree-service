package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.ParentDto;
import com.api.family.apitreeservice.model.dto.user.AddParentDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
        Customer parentCustomer = customerRepository.findById(addParentDto.getParentId().longValue())
                .orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
        customer.setParent(parentCustomer);
        customer.setIsParent(addParentDto.getIsParent());
        customer.setSurName(addParentDto.getSurName());
        customer.setBirthDate(addParentDto.getBirthDate());
        customer.setAge(addParentDto.getAge());
        utilService.checkIfCustomerInEmail(addParentDto.getEmail(), Boolean.FALSE, Errors.NOT_CUSTOMER_EMAIL);
        customer.setEmail(addParentDto.getEmail());
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
        if (parent.isPresent())
            parentDto = modelMapper.map(parent.get(), ParentDto.class);

        return parentDto;
    }

    public ParentDto findParentsByParentId(List<Integer> id) {
        log.info("findParentsByParentId started for ID: {}", id);
        List<Customer> fatherCustomers = customerRepository.findByParentAndIsParent(null, 0);
        ParentDto parentDto = modelMapper.map(fatherCustomers.getFirst(), ParentDto.class);
        List<ParentDto> lastParentList;
        Map<Integer, List<ParentDto>> maps = new HashMap<>();
        for (Integer i : id) {
            Optional<Customer> firstCustomer = customerRepository.findById(i);
            if (firstCustomer.isPresent()) {
                List<Customer> childList = customerRepository.findByParentAndIsParent(firstCustomer.get(), 0);
                maps.put(i, childList.stream().map(x -> modelMapper.map(x, ParentDto.class)).toList());
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
