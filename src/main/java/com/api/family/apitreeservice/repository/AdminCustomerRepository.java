package com.api.family.apitreeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.family.apitreeservice.model.postgres.AdminCustomer;
import com.api.family.apitreeservice.model.postgres.Customer;

import java.util.List;

public interface AdminCustomerRepository
                extends JpaRepository<AdminCustomer, Long>, JpaSpecificationExecutor<AdminCustomer> {
        @Query("select ac.newCustomer from AdminCustomer ac where ac.admin_id = :adminId order by ac.createdDateTime desc")
        List<Customer> findCustomersByAdminId(@Param("adminId") Integer adminId);
}
