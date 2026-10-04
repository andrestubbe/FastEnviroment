package fastenvironment;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Direct FFM bindings to Windows Win32 kernel32 and user32 APIs.
 * Requires zero native DLL compilation or JNI bridges.
 */
final class NativeKernel32 {

    private static final MethodHandle GET_USER_DEFAULT_UI_LANGUAGE;
    private static final MethodHandle GET_SYSTEM_DEFAULT_UI_LANGUAGE;
    private static final MethodHandle GET_USER_DEFAULT_LOCALE_NAME;
    private static final MethodHandle GET_SYSTEM_DEFAULT_LOCALE_NAME;
    private static final MethodHandle LCID_TO_LOCALE_NAME;
    private static final MethodHandle GET_KEYBOARD_LAYOUT;
    private static final MethodHandle GET_LOCALE_INFO_EX;

    private static final boolean INITIALIZED;

    static {
        boolean ok = false;
        MethodHandle getUserUi = null;
        MethodHandle getSysUi = null;
        MethodHandle getLocaleName = null;
        MethodHandle getSysLocaleName = null;
        MethodHandle lcidToLocale = null;
        MethodHandle getKbdLayout = null;
        MethodHandle getLocaleInfo = null;

        try {
            Linker linker;
            try {
                linker = fastcore.FastCore.getNativeLinker();
            } catch (Throwable ignored) {
                linker = Linker.nativeLinker();
            }
            SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32.dll", Arena.global());
            SymbolLookup user32 = SymbolLookup.libraryLookup("user32.dll", Arena.global());

            // LANGID GetUserDefaultUILanguage()
            MemorySegment symGetUserUi = kernel32.find("GetUserDefaultUILanguage").orElse(null);
            if (symGetUserUi != null) {
                getUserUi = linker.downcallHandle(symGetUserUi, FunctionDescriptor.of(ValueLayout.JAVA_SHORT));
            }

            // LANGID GetSystemDefaultUILanguage()
            MemorySegment symGetSysUi = kernel32.find("GetSystemDefaultUILanguage").orElse(null);
            if (symGetSysUi != null) {
                getSysUi = linker.downcallHandle(symGetSysUi, FunctionDescriptor.of(ValueLayout.JAVA_SHORT));
            }

            // int GetUserDefaultLocaleName(LPWSTR lpLocaleName, int cchLocaleName)
            MemorySegment symGetLocaleName = kernel32.find("GetUserDefaultLocaleName").orElse(null);
            if (symGetLocaleName != null) {
                getLocaleName = linker.downcallHandle(
                        symGetLocaleName,
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
                );
            }

            // int GetSystemDefaultLocaleName(LPWSTR lpLocaleName, int cchLocaleName)
            MemorySegment symGetSysLocaleName = kernel32.find("GetSystemDefaultLocaleName").orElse(null);
            if (symGetSysLocaleName != null) {
                getSysLocaleName = linker.downcallHandle(
                        symGetSysLocaleName,
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
                );
            }

            // int LCIDToLocaleName(LCID Locale, LPWSTR lpName, int cchName, DWORD dwFlags)
            MemorySegment symLcidToLocale = kernel32.find("LCIDToLocaleName").orElse(null);
            if (symLcidToLocale != null) {
                lcidToLocale = linker.downcallHandle(
                        symLcidToLocale,
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT)
                );
            }

            // int GetLocaleInfoEx(LPCWSTR lpLocaleName, LCTYPE LCType, LPWSTR lpLCData, int cchData)
            MemorySegment symGetLocaleInfo = kernel32.find("GetLocaleInfoEx").orElse(null);
            if (symGetLocaleInfo != null) {
                getLocaleInfo = linker.downcallHandle(
                        symGetLocaleInfo,
                        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
                );
            }

            // HKL GetKeyboardLayout(DWORD idThread)
            MemorySegment symGetKbdLayout = user32.find("GetKeyboardLayout").orElse(null);
            if (symGetKbdLayout != null) {
                getKbdLayout = linker.downcallHandle(
                        symGetKbdLayout,
                        FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
                );
            }

            ok = (getUserUi != null && getLocaleName != null);
        } catch (Throwable t) {
            ok = false;
        }

