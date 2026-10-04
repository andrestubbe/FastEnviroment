# FastEnvironment 0.1.0 — Native Environment, Locale & Language API for Java

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.java.com)
[![Platform](https://img.shields.io/badge/Platform-Windows%2010+-lightgrey.svg)]()
[![JitPack](https://img.shields.io/badge/JitPack-ready-green.svg)](https://jitpack.io/#andrestubbe/FastEnvironment)

---

**🌐 Zero-overhead OS Language, Culture & Regional Telemetry for the FastJava ecosystem.**

`FastEnvironment` queries Windows operating system settings (UI language, system locale, BCP-47 tags) natively via modern **Java 21+ FFM (Foreign Function & Memory API)** with zero DLL compilation or JNI bridging overhead.

---

## Features

- **Direct Win32 Interop**: Invokes `kernel32.dll` directly from pure Java via modern `java.lang.foreign`.
- **Zero Native Build Dependencies**: No C++ compiler, CMake, or DLL packaging required.
- **Microsecond Telemetry**: Instantly detects user UI culture, system language, and BCP-47 locale tags.
- **Fail-Safe Fallbacks**: Automatically falls back to standard JVM `Locale` when invoked on non-Windows environments.

---

## Quick Start

```java
import fastenvironment.FastEnvironment;
import fastenvironment.LanguageInfo;

public class App {
    public static void main(String[] args) {
        // Query active OS UI language
        LanguageInfo uiLang = FastEnvironment.getUILanguage();

        System.out.println("BCP-47 Tag:  " + uiLang.bcp47Tag()); // e.g. "de-DE", "en-US"
        System.out.println("ISO-2 Code:  " + uiLang.iso2());     // e.g. "de", "en"
        System.out.printf("Language ID: 0x%X%n", uiLang.langId());
    }
}
```

---

## Maven Dependency (JitPack)

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

---

## Architecture

```
FastEnvironment/
├── pom.xml                 # Java 21 compiler configuration
├── docs/                   # Documentation and architecture guides
└── src/main/java/fastenvironment/
    ├── FastEnvironment.java    # Public static facade
    ├── LanguageInfo.java       # Immutable domain record
    └── NativeKernel32.java     # Zero-overhead Win32 FFM linker
```

---

## License

MIT License. Copyright (c) Andre Stubbe.
