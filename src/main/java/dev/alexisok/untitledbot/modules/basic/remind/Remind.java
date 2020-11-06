package dev.alexisok.untitledbot.modules.basic.remind;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * @author AlexIsOK
 * @since 1.3.22
 */
public final class Remind extends UBPlugin {
    
    @NotNull
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length <= 3) {
            eb.addField("Remind", "Usage `remind <time> <unit> <message...>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        String cr = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "remind");
        
        long currentTimer = Long.parseLong(cr == null ? "0" : cr);
    
        if(currentTimer > Instant.now().toEpochMilli() / 1000) {
            eb.addField("Remind", "You already have a reminder running!  Please wait for it to execute.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        long duration;
        
        try {
            duration = Long.parseLong(args[1]);
            if(duration < 0 || duration > 86400000)
                throw new NumberFormatException();
        } catch(NumberFormatException ignored) {
            eb.addField("Remind", "Error: the time must be a number between 0 and 86400000 (inclusive).", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        TimeUnit unit;
        
        //enum only accepts uppercase values
        args[2] = args[2].toUpperCase();
        
        try {
            unit = TimeUnit.valueOf(args[2]);
            if(unit.toSeconds(duration) > 86400 || unit.toSeconds(duration) < 10) {
                eb.addField("Remind", "The time for remind must be between 10 seconds and 86400 seconds (one day).", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        } catch(Throwable ignored) {
            eb.addField("Remind", "There was an error with the unit.  Make sure it is spelled correctly.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        String messageToSend = String.join(" ", ArrayUtils.remove(ArrayUtils.remove(ArrayUtils.remove(args, 0), 0), 0));
        Vault.storeUserDataLocal(message.getAuthor().getId(),
                message.getGuild().getId(),
                "remind",
                String.valueOf((Instant.now().toEpochMilli() / 1000) + unit.toSeconds(duration)));
        Logger.debug("Timer being set...");
        message.getAuthor().openPrivateChannel().queue(a -> {
            a.sendMessage("Here is your reminder from <#" + message.getChannel().getId() + ">:\n" + StringUtils.left(messageToSend, 1950)).queueAfter(unit.toSeconds(duration), TimeUnit.SECONDS,
                    r -> BotClass.addToDeleteCache(message.getId(), r)
            );
        });
        
        eb.addField("Remind", "Your message has been queued!  It will send on the designated time.", false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("remind", this);
        Manual.setHelpPage("remind", "Have the bot DM you a message after a while.\n" +
                                             "Usage: `remind <time> <unit> <message...>`\n" +
                                             "Valid time units: `hours`, `minutes`, `seconds`, and `milliseconds`.\n" +
                                             "Maximum time must not exceed 86400 seconds (one day).\n" +
                                             "Note: although it is rare, if the bot goes offline, the timer will be cleared.");
    }
}
