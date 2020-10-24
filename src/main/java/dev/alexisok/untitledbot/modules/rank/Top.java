package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static dev.alexisok.untitledbot.modules.rank.xpcommands.Shop.*;
import static dev.alexisok.untitledbot.modules.rank.xpcommands.Shop.BANK_VAULT_NAME;
import static dev.alexisok.untitledbot.modules.vault.Vault.getUserDataLocalOrDefault;
import static java.lang.Long.*;

/**
 * Gets the users in the guild with the most XP.
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Top extends UBPlugin {
    
    //10 minutes due to high cpu usage
    private static final long TIME_BETWEEN_COMMAND_IN_SECONDS = 600;
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(isRateLimit(message.getGuild().getId())) {
            eb.setColor(Color.RED);
            eb.addField("Rate limited!", String.format("To reduce server load, this command can only be run every %d minutes.",
                    (int) (TIME_BETWEEN_COMMAND_IN_SECONDS / 60)),
                    false);
            return eb.build();
        }
        
        LinkedHashMap<String, Long> topXP = new LinkedHashMap<>(new LinkedHashMap<>());
        
        for(File s : new File(Main.DATA_PATH + "/" + message.getGuild().getId() + "/").listFiles()) {
            try {
                
                long top = Ranks.totalXPFromAllLevels(s.getName().replace(".properties", ""), message.getGuild().getId());
                if (top == 0)
                    continue;
    
                topXP.put(s.getName().replace(".properties", ""), top);
                
            } catch(Exception ignored) {}
        }
        
        topXP = sortHashMap(topXP);
        try {
            //the rendered image
            File imageToSend = Objects.requireNonNull(render(topXP, message.getIdLong(), message.getGuild().getName(), message.getGuild().getId()));
            //send and then delete the image when it has sent
            message.getChannel().sendFile(imageToSend).queue(done -> imageToSend.delete());
            setRateLimiter(message.getGuild().getId());
        } catch(IOException | NullPointerException ignored) {
            message.getChannel().sendMessage("Could not send the rank top!").queue();
        }
        
        return null;
    }
    
    /**
     * Sort the hashmap and return a {@link LinkedHashMap}
     * @param hm unsorted hashmap
     * @return the sorted hashmap
     */
    @NotNull
    @Contract(pure = true)
    private static LinkedHashMap<String, Long> sortHashMap(@NotNull HashMap<String, Long> hm) {
        List<Map.Entry<String, Long>> list = new ArrayList<>(hm.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        return list.stream()
                            .collect(
                                Collectors.toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (a, b) -> b,
                                    LinkedHashMap::new
                            ));
    }
    
    /**
     * 
     * @param guildID the ID of the guild.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String guildID) {
        //testing server
        if(guildID.equals("696529468247769149"))
            return false;
        String epochOldString = Vault.getUserDataLocal(null, guildID, "top.ratelimit");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
    
        return epochCurrent - epochPrevious <= TIME_BETWEEN_COMMAND_IN_SECONDS;
    }
    
    private static void setRateLimiter(String guildID) {
        Vault.storeUserDataLocal(null, guildID, "top.ratelimit", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    @Nullable
    @CheckReturnValue
    @Contract(pure = true)
    public static File render(@NotNull LinkedHashMap<String, Long> map, long uniqueID, @NotNull String guildName, @NotNull String guildID) throws UserDataCouldNotBeObtainedException, IOException {
        
        final int IMAGE_WIDTH  =  800;
        final int IMAGE_HEIGHT = 1200;
        
        BufferedImage bi = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D gtd = bi.createGraphics();
        
        gtd.drawImage(ImageIO.read(new File("./rs/top_bg.png")), 0, 0, null);
        
        //username and discriminator
        String font = "Serif";
        gtd.setFont(new Font(font, Font.BOLD, 36));
        gtd.setColor(Color.WHITE);
        guildName = "===" + guildName + "===";
        int length = gtd.getFontMetrics(new Font(font, Font.PLAIN, 36)).stringWidth(guildName);
        //draw the guild name centered
        gtd.drawString(guildName, IMAGE_WIDTH - (length / 2) - (IMAGE_WIDTH / 2), 50);
        gtd.setColor(new Color(221, 255, 192, 255));
        
        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger y = new AtomicInteger(70);
        int incr = 102;
        map.forEach((s, l) -> {
            if(i.get() >= 10)
                return;
            
            User u;
            
            try {
                u = Main.jda.getGuildById(guildID).getMemberById(s).getUser();
            } catch(Throwable ignored) {return;}
            
            if(u.isBot())
                return;
            try {
                new FileOutputStream("./tmp/" + s + ".png").getChannel().transferFrom(Channels.newChannel(new URL(u.getEffectiveAvatarUrl()).openStream()), 0, MAX_VALUE);
                gtd.drawImage(
                        ImageIO.read(new File("./tmp/" + s + ".png")).getScaledInstance(64, 64, Image.SCALE_FAST),
                        10, y.get(), null);
                y.addAndGet(incr);
                i.getAndIncrement();
                gtd.setFont(new Font(font, Font.PLAIN, 30));
                gtd.setColor(new Color(255, 255, 255, 255));
                gtd.drawString(u.getName() + "#" + u.getDiscriminator(), 80, y.get() - 80);
                long currencyTotal = 
                        parseLong(getUserDataLocalOrDefault(u.getId(), guildID, CURRENCY_VAULT_NAME, "0"))
                      + parseLong(getUserDataLocalOrDefault(u.getId(), guildID, BANK_VAULT_NAME, "0"));
                
                gtd.drawString("Level " + Ranks.getLevelForXP(l), 80, y.get() - 42);
                gtd.drawString(String.format("%s / %s XP",
                        getBetterNameOtherThanJustWhateverIdk(Ranks.getLevelForXPRemainder(l)),
                        getBetterNameOtherThanJustWhateverIdk(Ranks.xpNeededForLevel(Ranks.getLevelForXP(l)))),
                        275,
                        y.get() - 42);
                gtd.drawString("UB$" + currencyTotal, 575, y.get() - 42);
                gtd.setColor(new Color(255, 255, 255, 120));
                if(i.get() <= Math.min(map.size() - 1, 9))
                    gtd.fillRect(10, y.get() - 20, 780, 3);
                new File("./tmp/" + s + ".png").delete();
            } catch(Exception ignored) {ignored.printStackTrace();}
        });
        
        //save the image.
        try {
            ImageIO.write(bi, "png", new File(String.format("./tmp/rank/%d.png", uniqueID)));
            return new File(String.format("./tmp/rank/%d.png", uniqueID));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Make a string better looking
     * so 1000 -> 1.00k
     * 1000000 -> 1.00m
     * 
     * @param data the input string
     * @return the new string
     */
    @NotNull
    @Contract(pure = true)
    private static synchronized String getBetterNameOtherThanJustWhateverIdk(long data) {
        
        String returnString = String.valueOf(data);
        
        if(data >= 1000) returnString = String.format("%.2fK", (double) data / 1000.0);
        
        if(data >= 1000000) returnString = String.format("%.2fM", (double) data / 1000000.0);
        
        return returnString;
    }
    
}
