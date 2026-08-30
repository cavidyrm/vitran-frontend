package com.vitran.shop.feature.admin.rbac

import com.vitran.shop.core.domain.auth.UserRole

class AdminPermissions {

    fun canAccessAdmin(roles: Set<UserRole>): Boolean =
        UserRole.Admin in roles || UserRole.SuperAdmin in roles

    fun canAssignAdminRole(actorRoles: Set<UserRole>): Boolean =
        UserRole.SuperAdmin in actorRoles

    fun canDeleteCity(roles: Set<UserRole>): Boolean =
        UserRole.SuperAdmin in roles

    fun canImportTaxonomy(roles: Set<UserRole>): Boolean =
        UserRole.SuperAdmin in roles

    fun canDeletePlan(roles: Set<UserRole>): Boolean =
        UserRole.SuperAdmin in roles

    fun canDeleteStaticPage(roles: Set<UserRole>): Boolean =
        UserRole.SuperAdmin in roles

    fun assignableRoles(actorRoles: Set<UserRole>): List<UserRole> = buildList {
        add(UserRole.Customer)
        add(UserRole.Seller)
        if (canAssignAdminRole(actorRoles)) add(UserRole.Admin)
    }

    fun buildRolesUpdatePayload(
        actorRoles: Set<UserRole>,
        existingTargetRoles: Set<UserRole>,
        selectedEditableRoles: Set<UserRole>,
    ): List<String> = buildList {
        if (UserRole.Customer in selectedEditableRoles) add("customer")
        if (UserRole.Seller in selectedEditableRoles) add("seller")
        if (UserRole.Admin in selectedEditableRoles && canAssignAdminRole(actorRoles)) add("admin")
        if (UserRole.SuperAdmin in existingTargetRoles) add("super_admin")
    }
}
