package com.api.family.apitreeservice.constants;

import lombok.Getter;

@Getter
public enum OtpStatusEnumString {

    NEW("N"),
    EXPIRY("E"),
    SUCCESS("S"),
    REJECT("R"),
    BLOCKED("B");

    private final String value;

    OtpStatusEnumString(String value) {
        this.value = value;
    }

}
