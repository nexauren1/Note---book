# Notoora — Android notes, themes and paper

Notoora is a native Android note-taking app inspired by the workflow shown in the reference screenshots. The code, icon, palettes and paper patterns in this project are original; the reference app's brand and artwork are not copied.

## Included in the current prototype

- Kotlin + Jetpack Compose Android UI.
- Local-first notes stored in SQLite, with schema migration.
- Auto-save, search across title/body/label, custom labels, favourites and pinning.
- Archive and trash flows. Deleted notes remain as tombstones to carry deletions through backup merges.
- 24 procedural paper presets generated at render time with Compose Canvas: plain, lined, grid, dots, gingham, confetti, leafy borders, gradients, stars and margin styles.
- 12 color palettes for the app accent and note appearance.
- Drawing canvas with selected ink colors, adjustable stroke width, undo and clear; strokes are saved as normalized points in each note and included in JSON/Drive backups.
- Checklist, bullets, numbered list and emoji/sticker insertion helpers.
- Local document attachment picker (retains document URI).
- JSON import/export via Android's document picker. Import validates Notoora backup format and merges by note UUID/updated timestamp.
- User-triggered Google Drive backup/sync code. It uses Google Identity authorization and the Drive API drive.file scope, and keeps one Notoora Backup.json file.

## Build

Requires JDK 17 and Android SDK platform 35. Run: gradle --no-daemon :app:assembleDebug.

A GitHub Actions workflow compiles app/build/outputs/apk/debug/app-debug.apk and uploads it as the Notoora-debug-apk artifact after every push to main.

## Configure Google Drive before testing sync

Drive integration needs developer setup before it can authenticate:

1. Create/select a project in Google Cloud Console and enable Google Drive API.
2. Configure the OAuth consent screen and add test users while in testing mode.
3. Register an Android OAuth client for package com.nexauren.notoora, using the SHA-1 certificate fingerprint for the build being tested. Add the release signing fingerprint before publishing.
4. Use the https://www.googleapis.com/auth/drive.file scope requested in code.
5. Build and run on Android with Google Play services and an eligible Google account.

Google may require OAuth verification before general public use. Never publish a private signing key or OAuth client secret in this repository.

## Known limitations before public release

- Drive sync is user-triggered, not background sync.
- Attachments currently store URI references; the backup does not yet package attachment bytes for full cross-device migration.
- Handwriting/drawing strokes are saved, but this prototype has not yet added pressure sensitivity, eraser tools, or export-to-image.
- Home-screen widgets, reminders/notifications, biometric lock, full localization, UI/unit tests, accessibility review, and a signed release build remain future work.
- The name Notoora is provisional until store, trademark and domain availability checks are completed.

This is a buildable engineering prototype, not yet a production release. Please test on real devices before distribution.
