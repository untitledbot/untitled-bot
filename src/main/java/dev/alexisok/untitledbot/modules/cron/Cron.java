package dev.alexisok.untitledbot.modules.cron;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;

/**
 * Cron command.
 * Uses the format minute, hour, day of week.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public class Cron extends UBPlugin {
    
    @Override
    @Contract(pure=true)
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        return null;
    }
    
    @Contract(pure=true)
    private static long millisUntilNextHour() {
        LocalDateTime h = LocalDateTime.now().plusHours(1).truncatedTo(HOURS);
        return LocalDateTime.now().until(h, MILLIS);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("cron", UBPerm.ADMIN, this);
        Manual.setHelpPage("cron", "Set a cron job to send messages at specified times.\n" +
                                           "Usage: `cron <dm | channel> <@join | @leave | hour, day of week> <[channel #]> <message...>.\n" +
                                           "Note: because of the bot hosting location, the timezone is Mountain Standard Time.\n" +
                                           "Day of the week can be a two letter day or number:\n" +
                                           "```\n" +
                                           "su mo tu we th fr sa\n" +
                                           " 1  2  3  4  5  6  7\n" +
                                           "```\n" +
                                           "The DM option is not available for scheduling.");
    }
}
