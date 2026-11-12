package com.eoxys.utils;

public enum UserRole {
    CUSTOMER,
    SELLER,
    INVENTORY,
    WAREHOUSE,
    ADMIN;

    public static UserRole fromString(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return CUSTOMER;
        }
        String clean = roleStr.trim().toUpperCase();
        if (clean.equals("USER") || clean.equals("ROLE_USER") || clean.equals("ROLE_CUSTOMER") || clean.equals("CUSTOMER")) {
            return CUSTOMER;
        }
        if (clean.equals("ROLE_SELLER") || clean.equals("SELLER")) return SELLER;
        if (clean.equals("ROLE_INVENTORY") || clean.equals("INVENTORY")) return INVENTORY;
        if (clean.equals("ROLE_WAREHOUSE") || clean.equals("WAREHOUSE")) return WAREHOUSE;
        if (clean.equals("ROLE_ADMIN") || clean.equals("ADMIN")) return ADMIN;

        try {
            return UserRole.valueOf(clean);
        } catch (IllegalArgumentException e) {
            return CUSTOMER;
        }
    }

    public String toAuthority() {
        return "ROLE_" + this.name();
    }
}

