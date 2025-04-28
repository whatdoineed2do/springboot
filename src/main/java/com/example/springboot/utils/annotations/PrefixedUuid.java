package com.example.springboot.utils.annotations;

import java.util.UUID;

public class PrefixedUuid {
    public static String generate(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
