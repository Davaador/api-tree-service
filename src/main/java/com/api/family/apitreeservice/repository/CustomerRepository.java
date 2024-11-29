package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByRegister(String register);

    Optional<Customer> findByUser(User user);

    List<Customer> findByGender(String gender);

    Optional<Customer> findByWife(Customer wife);

    Optional<Customer> findByHusband(Customer husband);
    List<Customer> findByAgeGreaterThanEqual(Integer age);
    List<Customer> findByParent(Customer parent);
    List<Customer> findByParentAndIsParent(Customer parent, Integer isParent);
    Optional<Customer> findById(int id);
    @NotNull List<Customer> findAll();

}
