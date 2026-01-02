# Repository Guidelines

## Project Structure & Module Organization

- `app/`: Android application module.
- `app/src/main/java/com/denialshield/`: Kotlin source (UI, data, utils).
- `app/src/main/res/`: Android resources (use lowercase with underscores).
- `app/src/main/assets/`: Local assets such as `model.bin` and sample docs.
- `docs/`: Setup, build, usage, and CI guides.
- `scripts/`: Local and Docker build/test helpers (`build.sh`, `start.sh`, `test.sh`, `update.sh`).

## Build, Test, and Development Commands

- `./update`: Initialize/update git submodules.
- `./build`: Local debug build via `./gradlew assembleDebug`.
- `./start`: Docker-based build (no local Android toolchain required).
- `./test`: Docker-based test run.
- `./gradlew assembleDebug`: Direct Gradle build if you prefer.
- `./gradlew test`: Run unit tests (mirrors CI).

## Coding Style & Naming Conventions

- Language: Kotlin (Jetpack Compose UI, Room data layer).
- Formatting: Use Android Studio’s default Kotlin/Compose formatter (4-space indent).
- Naming: `PascalCase` for classes/Compose screens, `camelCase` for functions/vars.
- Packages: Keep code under `com.denialshield.*`; add new screens under `ui/screens`.

## Testing Guidelines

- Frameworks: JUnit4 for unit tests, AndroidX test for instrumentation.
- Placement: `app/src/test` for unit tests, `app/src/androidTest` for device tests.
- Run locally with `./gradlew test` or `./test` for Docker parity with CI.

## Commit & Pull Request Guidelines

- Commit messages often follow simple verbs (`Update README.md`) or prefixes like
  `fix:` and `chore:`. Prefer short, action-focused messages; use prefixes when helpful.
- PRs should include a brief description, test evidence (commands + results), and
  screenshots for UI changes. Link relevant issues when available.

## Security & Configuration Notes

- AI features require a local `model.bin` in `app/src/main/assets/`; the app falls back
  to a template generator if missing.
- Data is intended to stay on-device; avoid introducing networked processing without
  explicit approval given the repository’s privacy focus.
