package com.api.family.apitreeservice.exception;

import org.springframework.http.HttpStatus;

public class Errors {

    private Errors() {
    }

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
    public static final CustomError FILE_OBJECT_NOT_FOUND = new CustomError(HttpStatus.NOT_FOUND,
            "TI115",
            "The file object is not found.");
    public static final CustomError FAILED_DELETING_FILE = new CustomError(HttpStatus.INTERNAL_SERVER_ERROR,
            "TI113",
            "Failed deleting the file.");
    public static final CustomError UNKNOWN_FILE_TYPE = new CustomError(HttpStatus.BAD_REQUEST,
            "TI114",
            "Unknown file type.");
    public static final CustomError FAILED_CONVERTING_FILE_TO_BYTE = new CustomError(HttpStatus.INTERNAL_SERVER_ERROR,
            "TI138",
            "Failed converting a file to byte array.");
    public static final CustomError NOT_HUSBAND = new CustomError(HttpStatus.BAD_REQUEST, "API0012",
            "Гэр бүлийн гишүүнээ сонгоно уу.");
    public static final CustomError NOT_CHILD = new CustomError(HttpStatus.BAD_REQUEST, "API0013",
            "Таньд бүртгүүлсэн хүүхдийн мэдээлэл байхгүй байна.");

}
