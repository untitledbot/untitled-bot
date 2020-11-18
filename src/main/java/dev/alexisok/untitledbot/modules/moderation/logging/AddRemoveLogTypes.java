package dev.alexisok.untitledbot.modules.moderation.logging;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.moderation.ModHook;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Add or remove logging types.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class AddRemoveLogTypes extends UBPlugin implements Command {
    
    private static final String LOG_SEPARATOR = ",";
    
    @Override
    public @Nullable MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args[0].equals("add-log")) {
            if(args.length == 1) {
                eb.setColor(Color.RED);
                eb.addField("Logging", "Usage: add-log <log types.....>\n" +
                                               "Use `list-log-types` for a list of all log types.", false);
                ModHook.resetCacheForGuild(message.getGuild().getId());
                return eb.build();
            }
            
            ArrayList<LogTypes> lt = new ArrayList<>();
            
            try {
                String[] stuffs = Vault.getUserDataLocal(null, message.getGuild().getId(), "log.policies").split(LOG_SEPARATOR);
                
                //add existing log types
                for(String s : stuffs) {
                    lt.add(LogTypes.valueOf(s.toUpperCase()));
                }
            } catch(NullPointerException | IllegalArgumentException ignored) {}
            
            int unknownBreak = 0;
            final int maxBreak = 2;
            boolean maxBreakWarning = false;
            
            for(String s : args) {
                if(s.equalsIgnoreCase("add-log"))
                    continue;
                s = s.replace("-", "_").toUpperCase();
                try {
                    if(s.equals("add-log"))
                        continue;
                    if(!lt.contains(LogTypes.valueOf(s)))
                        lt.add(LogTypes.valueOf(s));
                } catch(EnumConstantNotPresentException ignored) {
                    Logger.debug("Caught " + s + " as an ignored enum.");
                } catch(IllegalArgumentException ignored) {
                    if(maxBreak >= unknownBreak) {
                        eb.addField("Warning", "Unknown log type " + s, false);
                        unknownBreak++;
                    } else if(!maxBreakWarning) {
                        eb.addField("Warning", "Maximum field limit reached for this type of warning.", false);
                        maxBreakWarning = true;
                    }
                }
            }
            
            ModHook.resetCacheForGuild(message.getGuild().getId());
            
            return getMessageEmbed(message, eb, lt);
            
        } else if(args[0].equals("remove-log")) {
            if(args.length == 1) {
                eb.setColor(Color.RED);
                eb.addField("Logging", "Usage: remove-log <log types.....>\n" +
                                               "Use `list-log-types` for a list of all log types.", false);
                ModHook.resetCacheForGuild(message.getGuild().getId());
                return eb.build();
            }
            ArrayList<LogTypes> lt = new ArrayList<>();
            
            Logger.debug("Removing existing log types.");
            
            try {
                String[] stuffs = Vault.getUserDataLocal(null, message.getGuild().getId(), "log.policies").split(LOG_SEPARATOR);
                
                //add existing log types
                for(String s : stuffs) {
                    lt.add(LogTypes.valueOf(s));
                }
            } catch(NullPointerException ignored) {}
            
            Logger.debug("Removing log types.");
            
            int unknownBreak = 0;
            final int maxBreak = 2;
            boolean maxBreakWarning = false;
            
            for(String s : args) {
                if(s.equalsIgnoreCase("remove-log"))
                    continue;
                s = s.replace("-", "_").toUpperCase();
                try {
                    if(s.equals("remove-log"))
                        continue;
                    lt.remove(LogTypes.valueOf(s.toUpperCase()));
                } catch(EnumConstantNotPresentException ignored) {
                    Logger.debug("Caught " + s + " as an ignored enum.");
                } catch(IllegalArgumentException ignored) {
                    if(maxBreak >= unknownBreak) {
                        eb.addField("Warning", "Unknown log type " + s, false);
                        unknownBreak++;
                    } else if(!maxBreakWarning) {
                        eb.addField("Warning", "Maximum field limit reached for this type of warning.", false);
                        maxBreakWarning = true;
                    }
                }
            }
            
            ModHook.resetCacheForGuild(message.getGuild().getId());
            
            return getMessageEmbed(message, eb, lt);
        } else {
            ModHook.resetCacheForGuild(message.getGuild().getId());
            Logger.log("Somehow, this message was shown.  AddRemoveLogTypes error?  Not really.  Please report this.");
            return null;
        }
    }
    
    @NotNull
    private MessageEmbed getMessageEmbed(@NotNull Message message, @NotNull EmbedBuilder eb, @NotNull ArrayList<LogTypes> lt) {
        String[] store = new String[lt.size()];
        
        for(int i = 0; i < store.length; i++)
            store[i] = lt.get(i).toString();
        
        Vault.storeUserDataLocal(null,
                message.getGuild().getId(),
                "log.policies",
                Arrays.toString(store)
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "")); //this is horrible please send help
        
        eb.setColor(Color.GREEN);
        eb.addField("Logging", "Logging policies updated.", false);
        
        return eb.build();
    }
}
