package dev.alexisok.untitledbot.modules.basic.minecraft;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.modules.music.NowPlaying;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shanerx.mojang.Mojang;

import java.awt.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static org.shanerx.mojang.SalesStats.Options.*;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public class MinecraftAPI extends UBPlugin {
    
    @Override
    public synchronized @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Mojang api = new Mojang().connect();
        
        if(args.length == 1) {
            eb.addField("Mojang API", "Please add a sub-command.\n" +
                    "Use `help minecraft` for a list of sub-commands.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        switch(args[1]) {
            case "sales": {
                int last24n = api.getSaleStatistics(ITEM_SOLD_MINECRAFT).getLast24hrs();
                int last24p = api.getSaleStatistics(PREPAID_CARD_REDEEMED_MINECRAFT).getLast24hrs();
                
                int totalNormal = api.getSaleStatistics(ITEM_SOLD_MINECRAFT).getTotal();
                int totalPrepaid = api.getSaleStatistics(PREPAID_CARD_REDEEMED_MINECRAFT).getTotal();
                
                eb.addField("Minecraft Sales", String.format("" +
                        "There have been %d sales in total, %d of which were prepaid.%n" +
                        "Additionally, there have been %d sales in the last 24 hours, %d of which were prepaid.",
                        totalNormal + totalPrepaid, totalPrepaid, last24n + last24p, last24p), false);
                eb.setColor(Color.GREEN);
                return eb.build();
            }
            case "history": {
                if(args.length != 3) {
                    eb.addField("History", "Usage: `minecraft history <username>`", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                
                String UUID = null;
                
                try {
                    UUID = api.getUUIDOfUsername(args[2]);
                } catch(Exception ignored) {}
                
                if(UUID == null) {
                    eb.addField("Error", "Could not get a player by that name!", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                eb.setTitle("Username history for " + args[2]);
                StringBuilder description = new StringBuilder("");
                api.getNameHistoryOfPlayer(UUID).forEach((username, time) -> {
                    description.append(String.format("**Username**: %s   **time**: %s%n", edm(username), new Date(time)));
                });
                eb.setDescription(description);
                eb.setColor(Color.GREEN);
                return eb.build();
            }
            default: {
                eb.addField("Mojang API", "Unknown sub-command.", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        }
    }
    
    /**
     * Escape Discord markdown.
     * @return the escaped String.
     */
    private static String edm(String str) {
        return NowPlaying.escapeDiscordMarkdown(str);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("minecraft", this);
        Manual.setHelpPage("minecraft", "Use the Minecraft API.\n" +
                "Sub-commands:\n" +
                "`sales` - get the amount of sales for Minecraft.\n" +
                "`history` - get the name history of a player.\n" +
                "");
    }
}
