package com.api.family.apitreeservice.spec;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.model.postgres.Customer;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CustomerSpecs {
    public Specification<Customer> customerContainsEnabled(String phoneNumber, String lastName, String firstName) {

        return (root, query, builder) -> {
            Predicate enabledPredicate = builder.equal(root.join("user").get("enabled"), true);

            Predicate phoneNumberPredicate = StringUtils.isNotBlank(phoneNumber)
                    ? builder.like(root.get("phoneNumber"),
                            String.format(Constants.FILTER_FORMAT, phoneNumber))
                    : builder.conjunction();

            Predicate lastNamePredicate = StringUtils.isNotBlank(lastName)
                    ? builder.like(root.get("lastName"),
                            String.format(Constants.FILTER_FORMAT, lastName))
                    : builder.conjunction();

            Predicate firstNamePredicate = StringUtils.isNotBlank(firstName)
                    ? builder.like(root.get("firstName"),
                            String.format(Constants.FILTER_FORMAT, firstName))
                    : builder.conjunction();

            return builder.and(enabledPredicate, phoneNumberPredicate, lastNamePredicate,
                    firstNamePredicate);
        };
    }

    public Specification<Customer> buildCustomerSpecification(
            String phoneNumber, String lastName, String firstName, String email,
            String register, String gender, Integer minAge, Integer maxAge,
            Date birthDateFrom, Date birthDateTo, Boolean isDeceased, Integer isParent) {

        return (root, query, builder) -> {
            Predicate enabledPredicate = builder.equal(root.join("user").get("enabled"), true);

            // Check if any search criteria is provided
            boolean hasSearchCriteria = StringUtils.isNotBlank(phoneNumber) ||
                    StringUtils.isNotBlank(lastName) ||
                    StringUtils.isNotBlank(firstName) ||
                    StringUtils.isNotBlank(email) ||
                    StringUtils.isNotBlank(register) ||
                    StringUtils.isNotBlank(gender) ||
                    minAge != null ||
                    maxAge != null ||
                    birthDateFrom != null ||
                    birthDateTo != null ||
                    isDeceased != null ||
                    isParent != null;

            // If no search criteria, return all enabled users (no filtering)
            if (!hasSearchCriteria) {
                return enabledPredicate;
            }

            Predicate phoneNumberPredicate = StringUtils.isNotBlank(phoneNumber)
                    ? builder.like(builder.lower(root.get("phoneNumber")),
                            String.format("%%%s%%", phoneNumber.toLowerCase()))
                    : builder.conjunction();

            Predicate lastNamePredicate = StringUtils.isNotBlank(lastName)
                    ? builder.like(builder.lower(root.get("lastName")),
                            String.format("%%%s%%", lastName.toLowerCase()))
                    : builder.conjunction();

            Predicate firstNamePredicate = StringUtils.isNotBlank(firstName)
                    ? builder.like(builder.lower(root.get("firstName")),
                            String.format("%%%s%%", firstName.toLowerCase()))
                    : builder.conjunction();

            Predicate emailPredicate = StringUtils.isNotBlank(email)
                    ? builder.like(builder.lower(root.get("email")),
                            String.format("%%%s%%", email.toLowerCase()))
                    : builder.conjunction();

            Predicate registerPredicate = StringUtils.isNotBlank(register)
                    ? builder.like(builder.lower(root.get("register")),
                            String.format("%%%s%%", register.toLowerCase()))
                    : builder.conjunction();

            Predicate genderPredicate = StringUtils.isNotBlank(gender)
                    ? builder.equal(root.get("gender"), gender)
                    : builder.conjunction();

            Predicate minAgePredicate = minAge != null
                    ? builder.greaterThanOrEqualTo(root.get("age"), minAge)
                    : builder.conjunction();

            Predicate maxAgePredicate = maxAge != null
                    ? builder.lessThanOrEqualTo(root.get("age"), maxAge)
                    : builder.conjunction();

            Predicate birthDateFromPredicate = birthDateFrom != null
                    ? builder.greaterThanOrEqualTo(root.get("birthDate"), birthDateFrom)
                    : builder.conjunction();

            Predicate birthDateToPredicate = birthDateTo != null
                    ? builder.lessThanOrEqualTo(root.get("birthDate"), birthDateTo)
                    : builder.conjunction();

            Predicate isDeceasedPredicate = isDeceased != null
                    ? builder.equal(root.get("isDeceased"), isDeceased)
                    : builder.conjunction();

            Predicate isParentPredicate = isParent != null
                    ? builder.equal(root.get("isParent"), isParent)
                    : builder.conjunction();

            return builder.and(
                    enabledPredicate, phoneNumberPredicate, lastNamePredicate, firstNamePredicate,
                    emailPredicate, registerPredicate, genderPredicate, minAgePredicate,
                    maxAgePredicate,
                    birthDateFromPredicate, birthDateToPredicate, isDeceasedPredicate,
                    isParentPredicate);
        };
    }
}
