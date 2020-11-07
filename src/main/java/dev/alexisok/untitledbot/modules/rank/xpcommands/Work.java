package dev.alexisok.untitledbot.modules.rank.xpcommands;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the `work` command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Work extends UBPlugin {
    
    private static final ArrayList<String> RESPONSES = new ArrayList<>();
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String limitStr = Vault.getUserDataLocal(null, message.getGuild().getId(), "work.cooldown");
        
        long limit = 86400; //1 day
        
        if(limitStr != null && limitStr.matches("[0-9]+"))
            limit = Long.parseLong(limitStr);
        
        if(isRateLimit(message.getAuthor().getId(), message.getGuild().getId(), limit)) {
            eb.addField("Work",
                    String.format("You cannot work so soon!\n%s",
                            limit == 86400
                                    ? "A moderator can set the limit and amount using the `config` command." 
                                    : "Please wait " + limit + " seconds."),
                    false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long current = Long.parseLong(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, "0"));
        
        long min = Long.parseLong(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "work.limit.minimum", "100"));
        long max = Long.parseLong(Vault.getUserDataLocalOrDefault(null, message.getGuild().getId(), "work.limit.maximum", "500"));
        
        if(min > max) {
            eb.addField("Error", "Please [reconfigure the work command](https://untitled-bot.xyz/config.html)." +
                                         "  The bound (maximum) amount of money must be greater than the origin (minimum).", false);
            eb.setColor(Color.RED);
        }
        
        long amount = ThreadLocalRandom.current().nextLong(min, max + 1);
        
        if((current + (amount * 5)) > Long.MAX_VALUE - 2000L) {
            eb.addField("Work", String.format("I would give you %d but you have the maximum amount of money!", amount), false);
            eb.setColor(Color.YELLOW);
            return eb.build();
        }
        
        try {
            eb.addField("Work",
                    String.format(RESPONSES.get(ThreadLocalRandom.current().nextInt(RESPONSES.size())),
                            amount),
                    false);
            current += amount;
        } catch(RuntimeException ignored) {
            eb.addField("Work",
                    String.format("You would have earned UB$%d but instead you earned UB$%d because there is a bug.%n" +
                                          "First person to report this bug through the Discord server gets +40 levels (up to 100 max)" +
                                          " for every server they are currently in.%n" +
                                          "Will you report the bug, or will you continue to abuse it?  The choice is yours to make.",
                            amount, amount * 5), false);
            current += amount * 5;
        }
        
        setRateLimiter(message.getAuthor().getId(), message.getGuild().getId());
        
        Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), Shop.CURRENCY_VAULT_NAME, String.valueOf(current));
        
        eb.setColor(Color.GREEN);
        
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        
        //use "UB$%s" for the place of the money earned.
        RESPONSES.add("You work as a developer for [untitled-bot](https://github.com/untitledbot/untitled-bot) and make UB$%s.");
        RESPONSES.add("You make UB$%s making a Minecraft Lets Play series.");
        RESPONSES.add("You get your Discord bot verified and earn UB$%s from donations.");
        RESPONSES.add("You set your Discord profile picture to an anime girl and get UB$%s in donations.");
        RESPONSES.add("You mine bitcoin using your old laptop and make UB$%s.");
        RESPONSES.add("You play Untitled Goose Game and get UB$%s in YouTube revenue.");
        RESPONSES.add("???  You stumble across UB$%s when walking your dog!");
        RESPONSES.add("You illegally download UB$ onto your computer, getting UB$%s.  Disgusting.");
        RESPONSES.add("You use 10 percent of your power, gaining UB$%s in the process.");
        RESPONSES.add("You lick the pudding off of the lid like a sane person, gaining UB$%s.");
        RESPONSES.add("You entered the [UB$ contest](https://youtu.be/M5V_IXMewl4), gaining UB$%s because of it.");
        RESPONSES.add("UB$%s appears in your pocket as if by magic.");
        RESPONSES.add("You wander throughout the Discord Forest and find Wumpus.  He gives you UB$%d.");
        RESPONSES.add("you get UB$%d");
        RESPONSES.add("You write your first program, but say \"How are you, World?\" instead of \"Hello, World!\".\n" +
                              "The World appreciates your thoughts and gives you UB$%d as a thank you.");
        RESPONSES.add("You use your STAND POWER to unlock UB$%d."); //menacing
        RESPONSES.add("i need more responses please contribute to this on github or something anyways you earned UB$%d.");
        RESPONSES.add("You find out the purpose of life, and earn UB$%d because of it.");
        RESPONSES.add("You went vegan for ten minutes and earn UB$%d from stream donations.");
        
        CommandRegistrar.register("work", this);
        Manual.setHelpPage("work", "Work to get untitled-bot currency (not a real currency).");
    }
    
    /**
     * Is the user being rate limited?  Maybe...
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @param cooldownTime the time to wait between the command in seconds.
     * @return {@code true} if the user is being rate-limited, {@code false} otherwise.
     */
    private static boolean isRateLimit(String userID, String guildID, long cooldownTime) {
        String epochOldString = Vault.getUserDataLocal(userID, guildID, "work.cooldown");
        
        if(epochOldString == null) return false;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious <= cooldownTime;
    }
    
    /**
     * Set the rate limit for the user.
     * This does not need the cooldown time as that is only
     * needed when checking for a rate limit.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     */
    private static void setRateLimiter(String userID, String guildID) {
        Vault.storeUserDataLocal(userID, guildID, "work.cooldown", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    /**
     * Get the amount of time left for the rate limit in hours.
     * It is recommended to convert this using "%.2f" in a formatting method.
     * 
     * @param userID the ID of the user.
     * @param guildID the ID of the guild.
     * @param cooldownTime the time to wait between the command in seconds.
     * @see String#format(String, Object...)
     * @see java.io.PrintStream#printf(String, Object...)
     * @return the amount of hours left as a double.
     */
    private static double rateLimitTimeHours(String userID, String guildID, long cooldownTime) {
        return ((cooldownTime - (Instant.now().getEpochSecond() - rateLimitTime(userID, guildID))) / 60.0) / 60.0;
    }
    
    /**
     * Get the time for the user rate-limit in unix-epoch.
     * returns 0 if the data is null or empty.
     * @return the time left for rate-limit in seconds.
     */
    private static double rateLimitTime(String userID, String guildID) {
        String a = Vault.getUserDataLocal(userID, guildID, "work.cooldown");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
}
