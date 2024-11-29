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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        return customers.stream().filter(c -> c.getUser().isEnabled()).map(x -> modelMapper.map(x, CoupleDto.class)).toList();
    }

    public CoupleDto addParent(AddParentDto addParentDto) {
        log.info("addParent started: ");
        Customer customer = utilService.findByCustomer();
        Customer parentCustomer = customerRepository.findById(addParentDto.getParentId().longValue()).orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
        customer.setParent(parentCustomer);
        customer.setIsParent(addParentDto.getIsParent());
        customer.setSurName(addParentDto.getSurName());
        customer.setBirthDate(addParentDto.getBirthDate());
        customer.setAge(addParentDto.getAge());
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
                && c.getIsParent() == 0).findFirst();
        if (parent.isPresent()) parentDto = modelMapper.map(parent.get(), ParentDto.class);

        return parentDto;
    }

    public ParentDto findParentsByParentId(List<Integer> id) {
        log.info("findParentsByParentId started for ID: {}", id);
        List<ParentDto> parentList;
        ParentDto parentDto = null;
        List<Customer> fatherCustomers = customerRepository.findByParentAndIsParent(null, 0);
        if (!CollectionUtils.isEmpty(fatherCustomers)) {
            parentDto = modelMapper.map(fatherCustomers.getFirst(), ParentDto.class);
        }

        for (Integer i : id) {
            if (Objects.nonNull(parentDto)) {
                Optional<Customer> firstCustomer = customerRepository.findById(i);
                if (firstCustomer.isPresent()) {
                    List<Customer> childList = customerRepository.findByParentAndIsParent(firstCustomer.get(), 0);
                    if (CollectionUtils.isEmpty(parentDto.getChildren())) {
                        parentList = childList.stream().map(x -> modelMapper.map(x, ParentDto.class)).toList();
                        parentDto.setChildren(parentList);
                    } else {
                        Optional<ParentDto> parentDto1 = parentDto.getChildren().stream().filter(x -> x.getId().equals(i)).findFirst();
                        parentDto1.ifPresent(dto -> dto.setChildren(childList.stream().map(x -> modelMapper.map(x, ParentDto.class)).toList()));
                    }

                }
            }
        }
        return parentDto;
    }
}
