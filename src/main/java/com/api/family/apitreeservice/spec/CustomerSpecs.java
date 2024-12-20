package com.api.family.apitreeservice.spec;

import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Customer_;
import com.api.family.apitreeservice.model.postgres.User_;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CustomerSpecs {
    public Specification<Customer> customerContainsEnabled(String phoneNumber) {

        return (root, query, builder) -> {
            Predicate enabledPredicate = builder.equal(root.join(Customer_.USER).get(User_.ENABLED), true);

            Predicate phoneNumberPredicate = builder.like(root.get(Customer_.PHONE_NUMBER), String.format("%%%s%%", phoneNumber));

            return builder.and(enabledPredicate, phoneNumberPredicate);
        };
    }
}
