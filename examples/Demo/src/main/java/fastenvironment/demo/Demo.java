package fastenvironment.demo;

import fastenvironment.FastEnvironment;
import fastenvironment.LanguageInfo;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== FastEnvironment Demo ===");
        System.out.println("Native Available: " + FastEnvironment.isNativeAvailable());

        LanguageInfo ui = FastEnvironment.getUILanguage();
        System.out.println("User UI Language:   " + ui);
        System.out.println("  BCP-47 Tag:       " + ui.bcp47Tag());
        System.out.println("  ISO-2 Code:       " + ui.iso2());
        System.out.println("  Language ID:      0x" + Integer.toHexString(ui.langId()).toUpperCase());

        LanguageInfo sys = FastEnvironment.getSystemLanguage();
        System.out.println("System UI Language: " + sys);
    }
}
