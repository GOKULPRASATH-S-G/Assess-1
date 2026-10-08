package com.petcare.model;

/**
 * Enumeration representing pet gender.
 */
public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String text) {
        if (text == null) return MALE;
        String trimmed = text.trim().toUpperCase();
        if (trimmed.startsWith("F")) return FEMALE;
        return MALE;
    }
}
