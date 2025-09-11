package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByRegister(String register);

    Optional<Customer> findByUser(User user);

    List<Customer> findByGender(String gender);

    Optional<Customer> findByWife(Customer wife);

    Optional<Customer> findByHusband(Customer husband);

    List<Customer> findByAgeGreaterThanEqual(Integer age);

    List<Customer> findByParent(Customer parent);

    List<Customer> findByParentAndIsParent(Customer parent, Integer isParent);

    Optional<Customer> findById(int id);

    @NonNull
    List<Customer> findAll();

    Optional<Customer> findByEmail(String email);

}
