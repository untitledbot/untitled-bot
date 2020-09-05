package dev.alexisok.untitledbot.util;

import org.apache.commons.lang.StringUtils;

/**
 * Converts seconds to a human readable time
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class SecondsToReadable {
    
    /**
     * Convert the time in seconds to a human-readable format
     * @param time the time in seconds
     * @return the human readable format
     */
    @SuppressWarnings("UnusedAssignment")
    public static String convert(long time) {
        long days    = 0;
        long hours   = 0;
        long minutes = 0;
        long seconds = 0;
        
        while(time >= 86400) {
            time = time - 86400;
            days++;
        }
        while(time >= 3600) {
            time = time - 3600;
            hours++;
        }
        while(time >= 60) {
            time = time - 60;
            minutes++;
        }
        seconds = time;
        
        String returnString = "";
        
        if(days != 0)
            returnString += days + " day" + (days == 1 ? "" : "s");
        if(hours != 0) {
            if (returnString.equals(""))
                returnString += hours + " hour" + (hours == 1 ? "" : "s");
            else
                returnString += ", " + ((StringUtils.countMatches(returnString, ",") >= 2 && (seconds == 0 && minutes == 0) ? "" : "and ")) + hours + " hour" + (hours == 1 ? "" : "s");
        }
        if(minutes != 0) {
            if (returnString.equals(""))
                returnString += minutes + " minute" + (minutes == 1 ? "" : "s");
            else
                returnString += ", " + (StringUtils.countMatches(returnString, ",") >= 2 && seconds == 0 ? "" : "and ") + minutes + " minute" + (minutes == 1 ? "" : "s");
        }
        if(seconds != 0) {
            if (returnString.equals(""))
                returnString += seconds + " second" + (seconds == 1 ? "" : "s");
            else
                returnString += ", " + ((StringUtils.countMatches(returnString, ",") >= 2) ? "" : "and ") + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        
        return returnString;
    
    }
    
}
