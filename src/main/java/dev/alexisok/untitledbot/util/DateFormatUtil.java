package dev.alexisok.untitledbot.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class DateFormatUtil {
    
    private DateFormatUtil() {}
    
    private static final DateTimeFormatter f = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    /**
     * Format a date
     * @param d the date
     * @return the formatted string
     */
    public static synchronized String format(Date d) {
        return f.format(d.toInstant());
    }
    
    public static synchronized String format(OffsetDateTime odt) {
        return f.format(odt);
    }
    
}
