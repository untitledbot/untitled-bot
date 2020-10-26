package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckReturnValue;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * Render an image for the rank card.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class RankImageRender {
    
    private RankImageRender(){}
    
    static {
        Logger.log("Created tmp rank directory?  " + new File("./tmp/rank/").mkdirs());
        Logger.log("If this is false, it probably means the directory already exists.  If the directory doesn't exist, make sure" +
                           " that the bot has write permissions in the current directory.");
        for(String fn : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
            Logger.debug("Font " + fn + " is registered.");
        }
        Logger.debug("Done listing fonts.");
        
    }
    
    /**
     * Render the image.
     * 
     * It is highly encouraged that the file is deleted after it is sent to Discord, either by
     * the program or by a cron.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @param uniqueID a unique ID to give the file, usually the ID of the message.  Prevents collision.
     * @return the file where it is stored, or {@code null} if it could not be rendered.
     * @throws UserDataCouldNotBeObtainedException if the user data could not be obtained.
     */
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static File render(String userID, String guildID, long uniqueID) throws UserDataCouldNotBeObtainedException, IOException {
        
        User u = Main.jda.getUserById(userID);
        
        //current and needed xp
        long current;
        long maximum;
        
        //user level
        int rank;
        
        //balance as a number
        long balance;
        
        //amount of money the user has in their bank
        long bankBal;
        
        //name and discriminator
        String name;
        String discriminator;
        
        //the balance with commas
        String balanceAsDisplay;
        
        String bankBalAsDisplay;
        
        //set all the needed stuff.
        try {
            current = Long.parseLong(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, "ranks-xp")));
            rank = Integer.parseInt(Objects.requireNonNull(Vault.getUserDataLocal(userID, guildID, "ranks-level")));
            maximum = Ranks.xpNeededForLevel(rank);
            name = Objects.requireNonNull(u).getName();
            discriminator = u.getDiscriminator();
            String balStr = Vault.getUserDataLocal(userID, guildID, Shop.CURRENCY_VAULT_NAME);
            balance = Long.parseLong(balStr != null ? balStr : "0");
            balanceAsDisplay = new DecimalFormat("#,###").format(balance);
            String bankStr = Vault.getUserDataLocal(userID, guildID, Shop.BANK_VAULT_NAME);
            bankBal = Long.parseLong(bankStr != null ? bankStr : "0");
            bankBalAsDisplay = new DecimalFormat("#,###").format(bankBal);
        } catch(NullPointerException ignored) {
            return null;
        }
        
        //shorten the string up a bit if it's over 1k
        String currentAsDisplay = Top.getBetterNameOtherThanJustWhateverIdk(current);
        
        String maximumAsDisplay;
        
        //shorten the string up a bit if it's over 1k
        if(maximum != -1)
            maximumAsDisplay = Top.getBetterNameOtherThanJustWhateverIdk(maximum);
        else
            maximumAsDisplay = "\u221E"; //infinity symbol
        
        //this is where the magic happens
        int width = 800;
        int height = 300;
        
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D gtd = bi.createGraphics();
        
        
        try {
            //if the user is a contributor, add their image here.
            gtd.drawImage((ImageIO.read(new File("rs/" + userID + ".png"))), 0, 0, null);
        } catch(Exception ignored) {
            //non-contributors get a default background
            gtd.drawImage((ImageIO.read(new File("rs/default.png"))), 0, 0, null);
        }
        
        //username and discriminator
        String font = "Ubuntu";
        gtd.setFont(new Font(font, Font.PLAIN, 36));
        gtd.setColor(Color.WHITE);
        gtd.drawString("" + name + "#" + discriminator, 30, 50);
        
        //balance
        gtd.setFont(new Font(font, Font.PLAIN, 26));
        gtd.drawString("Balance: UB$" + balanceAsDisplay, 30, 80);
        gtd.drawString("In Bank: UB$" + bankBalAsDisplay, 30, 120);
        
        //level numbers (x / y XP)
        gtd.drawString(String.format("%s / %s XP", currentAsDisplay, maximumAsDisplay), 30, 200);
        gtd.drawString(String.format("%sLevel %d%s", rank != 100 ? "    " : "", rank, rank == 100 ? " (MAX)" : ""), 555, 200);
        
        //progressbar for level (outline)
        gtd.setColor(Color.GRAY);
        gtd.fillRoundRect(30, 220, 700, 32, 32, 32);
        
        //fill width double
        double fillWD = (((double) current / (double) (maximum)));
        
        //fill width pixels
        int fillW = (int) ((700.0) * (fillWD));
        
        //fill the progress bar
        gtd.setColor(Color.GREEN);
        gtd.fillRoundRect(30, 220, fillW < 20 ? 0 : fillW, 32, 32, 32);
        
        new FileOutputStream("./tmp/" + u.getId() + ".png").getChannel().transferFrom(Channels.newChannel(new URL(u.getEffectiveAvatarUrl()).openStream()), 0, Long.MAX_VALUE);
        
        gtd.drawImage((ImageIO.read(new File("./tmp/" + u.getId() + ".png"))).getScaledInstance(128, 128, Image.SCALE_FAST), 660, 10, null);
        
        new File("./tmp/" + u.getId() + ".png").delete();
        
        //save the image.
        try {
            ImageIO.write(bi, "png", new File(String.format("./tmp/rank/%d.png", uniqueID)));
            return new File(String.format("./tmp/rank/%d.png", uniqueID));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
