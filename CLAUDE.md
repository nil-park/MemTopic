# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MemTopic is an Android app designed to enhance memory retention and presentation skills using TTS and generative AI. The app provides an interactive learning experience where users can create topics, use AI-generated content, and engage with TTS functionality for memory training.

## Commands

### Build and Development

```bash
# Build the Android app
cd android && ./gradlew assembleDebug

# Run tests
cd android && ./gradlew test

# Clean build
cd android && ./gradlew clean

# Install on device/emulator
cd android && ./gradlew installDebug
```

### Testing

```bash
# Run unit tests
cd android && ./gradlew testDebugUnitTest

# Run specific test class
cd android && ./gradlew testDebugUnitTest --tests "com.nolbee.memtopic.utils.ContentParserTest"

# Run instrumented tests
cd android && ./gradlew connectedAndroidTest
```

## Architecture

### Technology Stack

- **Android**: Native Android app using Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Hilt dependency injection
- **Database**: Room with SQLite
- **TTS Integration**: Google Cloud Text-to-Speech API
- **Networking**: Ktor client
- **Build System**: Gradle with Kotlin DSL

### Key Modules

#### Core Components

- `MainActivity.kt`: Single activity hosting Compose navigation
- `MemTopicApp.kt`: Application class with Hilt setup
- `AudioPlayerService`: Foreground service for media playback

#### Data Layer

- **Database**: Room database with DAOs for audio cache and playback
- **Repositories**: `AudioCacheRepository`, `PlaybackRepository`
- **Models**: Topic entities and data classes

#### Features

- **Account Management**: Google Cloud TTS configuration and secure key storage
- **Content Processing**: `ContentParser.kt` handles sentence segmentation using `BreakIterator`
- **Audio Services**: TTS integration with caching and playback controls
- **Navigation**: Compose Navigation with circular navigation support

#### Package Structure

```plaintext
com.nolbee.memtopic/
├── account_view/          # TTS account configuration
├── client/                # TTS client implementation  
├── database/              # Room database and DAOs
├── player/                # Audio playback service
├── ui/                    # Compose UI components
└── utils/                 # Utilities like ContentParser
```

### Development Patterns

- Uses Hilt for dependency injection throughout the app
- Follows Repository pattern for data access
- Compose ViewModels with state management
- Secure credential storage for API keys
- Foreground service for continuous audio playback

## Branch Naming Convention

Follow the established pattern: `{type}/{issue-number}-{brief-description}`

Types: `feature/`, `fix/`, `hotfix/`, `refactor/`, `docs/`, `chore/`, `test/`, `perf/`

Examples:

- `feature/42-add-voice-selection`
- `fix/25-topic-deletion-error`
- `refactor/28-simplify-topic-model`

## Documentation

Development plans are maintained in `docs/development/plans/` with detailed implementation specifications and progress tracking. Each plan includes current status, problems identified, and expected outcomes.

## Key Implementation Notes

### ContentParser

The `ContentParser.kt` utility handles intelligent sentence segmentation using Java's `BreakIterator` with custom tokenization for:

- Quoted text preservation
- Abbreviation handling (Dr., Mr., U.S.A, etc.)
- Decimal number protection
- Multi-language punctuation support
- Paragraph separation by line breaks

### Audio Architecture

- Uses foreground service for uninterrupted playback
- Implements audio caching to reduce API calls
- Supports media-style notifications
- Integrates with Google Cloud TTS with secure credential management

### Build Configuration

- Target SDK: 34, Min SDK: 26
- Kotlin compiler: JVM target 17
- Room version: 2.6.1
- Compose BOM: 2024.05.00
- Hilt version: 2.51.1
