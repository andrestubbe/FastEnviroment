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

    private static final boolean INITIALIZED;

    static {
        boolean ok = false;
        MethodHandle getUserUi = null;
        MethodHandle getSysUi = null;
        MethodHandle getLocaleName = null;

        try {
            Linker linker = Linker.nativeLinker();
            SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32.dll", Arena.global());

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

            ok = (getUserUi != null && getLocaleName != null);
        } catch (Throwable t) {
            ok = false;
        }

        GET_USER_DEFAULT_UI_LANGUAGE = getUserUi;
        GET_SYSTEM_DEFAULT_UI_LANGUAGE = getSysUi;
        GET_USER_DEFAULT_LOCALE_NAME = getLocaleName;
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
                // charsCopied includes null terminator
                char[] chars = new char[charsCopied - 1];
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = buffer.getAtIndex(ValueLayout.JAVA_CHAR, i);
                }
                return new String(chars);
            }
        } catch (Throwable t) {
            // ignore fallback
        }
        return null;
    }
}
