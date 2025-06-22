package com.ctrlbuy.webshop.security.entity;

/**
 * ✅ UPPDATERAD: ERole enum med utility-metoder och kompatibilitet
 */
public enum ERole {
    ROLE_USER("USER", "Standard användare"),
    ROLE_MODERATOR("MODERATOR", "Moderator med begränsade admin-rättigheter"),
    ROLE_ADMIN("ADMIN", "Administratör med fulla rättigheter");

    private final String roleName;
    private final String description;

    // ✅ Konstruktor
    ERole(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    // ✅ Getters
    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    // ✅ UTILITY METODER för kompatibilitet med User entity

    /**
     * Konvertera från String (som används i User.roles) till ERole
     */
    public static ERole fromString(String roleStr) {
        if (roleStr == null) {
            return ROLE_USER; // Default
        }

        // Hantera både "ROLE_USER" och "USER" format
        String cleanRole = roleStr.toUpperCase();
        if (!cleanRole.startsWith("ROLE_")) {
            cleanRole = "ROLE_" + cleanRole;
        }

        try {
            return ERole.valueOf(cleanRole);
        } catch (IllegalArgumentException e) {
            return ROLE_USER; // Default vid ogiltigt värde
        }
    }

    /**
     * Konvertera till String-format som används i User.roles
     */
    public String toRoleString() {
        return this.roleName; // Returnerar "USER", "ADMIN", etc.
    }

    /**
     * Konvertera till fullständigt roll-namn
     */
    public String toFullRoleName() {
        return this.name(); // Returnerar "ROLE_USER", "ROLE_ADMIN", etc.
    }

    /**
     * Kontrollera om denna roll har högre behörighet än en annan
     */
    public boolean hasHigherAuthorityThan(ERole other) {
        return this.ordinal() > other.ordinal();
    }

    /**
     * Kontrollera om denna roll har minst samma behörighet som en annan
     */
    public boolean hasAtLeastAuthorityOf(ERole other) {
        return this.ordinal() >= other.ordinal();
    }

    /**
     * Kontrollera om denna roll är admin eller högre
     */
    public boolean isAdminOrHigher() {
        return this == ROLE_ADMIN;
    }

    /**
     * Kontrollera om denna roll är moderator eller högre
     */
    public boolean isModeratorOrHigher() {
        return this == ROLE_MODERATOR || this == ROLE_ADMIN;
    }

    /**
     * Hämta alla roller som String-lista (för User.roles kompatibilitet)
     */
    public static String[] getAllRoleNames() {
        return new String[]{"USER", "MODERATOR", "ADMIN"};
    }

    /**
     * Validera om en roll-string är giltig
     */
    public static boolean isValidRole(String roleStr) {
        try {
            fromString(roleStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.roleName;
    }
}