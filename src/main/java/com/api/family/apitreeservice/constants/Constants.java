package com.api.family.apitreeservice.constants;

import java.util.List;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final List<String> ROOT_NUMBERS = List.of("11111111", "2222222");
    public static final List<String> ADMIN_NUMBERS = List.of("89330510", "89068293", "88970704");
    public static final Integer GREATER_AGE = 18;
    public static final String MEN_GENDER = "0";
    public static final String WOMEN_GENDER = "1";



}
