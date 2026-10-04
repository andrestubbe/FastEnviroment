package fastenvironment;

/**
 * Immutable record representing an OS language and culture entity.
 *
 * @param langId     Windows Language Identifier (e.g. 0x0407 for German)
 * @param bcp47Tag   BCP-47 Language Tag (e.g. "de-DE", "en-US")
 * @param name       Native/Display language name or tag
 * @param iso2       Two-letter ISO 639-1 code (e.g. "de", "en")
 */
public record LanguageInfo(int langId, String bcp47Tag, String name, String iso2) {

    public static LanguageInfo of(int langId, String tag) {
        String cleanTag = (tag == null || tag.isBlank()) ? "und" : tag.trim();
        String iso = cleanTag.contains("-") ? cleanTag.substring(0, cleanTag.indexOf('-')) : cleanTag;
        return new LanguageInfo(langId, cleanTag, cleanTag, iso.toLowerCase());
    }

    @Override
    public String toString() {
        return "LanguageInfo[id=0x" + Integer.toHexString(langId).toUpperCase()
                + ", tag=" + bcp47Tag + ", iso2=" + iso2 + "]";
    }
}
