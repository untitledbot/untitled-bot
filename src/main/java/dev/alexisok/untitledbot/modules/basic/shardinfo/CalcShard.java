package dev.alexisok.untitledbot.modules.basic.shardinfo;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Calculates the ID of a shard
 * 
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class CalcShard extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long guild_id;
        int num_shards;
        
        try {
            if(args.length != 3 || !args[1].matches("[0-9]{10,35}") || !args[2].matches("[0-9]{0,5}"))
                throw new NumberFormatException();
            guild_id = Long.parseLong(args[1]);
            num_shards = Integer.parseInt(args[2]);
        } catch(NumberFormatException ignored) {
            eb.addField("Shard Calculator", "Usage: `calc-shard <guildID> <shardTotal>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        eb.addField("Shard Calculator", "The ID of this shard will be " + (guild_id >> 22) % num_shards, false);
        eb.setColor(Color.GREEN);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("calc-shard", this);
        Manual.setHelpPage("calc-shard", "Calculate the ID of the shard based off of the guild ID and shard total.\n" +
                "Usage: `calc-shard <guildID> <shardTotal>`");
    }
}
