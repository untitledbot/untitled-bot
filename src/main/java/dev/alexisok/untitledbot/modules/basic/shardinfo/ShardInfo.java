package dev.alexisok.untitledbot.modules.basic.shardinfo;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class ShardInfo extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 2 && args[1].matches("[0-9]{1,2}")) {
            int shardQuery = Integer.parseInt(args[1]);
            
            //exclusive shard count, [)
            if(shardQuery >= Main.SHARD_COUNT || shardQuery < 0) {
                eb.addField("Shard Info", "That's not a valid shard!", false);
                eb.setColor(Color.RED);
                return eb.build();
            }

            JDA j = Main.getJDAByShardId(shardQuery);
            
            eb.addField("Details about shard " + shardQuery, String.format("```%n" +
                    "Servers: %s%n" +
                    "   Ping: %s%n" +
                    "  Users: %s%n" +
                    "```", j.getGuilds().size(), j.getGatewayPing(), j.getUsers().size()), false);
            eb.setColor(Color.GREEN);

        } else {
            eb.setTitle("Shard Info");
            StringBuilder description = new StringBuilder("```\n");
            
            description.append("shard ░ servers ░ ping ░ users\n");
            description.append("░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░\n");
            
            for(JDA j : Main.jda) {
                 int servers = j.getGuilds().size();
                 int users   = j.getUsers().size();
                long ping    = j.getGatewayPing();
                
                int shardID  = j.getShardInfo().getShardId();
                
                description.append(String.format("%s%s%s%s%s%s%s%n",
                        shardID, " ".repeat(Math.max(0, 6 - String.valueOf(shardID + (shardID == message.getJDA().getShardInfo().getShardId() ? "*" : "")).length())) + "░ ",
                        servers, " ".repeat(Math.max(0, 8 - String.valueOf(servers).length())) + "░ ",
                        ping, " ".repeat(Math.max(0, 5 - String.valueOf(ping).length())) + "░ ",
                        users - 1));
            }
            
            description.append("\n```");
            
            eb.setDescription(description.toString());
        }
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("shard-info", this);
        Manual.setHelpPage("shard-info", "Get information about all or a specific shard.\n" +
                "Usage: `%sshard-info [shard]`");
        CommandRegistrar.registerAlias("shard-info", "shards", "si");
    }
}
