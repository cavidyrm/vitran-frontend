# Security Production Review (Phase 12)

## Threat model (practical)

| Threat | Mitigation |
|--------|------------|
| Credential theft | Platform secure storage; never Room/prefs/localStorage for tokens |
| Token leakage in logs | `LoggingSanitizer`; release logging gated |
| API-key leakage | Ephemeral UI; no Room/analytics; clipboard warning |
| Payment spoofing | Never trust return params; refresh subscription |
| HTML/XSS | `AllowlistHtmlSanitizer`; no script execution |
| Unsafe deep links | Typed routes; unknown → safe fail |
| Path traversal | Sanitized download names; picker metadata |
| Cross-account cache leak | No private Room by default; StateStores clear on logout |
| Admin escalation | Client RBAC ≠ security; server enforces |
| Web bundled secrets | None; client code is public |
| Debug shipping | Env gating OTP/mock; release ≠ Local |

## Secure storage

| Platform | Implementation |
|----------|----------------|
| Android | EncryptedSharedPreferences + Keystore; backup exclusions |
| iOS | KVault → Keychain |
| Desktop | OS-backed store (KVault/file encryption) — release gate |
| Wasm/JS | In-memory only (intentional; no localStorage bearer) |

## Network / TLS

- Production origin: `https://vitran.ir` (origin-only)
- No trust-all / hostname bypass
- No certificate pinning (ops risk; decision documented)
- Android release: cleartext disabled; debug may allow localhost
- Mutation retries remain non-idempotent-safe (Phase 2 policy)

## Logging redaction

Never log: Authorization, access/refresh/temp tokens, OTP, passwords, Shop API keys, payment URLs/authority, multipart binaries, CSV bodies, review/comment bodies, Admin datasets.

## Payment security

Open `payment_url` ≠ success. Never call provider callback from app. Subscription refresh is authoritative. Process-death pending is best-effort memory only (Gap 41).

## API-key security

Regenerated keys shown once; not persisted; avoid auto-copy without user action where possible.

## HTML / XSS

Allowlist tags/attrs/schemes. Re-test on Wasm renderer. CSP documented for hosting.

## File security

No local path leak in multipart; sanitize filenames; cancel cleans temps/object URLs.

## Deep links

Only verified in-app routes. Payment return **not invented**. Auth deep links preserve pending nav without credentials.

## Admin RBAC

Release builds must not include role bypass. Tests cover permission gates.

## Web secrets

No private API secrets in Wasm bundles.

## Backup behavior

Android: exclude credential SharedPreferences from backup/extraction.

## Dependency security

Upgrade only for security/compat/production bugs. Inventory in release checklist.

## Release signing

Keys/passwords external (env / CI secrets). Never commit.

## Open blockers

See [production-blockers.md](production-blockers.md) — Web auth persistence, payment return, currency, Boost `price_paid` trust, Desktop secure storage until verified.
