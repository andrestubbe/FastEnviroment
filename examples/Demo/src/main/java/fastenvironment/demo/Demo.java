package fastenvironment.demo;

import fastenvironment.FastEnvironment;
import fastenvironment.LanguageInfo;
import fastenvironment.RegionalInfo;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   FastEnvironment OS Telemetry Showcase Demo");
        System.out.println("=================================================");
        System.out.println("Native Win32 FFM Available: " + FastEnvironment.isNativeAvailable());
        System.out.println();

        // 1. User Preferred UI Language
        LanguageInfo ui = FastEnvironment.getUILanguage();
        System.out.println("[User UI Language]");
        System.out.println("  BCP-47 Tag:       " + ui.bcp47Tag());
        System.out.println("  ISO-2 Code:       " + ui.iso2());
        System.out.println("  Language ID:      0x" + Integer.toHexString(ui.langId()).toUpperCase());
        System.out.println();

        // 2. System UI Language
        LanguageInfo sys = FastEnvironment.getSystemLanguage();
        System.out.println("[System UI Language]");
        System.out.println("  BCP-47 Tag:       " + sys.bcp47Tag());
        System.out.println("  ISO-2 Code:       " + sys.iso2());
        System.out.println("  Language ID:      0x" + Integer.toHexString(sys.langId()).toUpperCase());
        System.out.println();

        // 3. Regional Formats
        RegionalInfo reg = FastEnvironment.getRegionalInfo();
        System.out.println("[Regional & Culture Formatting]");
        System.out.println("  24-Hour Clock:    " + reg.is24HourFormat());
        System.out.println("  Short Date Format: " + reg.shortDateFormat());
        System.out.println("  Time Format:      " + reg.timeFormat());
        System.out.println("  Decimal Separator: '" + reg.decimalSeparator() + "'");
        System.out.println("  Thousand Separator:'" + reg.thousandSeparator() + "'");
        System.out.println("  Calendar Type:     " + reg.calendarType() + " (1 = Gregorian)");
        System.out.println();

        // 4. Keyboard Layout
        long kbd = FastEnvironment.getKeyboardLayout();
        System.out.println("[Keyboard Layout]");
        System.out.println("  Active HKL:       0x" + Long.toHexString(kbd).toUpperCase());
        System.out.println();
        System.out.println("[OK] FastEnvironment demonstration completed.");
    }
}
