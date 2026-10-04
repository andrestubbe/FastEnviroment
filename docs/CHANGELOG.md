# Changelog: FastEnvironment

All notable changes to this project will be documented in this file.

## [0.1.0] - 2026-10-04
### Added
- **Native Java 21+ FFM Substrate**: Direct downcall linkage into Win32 `kernel32.dll` and `user32.dll` via `java.lang.foreign` with zero C++ compilation or native DLL packaging.
- **Operating System Language Detection**: Query user preferred UI language (`getUILanguage()`) and system-wide UI language (`getSystemLanguage()`) returning BCP-47 tags (e.g. `de-DE`, `en-US`), ISO-2 codes (`de`, `en`), and native `LANGID`s.
- **Regional Formatting Telemetry**: Query active OS regional culture parameters (`getRegionalInfo()`), including 24-hour vs 12-hour clock modes, short date patterns, time patterns, decimal/thousands separators, and calendar type (`LOCALE_ICALENDARTYPE`) via `GetLocaleInfoEx`.
- **Keyboard Layout Inspection**: Added `getKeyboardLayout()` querying active thread input locale handles (`HKL`).
- **Interactive Showcase & Benchmarks**: Added interactive launcher `run-demo.bat` and formal JMH microbenchmark suite `run-benchmark.bat` demonstrating nanosecond cached throughput (>1,000,000,000 ops/sec).
