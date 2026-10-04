# FastEnvironment Documentation

## Overview
`FastEnvironment` provides zero-overhead OS telemetry (user UI language, system UI language, BCP-47 locale tags) for Java 21+ applications using direct Win32 FFM downcalls.

## API Reference
- `FastEnvironment.isNativeAvailable()`: Returns true if running on Windows and FFM symbols are resolved.
- `FastEnvironment.getUILanguage()`: Returns a `LanguageInfo` record containing BCP-47 tag, ISO-2 language code, and native Win32 `LANGID`.
- `FastEnvironment.getSystemLanguage()`: Returns system-wide UI language information.
- `FastEnvironment.refresh()`: Invalidates cached values.
