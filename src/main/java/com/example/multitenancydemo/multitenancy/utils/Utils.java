package com.example.multitenancydemo.multitenancy.utils;

import java.util.Collection;

public class Utils {

    /** private constructor */
    private Utils() {
        // this is a private constructor to hide the public one
    }

    public static boolean isNullOrEmpty(@SuppressWarnings("rawtypes") Collection c) {
        return c == null || c.isEmpty();
    }


    public static boolean isNullOrEmpty(Object c) {
        return c == null;
    }

}
