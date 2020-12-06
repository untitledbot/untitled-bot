package dev.alexisok.untitledbot.modules.basic.reward;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.discordbots.api.client.entity.VotingMultiplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

import static dev.alexisok.untitledbot.Main.API;

/**
 * Gives the user bonus XP if they have voted for the bot
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class VoteReward extends UBPlugin {
    
    private static final long XP_TO_GIVE = 1000L;
    
    @Nullable
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        
        if(true) {
            message.getChannel().sendMessage("Sorry " + message.getAuthor().getAsMention() + ", but this command has been disabled.\n" +
                    "It may come back online soon, but I can't promise anything.").queue(r -> BotClass.addToDeleteCache(message.getId(), r));
            return null;
        }
        
        if(args.length == 2) {
            if(args[1].equals("override")) {
                EmbedBuilder eb = new EmbedBuilder();
                if(message.getAuthor().getId().equals("541763812676861952")) {
                    eb.addField("Reward", "Here's the bonus xp for testing :P", false);
                    runReward("541763812676861952", message, eb, new VotingMultiplier());
                    return eb.build();
                } else {
                    eb.addField("Reward", "The override command can only be used by AlexIsOK, become him and try again.", false);
                }
                return eb.build();
            }
        }
    
        API.hasVoted(message.getAuthor().getId()).whenComplete((hasVoted, e) -> {
            API.getVotingMultiplier().whenComplete((multiplier, e2) -> {
                EmbedBuilder eb = new EmbedBuilder();
                EmbedDefaults.setEmbedDefaults(eb, message);
                if(hasVoted) {
                    if(isRateLimit(message.getAuthor().getId())) {
                        runReward(message.getAuthor().getId(), message, eb, multiplier);
                        return;
                    } else {
                        eb.addField("untitled-bot", String.format("You have already claimed your vote reward :P%n" +
                                                                          "You can claim it again in %.2f hour%s.",
                                rateLimitTimeHours(message.getAuthor().getId()),
                                (int) rateLimitTimeHours(message.getAuthor().getId()) == 1 ? "" : "s"
                        ), false);
                        eb.setColor(Color.RED);
                    }
                } else {
                    eb.addField("untitled-bot",
                            "Please vote for the bot on [top.gg](https://top.gg/bot/730135989863055472/vote) to claim your bonus XP :)",
                            false);
                    eb.setColor(Color.BLUE);
                }
                try {message.getChannel().sendMessage(eb.build()).queue(r -> BotClass.addToDeleteCache(message.getId(), r));} catch (InsufficientPermissionException ignored) {
                    Logger.debug("Could not send a vote reward message to a channel.");
                }
            });
        });
        
        return null;
    }
    
    
    /**
     *
     * @param userID the ID of the user.
     * @return true if there is a rate limit, false otherwise.
     */
    private static boolean isRateLimit(String userID) {
        String epochOldString = Vault.getUserDataLocal(userID, null, "boost.voted");
        
        if(epochOldString == null) return true;
        
        long epochPrevious = Long.parseLong(epochOldString);
        long epochCurrent  = Instant.now().getEpochSecond();
        
        return epochCurrent - epochPrevious >= 21600; //6 hours
    }
    
    private static void setRateLimiter(String userID) {
        Vault.storeUserDataLocal(userID, null, "boost.voted", String.valueOf(Instant.now().getEpochSecond()));
    }
    
    private static double rateLimitTimeHours(String userID) {
        return ((21600.0 - (Instant.now().getEpochSecond() - rateLimitTime(userID))) / 60.0) / 60.0;
    }
    
    /**
     * Get the time for the user rate-limit in unix-epoch.
     * returns 0 if the data is null or empty.
     * @return the time left for rate-limit in seconds
     */
    private static double rateLimitTime(String userID) {
        String a = Vault.getUserDataLocal(userID, null, "boost.voted");
        return a == null || a.isEmpty() ? 0.0 : Double.parseDouble(a) + 0.0;
    }
    
    /**
     * Do the reward stuff
     * 
     * @param userID the ID of the user as a {@link String}
     * @param message the message
     * @param eb the embed builder
     * @param multiplier the voting multiplier
     */
    private static void runReward(@NotNull String userID, @NotNull Message message, @NotNull EmbedBuilder eb, @NotNull VotingMultiplier multiplier) {
        long reward = XP_TO_GIVE;
        //quadruple reward for weekend
        if(multiplier.isWeekend())
            reward <<= 2;
        eb.addField("untitled-bot", "You have claimed " + reward + " XP for your vote!  Thank you for " +
                                            "voting for untitled-bot :)", false);
        eb.setColor(Color.GREEN);
        Logger.debug(String.format("Vote reward has been cast for %s", userID));
        Vault.storeUserDataLocal(userID,
                message.getGuild().getId(),
                "ranks-xp",
                String.valueOf(
                        Long.parseLong(
                            Objects.requireNonNull(
                                Vault.getUserDataLocal(
                                        userID,
                                        message.getGuild().getId(),
                                        "ranks-xp"
                                )
                            )
                        ) + reward
                )
        );
        try {message.getChannel().sendMessage(eb.build()).queue(r -> BotClass.addToDeleteCache(message.getId(), r));} catch (Exception e) {
            e.printStackTrace();
            Logger.debug("Could not send a vote reward message to a channel.");
        }
        setRateLimiter(userID);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("vote-reward", this);
        Manual.setHelpPage("vote-reward", "Get bonus XP for voting for the bot!\n" +
                                                  "Note: you cannot vote for the bot and get bonus XP on " +
                                                  "multiple servers.");
        CommandRegistrar.registerAlias("vote-reward", "vr");
        
    }
}
