package dev.alexisok.untitledbot.modules.dash;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;

/**
 * Describes error page handlers
 */
final class ErrorHandler {

    private static final String FOUR_ZERO_ONE;
    private static final String FOUR_ZERO_THREE;
    private static final String FOUR_ZERO_FOUR;
    
    static {
        String FOUR_ZERO_ONE_ = "", FOUR_ZERO_THREE_ = "", FOUR_ZERO_FOUR_ = "";
        try {
            FOUR_ZERO_ONE_ = Files.readString(new File("./pub/dash/401.html").toPath());
            FOUR_ZERO_THREE_ = Files.readString(new File("./pub/dash/403.html").toPath());
            FOUR_ZERO_FOUR_ = Files.readString(new File("./pub/dash/404.html").toPath());
        } catch(Throwable e) {
            e.printStackTrace();
            System.exit(9);
        }
        FOUR_ZERO_ONE = FOUR_ZERO_ONE_;
        FOUR_ZERO_THREE = FOUR_ZERO_THREE_;
        FOUR_ZERO_FOUR = FOUR_ZERO_FOUR_;
    }
    
    /**
     * 401 unauthorized.
     */
    @NotNull
    @Contract(pure = true)
    public static String unauthorized() {
        return FOUR_ZERO_ONE;
    }
    
    @NotNull
    @Contract(pure = true)
    public static String serverError() {
        //TODO TEMP
        return "oops";
    }
}
