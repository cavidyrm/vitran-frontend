/**
 * Placeholder SQLite WASM worker entry.
 * Replace with the AndroidX reference worker (sqlite-wasm + OPFS protocol) before production OPFS.
 * Hosted next to the Wasm app; requires Cross-Origin-Opener-Policy: same-origin and
 * Cross-Origin-Embedder-Policy: require-corp on the document.
 *
 * Until the full worker is vendored, WasmDatabaseFactory falls back to in-memory SQLite.
 */
self.onmessage = function (event) {
  // Protocol messages are handled by the official AndroidX sqlite-wasm worker implementation.
  console.warn(
    "[vitran] sqlite3.worker.js placeholder — OPFS driver unavailable; using memory fallback.",
  );
};
