package com.api.family.apitreeservice.constants;

import lombok.Getter;

@Getter
public enum RoleEnumString {
    ROLE_ADMIN("ADMIN"),
    ROLE_CUSTOMER("CUSTOMER"),
    ROLE_ROOT("ROOT"),
    CHILD("CHILD");


    private final String value;

    RoleEnumString(String value) {
        this.value = value;
    }



}
