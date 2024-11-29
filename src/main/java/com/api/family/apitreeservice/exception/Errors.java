package com.api.family.apitreeservice.exception;

import org.springframework.http.HttpStatus;

public class Errors {

    private Errors(){}

    public static final CustomError INTERNAL_ERROR = new CustomError(HttpStatus.INTERNAL_SERVER_ERROR, "TI001",
            "Internal server error.");
    public static final CustomError INVALID_VALUE = new CustomError(HttpStatus.BAD_REQUEST, "TI002",
            "Validation(s) are failed.");
    public static final CustomError ACCESS_DENIED = new CustomError(HttpStatus.UNAUTHORIZED, "API005",
            "Access denied.");
    public static final CustomError BAD_CREDENTIALS = new CustomError(HttpStatus.BAD_REQUEST, "API109",
            "phone or password is incorrect.");
    public static final CustomError INVALID_REQUEST = new CustomError(HttpStatus.BAD_REQUEST, "API004",
            "Invalid request.");
    public static final CustomError NOT_FOUND = new CustomError(HttpStatus.NOT_FOUND, "TI003",
            "The resource is not found.");
    public static final CustomError DUPLICATED_PHONE_NUMBER = new CustomError(HttpStatus.BAD_REQUEST, "TI120",
            "User with the phone number already exists.");
    public static final CustomError ROLE_NOT_FOUND = new CustomError(HttpStatus.NOT_FOUND, "TI107",
            "The role you are looking for is not found.");
    public static final CustomError DISABLED_USER = new CustomError(HttpStatus.BAD_REQUEST, "TI130",
            "Your user is disabled.");
    public static final CustomError NOT_MATCH_PASSWORD = new CustomError(HttpStatus.BAD_REQUEST, "API131",
            "Оруулсан нууц үг зөрүүтэй байна.");
    public static final CustomError DUPLICATED_REGISTER = new CustomError(HttpStatus.BAD_REQUEST, "TI120",
            "User with the register already exists.");
    public static final CustomError NOT_PENDING_USERS = new CustomError(HttpStatus.BAD_REQUEST, "NOT_PENDING_USERS",
            "Хүсэлт өгсөн харилцагч байхгүй байна.");
    public static final CustomError NOT_ADMIN = new CustomError(HttpStatus.BAD_REQUEST, "NOT_ADMIN",
            "Админ эрхээр зөвшөөрнө үү..");
    public static final CustomError ACTIVE_WIFE = new CustomError(HttpStatus.BAD_REQUEST, "NOT_ADMIN",
            "Бүртгүүлсэн гишүүн байна. Та мэдээллээ шалгана уу?");

}
