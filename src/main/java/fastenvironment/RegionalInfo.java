package fastenvironment;

/**
 * Immutable record representing OS regional formatting settings.
 *
 * @param is24HourFormat  true if OS uses 24-hour clock, false for 12-hour (AM/PM)
 * @param shortDateFormat OS short date pattern (e.g. "dd.MM.yyyy" or "M/d/yyyy")
 * @param timeFormat      OS time pattern (e.g. "HH:mm:ss")
 * @param decimalSeparator Decimal point symbol (e.g. "," or ".")
 * @param thousandSeparator Thousands grouping symbol (e.g. "." or ",")
 * @param calendarType      OS calendar identifier (1 = Gregorian, etc.)
 */
public record RegionalInfo(
        boolean is24HourFormat,
        String shortDateFormat,
        String timeFormat,
        String decimalSeparator,
        String thousandSeparator,
        int calendarType
) {}
