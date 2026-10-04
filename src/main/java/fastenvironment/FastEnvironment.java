package fastenvironment;

import java.util.Locale;

/**
 * FastEnvironment — Zero-overhead OS Language, Locale &amp; Regional Telemetry for FastJava.
 *
 * <p>Directly queries Windows 10/11 operating system parameters via modern Java FFM
 * without native DLL overhead or JNI compilation steps.</p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * LanguageInfo uiLang = FastEnvironment.getUILanguage();
 * System.out.println("OS UI Language: " + uiLang.bcp47Tag()); // "de-DE"
 * System.out.println("ISO-2: " + uiLang.iso2());              // "de"
 * }</pre>
 *
 * @author Andre Stubbe
 * @version 0.1.0
 */
public final class FastEnvironment {

    // LCTYPE constants from WinNls.h
    private static final int LOCALE_ITIME = 0x00000023; // 0 = 12-hour, 1 = 24-hour
    private static final int LOCALE_SSHORTDATE = 0x0000001F; // short date format string
    private static final int LOCALE_STIMEFORMAT = 0x00001003; // time format string
    private static final int LOCALE_SDECIMAL = 0x0000000E; // decimal separator
    private static final int LOCALE_STHOUSAND = 0x0000000F; // thousand separator
    private static final int LOCALE_ICALENDARTYPE = 0x00001009; // 1 = Gregorian, etc.

    private static volatile LanguageInfo cachedUILanguage;
    private static volatile LanguageInfo cachedSystemLanguage;
    private static volatile RegionalInfo cachedRegionalInfo;

    private FastEnvironment() {}

    /**
     * Checks whether native Win32 FFM access is operational on this host.
     *
     * @return {@code true} if running on Windows with Win32 FFM loaded.
     */
    public static boolean isNativeAvailable() {
        return NativeKernel32.isAvailable();
    }

    /**
     * Returns the active OS UI language preferred by the current user.
     *
     * @return current {@link LanguageInfo}
     */
    public static LanguageInfo getUILanguage() {
        if (cachedUILanguage != null) {
            return cachedUILanguage;
        }

        if (NativeKernel32.isAvailable()) {
            int langId = NativeKernel32.getUserDefaultUILanguage();
            String localeName = NativeKernel32.getUserDefaultLocaleName();
            if (localeName != null && !localeName.isBlank()) {
                cachedUILanguage = LanguageInfo.of(langId, localeName);
                return cachedUILanguage;
            }
        }

        // JVM Locale Fallback
        Locale def = Locale.getDefault();
        cachedUILanguage = LanguageInfo.of(0, def.toLanguageTag());
        return cachedUILanguage;
    }

    /**
     * Returns the system-wide default UI language.
     *
     * @return system {@link LanguageInfo}
     */
    public static LanguageInfo getSystemLanguage() {
        if (cachedSystemLanguage != null) {
            return cachedSystemLanguage;
        }

        if (NativeKernel32.isAvailable()) {
            int langId = NativeKernel32.getSystemDefaultUILanguage();
            String localeName = NativeKernel32.getSystemDefaultLocaleName();
            if (localeName != null && !localeName.isBlank()) {
                cachedSystemLanguage = LanguageInfo.of(langId, localeName);
                return cachedSystemLanguage;
            }
        }

        Locale def = Locale.getDefault();
        cachedSystemLanguage = LanguageInfo.of(0, def.toLanguageTag());
        return cachedSystemLanguage;
    }

    /**
     * Returns active OS regional formats (date, time 12h/24h, number separators).
     *
     * @return active {@link RegionalInfo}
     */
    public static RegionalInfo getRegionalInfo() {
        if (cachedRegionalInfo != null) {
            return cachedRegionalInfo;
        }

        if (NativeKernel32.isAvailable()) {
            String loc = NativeKernel32.getUserDefaultLocaleName();
            String timeMode = NativeKernel32.getLocaleInfoString(loc, LOCALE_ITIME);
            boolean is24Hour = "1".equals(timeMode);
            String dateFormat = NativeKernel32.getLocaleInfoString(loc, LOCALE_SSHORTDATE);
            String timeFormat = NativeKernel32.getLocaleInfoString(loc, LOCALE_STIMEFORMAT);
            String decimalSep = NativeKernel32.getLocaleInfoString(loc, LOCALE_SDECIMAL);
            String thousandSep = NativeKernel32.getLocaleInfoString(loc, LOCALE_STHOUSAND);
            String calStr = NativeKernel32.getLocaleInfoString(loc, LOCALE_ICALENDARTYPE);
            int calType = 1;
            if (calStr != null) {
                try {
                    calType = Integer.parseInt(calStr.trim());
                } catch (NumberFormatException ignored) {}
            }

            cachedRegionalInfo = new RegionalInfo(
                    is24Hour,
                    dateFormat != null ? dateFormat : "yyyy-MM-dd",
                    timeFormat != null ? timeFormat : "HH:mm:ss",
                    decimalSep != null ? decimalSep : ",",
                    thousandSep != null ? thousandSep : ".",
                    calType
            );
            return cachedRegionalInfo;
        }

        cachedRegionalInfo = new RegionalInfo(true, "yyyy-MM-dd", "HH:mm:ss", ",", ".", 1);
        return cachedRegionalInfo;
    }

    /**
     * Returns the active user keyboard layout handle (HKL).
     *
     * <p>The low-order word contains the Language Identifier (LANGID),
     * and the high-order word contains a device handle to the physical layout.</p>
     *
     * @return 64-bit native HKL pointer value, or 0 if unavailable.
     */
    public static long getKeyboardLayout() {
        if (NativeKernel32.isAvailable()) {
            return NativeKernel32.getKeyboardLayoutId();
        }
        return 0;
    }

    /**
     * Clears internal cached values in case OS preferences were dynamically changed.
     */
    public static void refresh() {
        cachedUILanguage = null;
        cachedSystemLanguage = null;
        cachedRegionalInfo = null;
    }
}
