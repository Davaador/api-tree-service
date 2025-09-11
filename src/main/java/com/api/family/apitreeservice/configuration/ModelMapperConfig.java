package com.api.family.apitreeservice.configuration;

import com.api.family.apitreeservice.model.dto.customer.CustomerBasicDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Customer to CustomerBasicDto mapping
        mapper.addMappings(new PropertyMap<Customer, CustomerBasicDto>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setFirstName(source.getFirstName());
                map().setLastName(source.getLastName());
                map().setRegister(source.getRegister());
                map().setSurName(source.getSurName());
                map().setBirthDate(source.getBirthDate());
                map().setAge(source.getAge());
                map().setGender(source.getGender());
                map().setPhoneNumber(source.getPhoneNumber());
                map().setEmail(source.getEmail());
                map().setEditCustomer(source.isEditCustomer());
                map().setModifiedDate(source.getModifiedDate());
                // User mapping will be handled by ModelMapper automatically
                map().setProfileImage(source.getProfileImage());
                map().setIsDeceased(source.getIsDeceased());
                map().setDeceasedDate(source.getDeceasedDate());
                map().setIsParent(source.getIsParent());
            }
        });

        return mapper;
    }
}
