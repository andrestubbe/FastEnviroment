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

    private static volatile LanguageInfo cachedUILanguage;
    private static volatile LanguageInfo cachedSystemLanguage;

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
            String localeName = NativeKernel32.getUserDefaultLocaleName();
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
     * Clears internal cached values in case OS preferences were dynamically changed.
     */
    public static void refresh() {
        cachedUILanguage = null;
        cachedSystemLanguage = null;
    }
}
