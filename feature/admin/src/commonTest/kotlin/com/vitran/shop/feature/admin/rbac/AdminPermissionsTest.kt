package com.vitran.shop.feature.admin.rbac

import com.vitran.shop.core.domain.auth.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdminPermissionsTest {

    private val permissions = AdminPermissions()

    @Test
    fun adminAccess_requiresAdminOrSuperAdmin() {
        assertFalse(permissions.canAccessAdmin(emptySet()))
        assertFalse(permissions.canAccessAdmin(setOf(UserRole.Customer, UserRole.Seller)))
        assertTrue(permissions.canAccessAdmin(setOf(UserRole.Admin)))
        assertTrue(permissions.canAccessAdmin(setOf(UserRole.SuperAdmin)))
    }

    @Test
    fun privilegedDestructiveActions_requireSuperAdmin() {
        val admin = setOf<UserRole>(UserRole.Admin)
        val superAdmin = setOf<UserRole>(UserRole.SuperAdmin)

        assertFalse(permissions.canDeleteCity(admin))
        assertFalse(permissions.canImportTaxonomy(admin))
        assertFalse(permissions.canDeletePlan(admin))
        assertFalse(permissions.canDeleteStaticPage(admin))

        assertTrue(permissions.canDeleteCity(superAdmin))
        assertTrue(permissions.canImportTaxonomy(superAdmin))
        assertTrue(permissions.canDeletePlan(superAdmin))
        assertTrue(permissions.canDeleteStaticPage(superAdmin))
    }

    @Test
    fun onlySuperAdminCanAssignAdmin_andSuperAdminIsNeverAssignable() {
        assertEquals(
            listOf(UserRole.Customer, UserRole.Seller),
            permissions.assignableRoles(setOf(UserRole.Admin)),
        )
        assertEquals(
            listOf(UserRole.Customer, UserRole.Seller, UserRole.Admin),
            permissions.assignableRoles(setOf(UserRole.SuperAdmin)),
        )
    }

    @Test
    fun rolesPayload_filtersAdminForNonSuperAdmin() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.Admin),
            existingTargetRoles = emptySet(),
            selectedEditableRoles = setOf(
                UserRole.Customer,
                UserRole.Seller,
                UserRole.Admin,
                UserRole.SuperAdmin,
            ),
        )

        assertEquals(listOf("customer", "seller"), payload)
    }

    @Test
    fun rolesPayload_allowsAdminForSuperAdmin_andPreservesExistingSuperAdmin() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.SuperAdmin),
            existingTargetRoles = setOf(UserRole.Customer, UserRole.SuperAdmin),
            selectedEditableRoles = setOf(UserRole.Seller, UserRole.Admin),
        )

        assertEquals(listOf("seller", "admin", "super_admin"), payload)
    }
}
