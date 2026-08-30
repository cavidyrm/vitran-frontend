# ADR 0013 — CMS HTML sanitization and rendering

## Status

Accepted — Phase 11

## Context

Static-page bodies are backend-managed HTML and must render across Android, iOS, Desktop, JS, and Wasm. Rendering arbitrary HTML in a WebView or assigning unsanitized browser `innerHTML` would create script, event-handler, unsafe-link, and platform-divergence risks.

## Decision

1. Represent HTML explicitly as `HtmlContent`, not a generic `String`.
2. Sanitize content in `PublicStaticPageViewModel` with the commonMain `AllowlistHtmlSanitizer` before presentation.
3. Permit only the documented basic formatting tags and `http`/`https` anchor targets; discard dangerous blocks, unknown tags, and unapproved attributes.
4. Render the sanitized subset with the Compose-native `SafeHtml` component.
5. Do not use Android WebView as the shared rendering architecture.
6. Do not assign unsanitized content to `innerHTML` on Web/Wasm.
7. Revalidate links in the renderer and open them through `ExternalUrlLauncher`.
8. Unsupported rich HTML degrades to the limited supported text structure rather than executing or embedding arbitrary content.

## Alternatives

- Platform WebViews — rejected as a shared architecture because behavior and security policy would diverge by target.
- Raw `innerHTML` for browser targets — rejected because the CMS response is untrusted input.
- Strip all markup — rejected because static pages require basic headings, lists, emphasis, and links.
- Full HTML/CSS engine in common code — rejected as unnecessary complexity for the current contract.

## Consequences

- Static-page rendering is deterministic and portable.
- The sanitizer and renderer remain separately testable.
- Rich layouts, images, tables, inline styles, and embedded media are not supported unless the allowlist and renderer are deliberately extended together.
- `ExternalUrlLauncher` remains the platform boundary for links.
