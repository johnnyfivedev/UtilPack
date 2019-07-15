package com.johnnyfivedev.utilpack;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.TimeZone;

import javax.annotation.Nullable;


public class Validator {

    private final static int YEAR_OF_BIRTH_BOTTOM_THRESHOLD = 1900;

    public static boolean isValidPassword(String password) {
        return password.length() > 5;
    }

    public static boolean isValidEmail(@Nullable String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidRussianPassportSeries(@Nullable String passportSeries) {
        return passportSeries != null && passportSeries.length() == 4 && TextUtils.isDigitsOnly(passportSeries);
    }

    public static boolean isValidRussianPassportNumber(@Nullable String passportNumber) {
        return passportNumber != null && passportNumber.length() == 6 && TextUtils.isDigitsOnly(passportNumber);
    }

    /*public static boolean isYearOfBirthValid(int yearOfBirth) {
        return yearOfBirth > YEAR_OF_BIRTH_BOTTOM_THRESHOLD && yearOfBirth <= DateTime.now(TimeZone.getDefault()).getYear();
    }*/
}
