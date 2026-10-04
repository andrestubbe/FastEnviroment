# FastEnvironment API Reference Manual

`FastEnvironment` provides ultra-fast, zero-overhead OS telemetry (User Preferred UI Language, System Language, BCP-47 locale tags, Regional Formatting, and Active Keyboard Layout) for Java 21+ using direct Win32 FFM (Foreign Function & Memory API) downcalls without external DLL or JNI build dependencies.

---

## 1. Class: `fastenvironment.FastEnvironment`

Central facade providing static accessors and telemetry caches.

### Methods

| Method | Return Type | Description |
|:---|:---|:---|
| `isNativeAvailable()` | `boolean` | Verifies whether native Win32 `kernel32.dll` and `user32.dll` FFM downcalls are operational on this machine. |
| `getUILanguage()` | `LanguageInfo` | Returns the active OS UI language preferred by the current user (e.g. `de-DE`, `en-US`). |
| `getSystemLanguage()` | `LanguageInfo` | Returns the system-wide default UI language. |
| `getRegionalInfo()` | `RegionalInfo` | Returns active regional settings: 24h clock mode, date format pattern, time pattern, decimal/thousand separators, and calendar type. |
| `getKeyboardLayout()` | `long` | Returns the active thread's keyboard layout handle (`HKL` / `LANGID`). |
| `refresh()` | `void` | Invalidates cached records to query live OS settings afresh. |

---

## 2. Record: `fastenvironment.LanguageInfo`

Immutable record representing an operating system language entity.

```java
public record LanguageInfo(int langId, String bcp47Tag, String name, String iso2)
```

- `langId`: Native Win32 Language Identifier (e.g., `0x0407` for German, `0x0409` for US English).
- `bcp47Tag`: BCP-47 compliant tag (e.g., `"de-DE"`, `"en-US"`).
- `name`: Display / tag name representation.
- `iso2`: Two-letter ISO 639-1 language abbreviation (e.g., `"de"`, `"en"`).

---

## 3. Record: `fastenvironment.RegionalInfo`

Immutable record containing user regional and culture preferences.

```java
public record RegionalInfo(
    boolean is24HourFormat,
    String shortDateFormat,
    String timeFormat,
    String decimalSeparator,
    String thousandSeparator,
    int calendarType
)
```

- `is24HourFormat`: `true` if OS uses a 24-hour clock, `false` for 12-hour AM/PM.
- `shortDateFormat`: Short date pattern string from Windows NLS (e.g. `"dd.MM.yyyy"`).
- `timeFormat`: Time pattern string (e.g. `"HH:mm:ss"`).
- `decimalSeparator`: Decimal delimiter symbol (e.g. `","` or `"."`).
- `thousandSeparator`: Thousands grouping symbol (e.g. `"."` or `","`).
- `calendarType`: Windows NLS Calendar identifier (`LOCALE_ICALENDARTYPE`, e.g. `1` = Gregorian, `2` = Gregorian (US English), `3` = Japan Emperor Era, `6` = Hijri, `7` = Hebrew).
