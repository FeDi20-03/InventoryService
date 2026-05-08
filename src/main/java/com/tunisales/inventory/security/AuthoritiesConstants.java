package com.tunisales.inventory.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    // Tunisales business roles
    public static final String ADMIN_COMMERCIAL = "ROLE_ADMIN_COMMERCIAL";

    public static final String ADMIN_SYSTEME = "ROLE_ADMIN_SYSTEME";

    public static final String COMMERCIAL = "ROLE_COMMERCIAL";

    public static final String MAGASINIER = "ROLE_MAGASINIER";

    private AuthoritiesConstants() {}
}
