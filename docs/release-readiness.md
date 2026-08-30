# Release Readiness Checklist (Phase 12)

## Application

- [ ] Production base URL origin-only (`https://vitran.ir`)
- [ ] Release ≠ Local env / OTP display / mock payment
- [ ] Session restore before anonymous flash
- [ ] Offline cached reads work; mutations show clear offline error
- [ ] CMS HTML sanitized
- [ ] Critical flows smoke-tested

## Backend / API

- [ ] Unresolved gaps reviewed ([api-gaps.md](api-gaps.md), [production-blockers.md](production-blockers.md))
- [ ] Incomplete advertised features disabled or marked blocked
- [ ] Payment return still open — do not claim complete

## Security

- [ ] No secrets in git / catalog / BuildConfig
- [ ] Platform secure storage correct per target
- [ ] Android backup exclusions
- [ ] Logging redaction
- [ ] No trust-all TLS

## Privacy

- [ ] Data inventory accurate
- [ ] Third-party SDK list accurate
- [ ] CMS privacy/terms URLs from backend
- [ ] Store Data Safety / App Privacy EXTERNAL verification

## Android

- [ ] Release build + R8 smoke
- [ ] Signing via external secrets
- [ ] Permissions / cleartext / network security
- [ ] Icon / name / version
- [ ] Deep links only if verified

## iOS

- [ ] Archive where macOS available
- [ ] Keychain path
- [ ] Info.plist permissions match use
- [ ] Signing external
- [ ] Universal Links association EXTERNAL if used

## Desktop

- [ ] Package builds on supported host OS
- [ ] Secure token storage
- [ ] Signing/notarization EXTERNAL

## Web

- [ ] Production Wasm bundle
- [ ] COOP/COEP for OPFS
- [ ] CSP / CORS documented
- [ ] No private secrets in bundle
- [ ] Credential strategy intentional (in-memory)
- [ ] Source maps policy decided

## CI/CD

- [ ] PR tests + compile
- [ ] Deploy does not auto-publish stores
- [ ] Secrets not echoed

## Store metadata

- [ ] Privacy / support URLs real
- [ ] Screenshots EXTERNAL manual
- [ ] Current Play/App Store policy EXTERNAL VERIFICATION REQUIRED

## Monitoring

- [ ] CrashReporter provider EXTERNAL or accept NoOp risk
- [ ] Auth refresh / payment verify / startup failure plan

## Rollback

- [ ] Prior Android/iOS build per store rules
- [ ] Web: redeploy previous GHCR tag
- [ ] Desktop: republish prior installer
- [ ] DB: client rollback may not downgrade schema — cache recreation policy documented

## Smoke checklist

Fresh install; update; anonymous browse; register/login; restart authenticated; search; wishlist; Product Contact; seller create; product create; plan/payment (sandbox); Admin (test acct); offline cached read; logout.