        GET_USER_DEFAULT_UI_LANGUAGE = getUserUi;
        GET_SYSTEM_DEFAULT_UI_LANGUAGE = getSysUi;
        GET_USER_DEFAULT_LOCALE_NAME = getLocaleName;
        GET_SYSTEM_DEFAULT_LOCALE_NAME = getSysLocaleName;
        LCID_TO_LOCALE_NAME = lcidToLocale;
        GET_KEYBOARD_LAYOUT = getKbdLayout;
        GET_LOCALE_INFO_EX = getLocaleInfo;
        INITIALIZED = ok;
    }

    public static boolean isAvailable() {
        return INITIALIZED;
    }

    public static int getUserDefaultUILanguage() {
        if (GET_USER_DEFAULT_UI_LANGUAGE == null) return 0;
        try {
            short id = (short) GET_USER_DEFAULT_UI_LANGUAGE.invokeExact();
            return Short.toUnsignedInt(id);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static int getSystemDefaultUILanguage() {
        if (GET_SYSTEM_DEFAULT_UI_LANGUAGE == null) return 0;
        try {
            short id = (short) GET_SYSTEM_DEFAULT_UI_LANGUAGE.invokeExact();
            return Short.toUnsignedInt(id);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static String getUserDefaultLocaleName() {
        if (GET_USER_DEFAULT_LOCALE_NAME == null) return null;
        try (Arena arena = Arena.ofConfined()) {
            final int maxLen = 85; // LOCALE_NAME_MAX_LENGTH
            MemorySegment buffer = arena.allocateArray(ValueLayout.JAVA_CHAR, maxLen);
            int charsCopied = (int) GET_USER_DEFAULT_LOCALE_NAME.invokeExact(buffer, maxLen);
            if (charsCopied > 1) {
                char[] chars = new char[charsCopied - 1];
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = buffer.getAtIndex(ValueLayout.JAVA_CHAR, i);
                }
                return new String(chars);
            }
        } catch (Throwable t) {
            // fallback
        }
        return null;
    }

    public static String getSystemDefaultLocaleName() {
        if (GET_SYSTEM_DEFAULT_LOCALE_NAME == null) return null;
        try (Arena arena = Arena.ofConfined()) {
            final int maxLen = 85; // LOCALE_NAME_MAX_LENGTH
            MemorySegment buffer = arena.allocateArray(ValueLayout.JAVA_CHAR, maxLen);
            int charsCopied = (int) GET_SYSTEM_DEFAULT_LOCALE_NAME.invokeExact(buffer, maxLen);
            if (charsCopied > 1) {
                char[] chars = new char[charsCopied - 1];
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = buffer.getAtIndex(ValueLayout.JAVA_CHAR, i);
                }
                return new String(chars);
            }
        } catch (Throwable t) {
            // fallback
        }
        return null;
    }

    public static String lcidToLocaleName(int lcid) {
        if (LCID_TO_LOCALE_NAME == null || lcid <= 0) return null;
        try (Arena arena = Arena.ofConfined()) {
            final int maxLen = 85; // LOCALE_NAME_MAX_LENGTH
            MemorySegment buffer = arena.allocateArray(ValueLayout.JAVA_CHAR, maxLen);
            int charsCopied = (int) LCID_TO_LOCALE_NAME.invokeExact(lcid, buffer, maxLen, 0);
            if (charsCopied > 1) {
                char[] chars = new char[charsCopied - 1];
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = buffer.getAtIndex(ValueLayout.JAVA_CHAR, i);
                }
                return new String(chars);
            }
        } catch (Throwable t) {
            // fallback
        }
        return null;
    }

    public static long getKeyboardLayoutId() {
        if (GET_KEYBOARD_LAYOUT == null) return 0;
        try {
            MemorySegment hkl = (MemorySegment) GET_KEYBOARD_LAYOUT.invokeExact(0);
            return hkl.address();
        } catch (Throwable t) {
            return 0;
        }
    }

    public static String getLocaleInfoString(String localeName, int lcType) {
        if (GET_LOCALE_INFO_EX == null) return null;
        try (Arena arena = Arena.ofConfined()) {
            String s = (localeName != null ? localeName : "") + "\0";
            MemorySegment locStr = arena.allocateArray(ValueLayout.JAVA_CHAR, s.toCharArray());
            final int maxLen = 128;
            MemorySegment buffer = arena.allocateArray(ValueLayout.JAVA_CHAR, maxLen);
            int charsCopied = (int) GET_LOCALE_INFO_EX.invokeExact(locStr, lcType, buffer, maxLen);
            if (charsCopied > 1) {
                char[] chars = new char[charsCopied - 1];
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = buffer.getAtIndex(ValueLayout.JAVA_CHAR, i);
                }
                return new String(chars);
            }
        } catch (Throwable t) {
            // fallback
        }
        return null;
    }
}
