# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-12-31

### Added
- Git submodule `script-helpers` for local script utilities.
- Automation scripts for project management:
  - `./update`: Initialize and update git submodules.
  - `./build`: Local Android build script.
  - `./start`: Dockerized Android build for dependency-free environment.
  - `./test`: Dockerized test runner.
- Comprehensive documentation in `./docs`:
  - `setup.md`: Environment setup and prerequisites.
  - `build.md`: Build instructions for local and Docker environments.
  - `usage.md`: Guide on how to use automation scripts.
  - `ci.md`: Continuous Integration guide.
- This `CHANGELOG.md` file to track project history.

[0.1.0]: https://github.com/nikolareljin/denial-shield/compare/v0.0.0...v0.1.0
