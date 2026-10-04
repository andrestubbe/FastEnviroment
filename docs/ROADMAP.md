# FastEnvironment Roadmap

## Milestone Status

### Core OS Language & Regional Telemetry (v0.1.0)
**Status:** Released
- [x] Java 21+ FFM downcalls into Windows `kernel32.dll` and `user32.dll`.
- [x] Query user preferred UI language (`GetUserDefaultUILanguage`).
- [x] Query system-wide default UI language (`GetSystemDefaultUILanguage`).
- [x] Query BCP-47 locale tag (`GetUserDefaultLocaleName`).
- [x] Query regional time format (24-hour vs 12-hour AM/PM clock detection).
- [x] Query short date pattern and time format strings (`GetLocaleInfoEx`).
- [x] Query decimal and thousand separator symbols (`LOCALE_SDECIMAL`, `LOCALE_STHOUSAND`).
- [x] Query active thread keyboard layout handle (`GetKeyboardLayout`).
- [x] Standalone interactive CLI demonstration (`run-demo.bat`).
- [x] Full OpenJDK JMH microbenchmark harness (`run-benchmark.bat`).

---

## Upcoming Features

### Dynamic Environment Variables & Registry Bridge (v0.2.0)
**Status:** Planned
- [ ] Direct Win32 process environment block inspection.
- [ ] Safe `SetEnvironmentVariableW` integration without JVM process restart.
- [ ] Query user and system persistent environment variables from Windows Registry (`HKCU\Environment`, `HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment`).
- [ ] Broadcast `WM_SETTINGCHANGE` notifications (`HWND_BROADCAST`) to inform desktop applications of updated environment paths.

### Cross-Platform Linux & macOS FFM Downcalls (v0.3.0)
**Status:** Backlog
- [ ] Bind to `libc.so.6` `setlocale` / `nl_langinfo` on Linux.
- [ ] Bind to `libSystem.B.dylib` CoreFoundation locale primitives on macOS.
