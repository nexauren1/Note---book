# NoteFlow — creative notes for Android

NoteFlow is a native Android note-taking prototype inspired by the broad workflow and customization ideas in the supplied reference screenshots. The implementation, brand identity, vector icon and procedural patterns are original; the reference app's name, code and artwork are not copied.

## Current features

- Native Android app built with Kotlin and Jetpack Compose.
- Local-first notes in SQLite with schema migration and automatic saving.
- Search, note labels, favorites, pinning, archiving, trash and restore.
- 24 procedural paper presets drawn at runtime: plain, ruled, grid, dots, gingham, confetti, foliage, gradients, stars and margins.
- Editor-only themes with coordinated page/card/text colors; paper backgrounds are a separate setting.
- Choose a photo from the device gallery for a note background.
- Drawing canvas with colored pen, adjustable width, undo and clear; saved strokes are serialized into note backups.
- Checklist, bullet and numbered-list helpers, emoji/stickers and file attachments.
- Ready-made and user-created labels with a dedicated label manager/page flow.
- Reminders with quick scheduling for one hour or the next morning and Android notifications.
- Resizable home-screen widget for the latest note, new notes, checklists, image/audio attachments and sketches, with 8 selectable color-gradient themes.
- JSON backup import/export with ID-and-timestamp merge.
- Google Drive user-triggered sync using Google Identity and the Drive API drive.file scope.

## Build and APK

Requires JDK 17 and Android SDK platform 35. Run gradle --no-daemon :app:assembleDebug.

GitHub Actions compiles app/build/outputs/apk/debug/app-debug.apk and uploads it as the NoteFlow-debug-apk artifact after each push to main.

## Google Drive setup and verification

Before Drive sync can work on your own account, configure a Google Cloud project and enable Google Drive API, set up the OAuth consent screen, and register an Android OAuth client for the current application package and signing certificate. Google may require consent-screen verification before public use. The app requests only https://www.googleapis.com/auth/drive.file.

The code now searches for the new NoteFlow Backup.json file first and can still find older Paperya Backup.json / Notoora Backup.json names. This preserves compatibility with backups created by earlier test builds. A successful authorization prompt alone does not prove that synchronization completed; check the in-app status message after a sync.

## Limits before a public release

- The technical Android package identifier is still com.nexauren.paperya for continuity with the current prototype build; choose a final package ID before public release.
- Drive sync is user-triggered, not background sync.
- Local attachment URIs and custom wallpaper URIs are not yet packaged into the backup, so their actual files may not move to another device. This needs a portable media export/import layer.
- Biometric lock, full UI/unit tests, broader accessibility checks, portable media backup and a signed release build remain to be completed.
- Test the next APK on real devices before distribution.
