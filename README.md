# Notoora — Android notes, themes and paper

Notoora is a native Android note-taking app inspired by the workflow shown in the reference screenshots. The code, icon, palettes and paper patterns in this project are original; the reference app's brand and artwork are not copied.

## Implemented in this first build

- Kotlin + Jetpack Compose Android UI.
- Local-first notes stored in SQLite.
- Auto-save while editing, search across title/body/label, note labels, favourites and pinning.
- Archive and trash flows. Deleted notes are retained as tombstones so deletions can travel through a sync.
- 24 procedural paper presets: plain, lined, grid, dots, gingham, confetti, leaf-like borders, gradients, stars and margins. These designs are generated in Compose Canvas instead of shipping a large image pack.
- 12 accent palettes.
- Checklist, bullet, numbered-list and emoji/sticker insertion helpers.
- File attachment picker (stores selected document URIs).
- JSON import/export using Android's system document picker.
- Google Drive sync/backup code using Google Identity authorization and the Drive API drive.file scope. Notes are merged by UUID and updatedAt before uploading one Notoora Backup.json file.

## Build

Requires JDK 17 and Android SDK platform 35. Run: gradle --no-daemon :app:assembleDebug

GitHub Actions builds app/build/outputs/apk/debug/app-debug.apk and uploads it as the Notoora-debug-apk workflow artifact after each push to main.

## Configure Google Drive before testing sync

Drive integration is implemented, but it cannot work until the developer configures Google's OAuth client and enables the Drive API:

1. Create/select a project in Google Cloud Console and enable Google Drive API.
2. Configure the OAuth consent screen and add test users while the app is in testing mode.
3. Register an Android OAuth client for package com.nexauren.notoora, using the SHA-1 certificate fingerprint for the build being tested. Add the release signing fingerprint before publishing.
4. Request only the https://www.googleapis.com/auth/drive.file scope used in code.
5. Build and run on Android with Google Play services and an eligible Google account.

Google may require OAuth verification before general public use. Never publish a private signing key or OAuth client secret in this repository.

## Current boundaries and next work

This is the first engineering baseline, not yet a production release. The initial backup contains note text, labels, theme/background IDs, flags and attachment URI references. It does not yet package the actual bytes of attached media, so URI-based attachments do not automatically move between phones. Drive sync is user-triggered, not a background service. Before public release, add portable attachment packaging, a richer handwriting/drawing canvas with stroke persistence, reminders/notifications, home-screen widgets, per-note lock/biometrics, unit/UI tests, accessibility review and a signed release build.

The name Notoora is a working product name. Store, trademark and domain clearance should be completed before public release.
