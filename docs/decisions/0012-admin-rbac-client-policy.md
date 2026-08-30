# ADR 0012 — Admin RBAC client policy

## Status

Accepted — Phase 11

## Context

VitranShop exposes administrative screens to `admin` and `super_admin` users. Some operations are Super Admin-only, and user role updates can accidentally escalate privilege or remove an existing target's `super_admin` role if client forms construct payloads independently.

Client role state can be stale or manipulated. It cannot replace server authorization.

## Decision

1. `AdminPermissions` is the single client policy used for admin navigation and action visibility.
2. Client checks are UX-only; backend authorization and server `403` responses are authoritative.
3. Unknown roles, missing account state, customer, and seller roles grant no admin privilege.
4. Both Admin and Super Admin may access normal admin workflows.
5. Assigning the `admin` role is Super Admin-only.
6. `super_admin` is never offered as an assignable role.
7. Existing `super_admin` is preserved in an admin-user PATCH payload.
8. City deletion, taxonomy import, plan deletion, and static-page deletion are Super Admin-only client actions.
9. Updating the current user's roles/status refreshes `AccountRepository` state.
10. Views must not scatter direct role-set checks when an `AdminPermissions` capability exists.

## Alternatives

- Trust client role checks as enforcement — rejected; clients are not security boundaries.
- Decode JWT claims independently in admin UI — rejected; account state is the established role source and the server remains authoritative.
- Offer all controls and rely only on 403 — rejected; it creates misleading and unsafe UX.
- Hard-code checks in each screen — rejected; policies would drift.

## Consequences

- Admin affordances remain consistent across screens.
- Unknown future roles fail closed.
- Super Admin-only actions are hidden or disabled for normal Admin users but still require backend enforcement.
- Role payload construction is testable independently from Compose UI.
