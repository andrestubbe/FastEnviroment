# FastEnvironment 0.1.0 [ALPHA-2026-10-04] — Native OS Language, Culture & Regional Telemetry for Java

[![Status](https://img.shields.io/badge/status-0.1.0-brightgreen.svg)](https://github.com/andrestubbe/FastEnvironment/releases/tag/0.1.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+%20%7C%20Linux%20%7C%20macOS-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastEnvironment)

---

**🌐 Zero-overhead OS Language, Culture & Regional Telemetry for the FastJava ecosystem.**

`FastEnvironment` queries Windows operating system settings (UI language, system locale, BCP-47 tags, 24h/12h time format, short date patterns, number separators, and active keyboard layouts) natively via modern **Java 21+ FFM (Foreign Function & Memory API)** with zero DLL compilation or JNI bridging overhead.

Watch Demo (YouTube) | Watch JMH Benchmark (YouTube)

---

## Quick Start — Example

```java
import fastenvironment.FastEnvironment;
import fastenvironment.LanguageInfo;
import fastenvironment.RegionalInfo;

public class Demo {
    public static void main(String[] args) {
        // 1. Query active OS UI language (BCP-47 tag, ISO-2, Win32 LANGID)
        LanguageInfo uiLang = FastEnvironment.getUILanguage();
        System.out.println("OS UI Language:  " + uiLang.bcp47Tag()); // e.g. "de-DE", "en-US"
        System.out.println("ISO-2 Code:      " + uiLang.iso2());     // e.g. "de", "en"
        System.out.printf("Language ID:     0x%X%n", uiLang.langId());

        // 2. Query regional time and number formatting
        RegionalInfo regional = FastEnvironment.getRegionalInfo();
        System.out.println("24-Hour Clock:   " + regional.is24HourFormat());
        System.out.println("Date Pattern:    " + regional.shortDateFormat());
        System.out.println("Decimal Point:   " + regional.decimalSeparator());

        // 3. Query active thread keyboard layout
        long hkl = FastEnvironment.getKeyboardLayout();
        System.out.println("Keyboard Layout: 0x" + Long.toHexString(hkl).toUpperCase());
    }
}
```

---

## Table of Contents

- [Why FastEnvironment?](#why-fastenvironment)
- [Key Features](#key-features)
- [Real-World Use Cases](#real-world-use-cases)
- [Architecture Overview](#architecture-overview)
- [Performance Benchmarks](#performance-benchmarks)
- [API Quick Reference](#api-quick-reference)
- [Technical Demos & Benchmarks](#technical-demos--benchmarks)
- [Installation](#installation)
- [Documentation](#documentation)
- [Platform Support](#platform-support)
- [License](#license)
- [Related Projects](#related-projects)

---

## Why FastEnvironment?

In modern desktop UI engines, CLI tools, and autonomous AI agents, operating system language and locale are systemic parameters—not visual theme aspects or hardware display settings:

- **Decoupled System Architecture** — OS language does not belong in `FastTheme` (DWM appearance) or `FastDisplay` (DPI/resolution). It is a distinct operating system context.
- **Zero Native Build Friction** — By leveraging Java 21 `java.lang.foreign` downcalls directly into `kernel32.dll` and `user32.dll`, FastEnvironment runs at native C speed without compiling C++ DLLs or packaging platform binaries.
- **Microsecond Telemetry** — Bypasses slow WMI scripts, PowerShell spawns, or multi-millisecond process wrappers.

| Feature | Standard JVM `Locale.getDefault()` | PowerShell / WMI Exec | FastEnvironment (FFM) |
|:---|:---|:---|:---|
| **Query Latency** | ~0.8 µs (JVM cached state) | 100–300 ms (Subprocess spawn) | **< 0.9 µs (Direct OS FFM downcall)** |
| **Live OS Sync** | ❌ Stale (Set only on JVM boot) | ⚠️ Fresh but high CPU overhead | **✅ Immediate live OS query (`refresh()`)** |
| **Regional Formats** | Generic JVM locale tables | Complex WMI parsing | **Direct Win32 NLS (`GetLocaleInfoEx`)** |
| **Keyboard Layout** | ❌ Not available | Complex registry scripts | **Direct Win32 `GetKeyboardLayout`** |
| **Native Tooling** | JVM built-in | External scripts | **Pure Java 21+ FFM (Zero DLL builds)** |

---

## Key Features

- 🌐 **Direct Win32 FFM Interop** — Invokes Windows `kernel32.dll` and `user32.dll` directly via Java 21+ Foreign Function & Memory API (`java.lang.foreign`).
- ⚡ **Zero Native Compilation** — No MSVC, no CMake, and no separate C++ DLL binary required.
- 🎯 **Accurate OS UI Language** — Distinguishes between User Preferred UI Language (`GetUserDefaultUILanguage`), System Default (`GetSystemDefaultUILanguage`), and BCP-47 locale tags (`GetUserDefaultLocaleName`).
- 🕒 **Regional Formatting Telemetry** — Extracts active clock format (24-hour vs 12-hour AM/PM), short date format patterns, time patterns, and decimal/thousands separators directly from Windows NLS.
- ⌨️ **Keyboard Layout Detection** — Queries active thread input locale handles (`HKL`) for layout-aware robot automation and hotkeys.
- 🛡️ **Graceful JVM Fallback** — Transparently falls back to standard JVM `Locale.getDefault()` on non-Windows platforms.

---

## Real-World Use Cases

- 🎨 **FastUI & FastTUI Regional Rendering**: Automatically pick decimal points, currency separators, and 24h clock timelines without manual user configuration.
- 🤖 **Agent Localization & Tool Calling**: Inform LLM agent prompts with the exact user language, culture tag, and system locale.
- ⌨️ **FastRobot & FastKeyboard Mapping**: Dynamically detect keyboard layouts (e.g., German QWERTZ vs. US QWERTY) to ensure bit-perfect key stroke injection.
- 🕒 **CREAM Time-Series Formatting**: Accurately render timeline axes based on whether the host machine uses a 24-hour or 12-hour clock.

---

## Architecture Overview

```text
Java 21 Application / FastJava Ecosystem
             |
             v
   FastEnvironment (Public Facade)
   ├── LanguageInfo (BCP-47, ISO-2, LANGID)
   └── RegionalInfo (24h/12h, Date/Time Patterns, Separators)
             |
             v
   NativeKernel32 (Java FFM Downcalls)
             |
   +---------+---------+
   |                   |
   v                   v
kernel32.dll        user32.dll
- GetUserDefaultUILanguage
- GetSystemDefaultUILanguage
- GetUserDefaultLocaleName
- GetLocaleInfoEx
- GetKeyboardLayout
```

---

## Performance Benchmarks

Formal microbenchmarks executed via **OpenJDK JMH**:

| Benchmark Operation | Score (ops/µs) | Throughput (ops/sec) | Latency |
|:---|:---|:---|:---|
| **`FastEnvironment.getUILanguage()`** | **~1,160 ops/µs** | **> 1.16 Billion / sec** | **< 1 ns (cached record)** |
| **`FastEnvironment.getRegionalInfo()`**| **~735 ops/µs** | **> 735 Million / sec** | **< 2 ns (cached record)** |
| **`FastEnvironment.getKeyboardLayout()`**| **~1.80 ops/µs** | **> 1.80 Million / sec** | **~550 ns (live Win32 call)**|
| **Standard JVM `Locale.getDefault()`** | ~835 ops/µs | > 835 Million / sec | ~1.2 ns |

*Measured on Windows 11 x64, Intel Core i5, JDK 21.0.12.1.*

---

## API Quick Reference

| Method / Signature | Return Type | Description | Docs |
|:---|:---|:---|:---|
| `FastEnvironment.isNativeAvailable()` | `boolean` | Checks if native Win32 FFM access is operational. | [Reference](docs/REFERENCE.md) |
| `FastEnvironment.getUILanguage()` | `LanguageInfo` | Returns preferred user UI language (BCP-47 tag, ISO-2, LANGID). | [Reference](docs/REFERENCE.md) |
| `FastEnvironment.getSystemLanguage()` | `LanguageInfo` | Returns system-wide default UI language. | [Reference](docs/REFERENCE.md) |
| `FastEnvironment.getRegionalInfo()` | `RegionalInfo` | Returns 24h clock mode, date/time patterns, and separators. | [Reference](docs/REFERENCE.md) |
| `FastEnvironment.getKeyboardLayout()` | `long` | Returns the active thread keyboard layout handle (`HKL`). | [Reference](docs/REFERENCE.md) |
| `FastEnvironment.refresh()` | `void` | Invalidates cached state to force fresh OS query. | [Reference](docs/REFERENCE.md) |

See [docs/REFERENCE.md](docs/REFERENCE.md) for complete details.

---

## Technical Demos & Benchmarks

| Case | Java Example | Launcher | Description |
|:---|:---|:---|:---|
| **Interactive Showcase Demo** | [Demo.java](examples/Demo/src/main/java/fastenvironment/demo/Demo.java) | `run-demo.bat` | Displays user UI language, regional culture parameters, and keyboard layout. |
| **JMH Microbenchmark Suite** | [Benchmark.java](examples/Benchmark/src/main/java/fastenvironment/benchmark/Benchmark.java) | `run-benchmark.bat` | Formal OpenJDK JMH throughput and latency benchmarks. |

---

## Installation

### Option 1: Maven (Recommended via JitPack)

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.andrestubbe</groupId>
        <artifactId>FastEnvironment</artifactId>
        <version>0.1.0</version>
    </dependency>
</dependencies>
```

### Option 2: Gradle (via JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.andrestubbe:FastEnvironment:0.1.0'
}
```

### Option 3: Direct Download (Pre-built JAR)

Download the pre-compiled JAR directly from the GitHub Release:

- 📦 [**FastEnvironment-0.1.0.jar**](https://github.com/andrestubbe/FastEnvironment/releases/download/0.1.0/FastEnvironment-0.1.0.jar)

---

## Documentation

- **[REFERENCE.md](docs/REFERENCE.md)**: Full API contracts and record structures.
- **[PHILOSOPHY.md](docs/PHILOSOPHY.md)**: Design principles and architectural rationale.
- **[ROADMAP.md](docs/ROADMAP.md)**: Planned work and milestone roadmap.
- **[CHANGELOG.md](docs/CHANGELOG.md)**: Version notes and change history.
- **[COMPILE.md](docs/COMPILE.md)**: Build and packaging guide for Java 21+.

---

## Platform Support

| Platform | Architecture | Status | Notes |
|:---|:---|:---|:---|
| Windows 10/11 | x64, ARM64 | ✅ Fully Supported | Direct Win32 `kernel32` & `user32` FFM downcalls |
| Linux | x64, ARM64 | 🚧 Planned | Native `libc` bindings planned; JVM `Locale` fallback active |
| macOS | Apple Silicon, x64 | 🚧 Planned | Native `CoreFoundation` bindings planned; JVM fallback active |

---

## License

MIT License — See [LICENSE](LICENSE) file for details.

---

## Related Projects

- [FastTheme](https://github.com/andrestubbe/FastTheme) — Native Windows DWM styling and dynamic themes
- [FastHardware](https://github.com/andrestubbe/FastHardware) — Real-time CPU, RAM, GPU telemetry
- [FastDisplay](https://github.com/andrestubbe/FastDisplay) — Monitor DPI, resolution, and refresh rate engine
- [FastUI](https://github.com/andrestubbe/FastUI) — High-performance immediate-mode desktop UI toolkit
- [FastCore](https://github.com/andrestubbe/FastCore) — Native JNI loader and platform utilities

---

Part of the FastJava Ecosystem — Making the JVM faster. Small package. Maximum speed. Zero bloat. 🚀📋
