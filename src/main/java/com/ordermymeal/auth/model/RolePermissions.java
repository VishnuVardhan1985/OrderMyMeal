package com.ordermymeal.auth.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class RolePermissions {

    private RolePermissions() {
    }

    private static final Map<Role, Set<Permission>> ROLE_PERMISSIONS = Map.of(

            Role.USER,
            EnumSet.of(
                    Permission.MENU_VIEW,
                    Permission.ORDER_CREATE,
                    Permission.ORDER_VIEW_OWN,
                    Permission.ORDER_CANCEL_OWN),

            Role.VENDOR,
            EnumSet.of(
                    Permission.MENU_VIEW,
                    Permission.VENDOR_MENU_MANAGE,
                    Permission.VENDOR_ORDER_VIEW,
                    Permission.VENDOR_ORDER_UPDATE,
                    Permission.VENDOR_MANIFEST_VIEW),

            Role.ADMIN,
            EnumSet.of(
                    Permission.MENU_VIEW,
                    Permission.ORDER_VIEW,
                    Permission.MEMBER_MANAGE,
                    Permission.VENDOR_MANAGE,
                    Permission.MENU_MANAGE,
                    Permission.REFUND_MANAGE,
                    Permission.REPORT_VIEW),

            Role.SUPER_ADMIN,
            EnumSet.allOf(Permission.class));

    public static Set<Permission> getPermissions(Role role) {
        return ROLE_PERMISSIONS.getOrDefault(role, Set.of());
    }

    public static boolean hasPermission(
            Set<Role> roles,
            Permission permission) {
        return roles.stream()
                .anyMatch(role -> getPermissions(role).contains(permission));
    }
}