package com.api.family.apitreeservice.spec;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Customer_;
import com.api.family.apitreeservice.model.postgres.User_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CustomerSpecs {
    public Specification<Customer> customerContainsEnabled(String phoneNumber, String lastName, String firstName) {

        return (root, query, builder) -> {
            Predicate enabledPredicate = builder.equal(root.join(Customer_.USER).get(User_.ENABLED), true);

            Predicate phoneNumberPredicate = StringUtils.isNotBlank(phoneNumber) ?
                    builder.like(root.get(Customer_.PHONE_NUMBER), String.format(Constants.FILTER_FORMAT, phoneNumber)) :
                    builder.conjunction();

            Predicate lastNamePredicate = StringUtils.isNotBlank(lastName) ?
                    builder.like(root.get(Customer_.LAST_NAME), String.format(Constants.FILTER_FORMAT, lastName)) :
                    builder.conjunction();

            Predicate firstNamePredicate = StringUtils.isNotBlank(firstName) ?
                    builder.like(root.get(Customer_.FIRST_NAME), String.format(Constants.FILTER_FORMAT, firstName)) :
                    builder.conjunction();

            return builder.and(enabledPredicate, phoneNumberPredicate, lastNamePredicate, firstNamePredicate);
        };
    }
}
