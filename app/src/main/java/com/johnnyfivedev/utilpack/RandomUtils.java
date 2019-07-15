package com.johnnyfivedev.utilpack;

import java.util.UUID;

public class RandomUtils {

    public static String generateRandomId() {
        return UUID.randomUUID().toString();
    }
}
