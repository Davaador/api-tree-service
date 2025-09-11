package com.api.family.apitreeservice.validator;

import com.api.family.apitreeservice.constants.Constants;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class Functions {

    private Functions() {
        throw new IllegalStateException("Utility class");
    }

    public static String getGender(String registerNo) {
        if (StringUtils.isNotBlank(registerNo)) {
            return Integer.parseInt(String.valueOf(registerNo.charAt(registerNo.length() - 2))) % 2 == 0
                    ? Constants.WOMEN_GENDER
                    : Constants.MEN_GENDER;
        }
        return Constants.MEN_GENDER;
    }

    public static int getAge(String registerNo) {
        int bl = 0;
        int age;
        int year;
        int month;
        int day;
        Calendar calendarInstance;
        if (Integer.parseInt(registerNo.substring(4, 6)) > 12) {
            bl = 1;
        }

        if (bl == 0) {
            year = Integer.parseInt(registerNo.substring(2, 4)) + 1900;
            month = Integer.parseInt(registerNo.substring(4, 6));
        } else {
            year = Integer.parseInt(registerNo.substring(2, 4)) + 2000;
            month = Integer.parseInt(registerNo.substring(4, 6)) - 20;
        }
        day = Integer.parseInt(registerNo.substring(6, 8));

        calendarInstance = Calendar.getInstance();
        calendarInstance.add(Calendar.MONTH, +1);
        age = calendarInstance.get(Calendar.YEAR) - year;
        if (calendarInstance.get(Calendar.MONTH) < month ||
                (calendarInstance.get(Calendar.MONTH) == month && calendarInstance.get(Calendar.DAY_OF_MONTH) < day)) {
            age = age - 1;
        }

        return age;
    }

    public static @NotNull Date getBirthday(@NotNull String registerNo) {
        int year;
        int month;
        int day;
        int bl = 0;

        Calendar calendarInstance;
        if (Integer.parseInt(registerNo.substring(4, 6)) > 12) {
            bl = 1;
        }

        if (bl == 0) {
            year = Integer.parseInt(registerNo.substring(2, 4)) + 1900;
            month = Integer.parseInt(registerNo.substring(4, 6));
        } else {
            year = Integer.parseInt(registerNo.substring(2, 4)) + 2000;
            month = Integer.parseInt(registerNo.substring(4, 6)) - 20;
        }
        day = Integer.parseInt(registerNo.substring(6, 8));
        calendarInstance = Calendar.getInstance();
        calendarInstance.set(Calendar.YEAR, year);
        calendarInstance.set(Calendar.MONTH, month - 1);
        calendarInstance.set(Calendar.DAY_OF_MONTH, day);
        return calendarInstance.getTime();
    }

    public static void matchPasswords(String password, String matchingPassword) {
        if (!matchingPassword.equals(password))
            throw new CustomException(Errors.NOT_MATCH_PASSWORD);
    }

    /**
     * Төрсөн өдрөөр нь насыг бодож олно
     * 
     * @param birthDate төрсөн өдөр (Date)
     * @return нас (int)
     */
    public static int calculateAgeFromBirthDate(@NotNull Date birthDate) {
        LocalDate birthLocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthLocalDate, currentDate).getYears();
    }

    /**
     * Төрсөн өдрөөр нь насыг бодож олно
     * 
     * @param birthDate төрсөн өдөр (LocalDate)
     * @return нас (int)
     */
    public static int calculateAgeFromBirthDate(@NotNull LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    /**
     * Төрсөн өдрөөр нь насыг бодож олно (String format)
     * 
     * @param birthDateString төрсөн өдөр (String format: "yyyy-MM-dd")
     * @return нас (int)
     */
    public static int calculateAgeFromBirthDate(@NotNull String birthDateString) {
        try {
            LocalDate birthDate = LocalDate.parse(birthDateString);
            return calculateAgeFromBirthDate(birthDate);
        } catch (Exception e) {
            throw new CustomException(Errors.INVALID_DATE_FORMAT);
        }
    }

}
