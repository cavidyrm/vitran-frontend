# Room schema export

JSON schemas for `VitranDatabase` are written here when KSP runs with:

```
ksp {
  arg("room.schemaLocation", "$projectDir/schemas")
}
```

- File naming follows Room’s convention: `{fully.qualified.DatabaseClass}/{version}.json`
- Schema version 1 is cache-only; destructive migration on upgrade is allowed for disposable
  public cache tables (see `docs/persistence-offline-strategy.md`).
- Commit exported schemas once KSP has generated them so migration diffs are reviewable.
- Never store credentials or tokens in this database.
