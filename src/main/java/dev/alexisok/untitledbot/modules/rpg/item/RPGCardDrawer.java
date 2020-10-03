package dev.alexisok.untitledbot.modules.rpg.item;

import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.rpg.RPGUser;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Renders card images that are displayed to the users.
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
class RPGCardDrawer {
    
    private static final String STORE_DIRECTORY = "./cards/rendered/";
    
    static {
        if(!new File(STORE_DIRECTORY).mkdirs() || !new File("./cards/media/").mkdirs())
            Logger.log("The directories for the store directory " + STORE_DIRECTORY + " returned false.\n" +
                               "If the directory already exists, ignore this, otherwise make the permissions better.");
    }
    
    protected static void renderCards() {
        ArrayList<RPGItem> itemList = RPGItem.getITEMS();
        
        itemList.forEach(RPGCardDrawer::drawAndSaveCard);
        
    }
    
    private static void drawAndSaveCard(@NotNull RPGItem item) {
        BufferedImage bi = new BufferedImage(450, 690, BufferedImage.TYPE_INT_RGB);
    
        Graphics2D gtd = bi.createGraphics();
        
        try {
            gtd.drawImage(ImageIO.read(new File("./cards/rarity/backgrounds/" + item.getRarity().name() + ".png")), 0, 0, null);
            gtd.drawImage(ImageIO.read(new File("./cards/media/" + item.getImageLocation() + ".png")), 0, 0, null);
        } catch(IOException ignored) {
            Logger.critical("Image for " + item + " was not registered!!!", 0, false);
        }
    }
    
}
