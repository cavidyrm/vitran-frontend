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
        assertFalse(permissions.canAccessAdmin(setOf(UserRole.User)))
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
    fun onlySuperAdminCanEditRoles_andSuperAdminIsNeverAssignable() {
        assertEquals(
            emptyList(),
            permissions.assignableRoles(setOf(UserRole.Admin)),
        )
        assertEquals(
            listOf(UserRole.User, UserRole.Admin),
            permissions.assignableRoles(setOf(UserRole.SuperAdmin)),
        )
        assertFalse(permissions.canEditUserRoles(setOf(UserRole.Admin)))
        assertTrue(permissions.canEditUserRoles(setOf(UserRole.SuperAdmin)))
    }

    @Test
    fun rolesPayload_omitsRolesForNonSuperAdmin() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.Admin),
            existingTargetRoles = setOf(UserRole.User, UserRole.Admin),
            selectedEditableRoles = setOf(
                UserRole.User,
                UserRole.Admin,
                UserRole.SuperAdmin,
            ),
        )

        assertEquals(null, payload)
    }

    @Test
    fun rolesPayload_allowsAdminForSuperAdmin_andPreservesExistingSuperAdmin() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.SuperAdmin),
            existingTargetRoles = setOf(UserRole.User, UserRole.SuperAdmin),
            selectedEditableRoles = setOf(UserRole.User, UserRole.Admin),
        )

        assertEquals(listOf("user", "admin", "super_admin"), payload)
    }

    @Test
    fun rolesPayload_allowsSuperAdminToRemoveAdmin() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.SuperAdmin),
            existingTargetRoles = setOf(UserRole.User, UserRole.Admin),
            selectedEditableRoles = setOf(UserRole.User),
        )

        assertEquals(listOf("user"), payload)
    }

    @Test
    fun rolesPayload_preservesUnknownExistingRoles() {
        val payload = permissions.buildRolesUpdatePayload(
            actorRoles = setOf(UserRole.SuperAdmin),
            existingTargetRoles = setOf(UserRole.User, UserRole.Unknown("auditor")),
            selectedEditableRoles = setOf(UserRole.User),
        )

        assertEquals(listOf("user", "auditor"), payload)
    }
}
