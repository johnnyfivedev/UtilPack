package com.johnnyfivedev.utilpack;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StringUtils {

    public static boolean eitherNullEmptyWhitespaced(@Nullable String s) {
        return s == null || s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean neitherNullEmptyWhitespaced(@Nullable String s) {
        return !eitherNullEmptyWhitespaced(s);
    }

    public static String removeLastEntrance(@Nullable String target, @NonNull String sequence) {
        if (target == null) {
            return null;
        }

        if (target.contains(sequence)) {
            return target.substring(0, target.lastIndexOf(sequence));
        } else {
            return target;
        }
    }

    public static String toEmptyIfNull(@Nullable String s) {
        return s == null ? "" : s;
    }

    public static CharSequence toEmptyIfNull(@Nullable CharSequence charSequence) {
        return charSequence == null ? "" : charSequence;
    }

    public static String decapitalizeWithFirstCapital(String target) {
        if (target == null || target.length() == 0) {
            return target;
        }
        String result = target.toLowerCase();
        return result.substring(0, 1).toUpperCase() + result.substring(1);
    }

    public static String capitalizeFirstChar(String s) {
        if (eitherNullEmptyWhitespaced(s)) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}