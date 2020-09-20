package dev.alexisok.untitledbot.modules.rpg;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.CheckReturnValue;
import java.io.File;

/**
 * Draws images for the RPG command profile, rank, level, etc..
 * 
 * @see dev.alexisok.untitledbot.modules.rank.RankImageRender similar class.
 * @author AlexIsOK
 * @since 1.4.0
 */
final class RPGProfileDrawer {
    
    //where %s is the id of the message.
    private static final String TMP_STORAGE_LOCATION = "./tmp/rpg/%s.png";
    
    /**
     * Render the image and return the location of the File.
     * 
     * The returned File will be stored as a png, and it is strongly
     * encouraged that the file is deleted after it is sent to Discord
     * to preserve disk space.
     * 
     * @param userID the Discord ID of the user as a String.
     * @param guildID the Discord ID of the guild as a String.
     * @param randomID the random ID that will be used to store the image,
     *                 usually the ID of the message that procured the command.
     * @return the file where the image is stored.
     */
    @NotNull
    @CheckReturnValue
    @Contract(pure = true)
    protected static File render(String userID, String guildID, String randomID) {
        return null;
    }
    
}
