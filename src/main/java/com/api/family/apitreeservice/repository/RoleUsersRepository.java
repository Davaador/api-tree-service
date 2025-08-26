package com.api.family.apitreeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.api.family.apitreeservice.model.postgres.RoleUsers;
import java.util.List;

public interface RoleUsersRepository extends JpaRepository<RoleUsers, Long>, JpaSpecificationExecutor<RoleUsers> {

    List<RoleUsers> findByPhoneNumber(String phoneNumber);

}
