package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

/**
 * Get total amount of user xp
 * 
 * @see Ranks#totalXPFromAllLevels(String, String)
 * @author AlexIsOK
 * @since 1.3
 */
public class Total extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        long total;
        
        try {
            int s = message.getMentionedMembers().size();
            User target = s == 1 ? message.getMentionedMembers().get(0).getUser() : Main.jda.getUserById(args[1]);
            total = Ranks.totalXPFromAllLevels(target.getId(), message.getGuild().getId());
        } catch(Exception ignored) {
            total = Ranks.totalXPFromAllLevels(message.getMember().getId(), message.getGuild().getId());
        }
        
        eb.addField("Ranks", "Total XP: " + total + ".", false);
        
        return eb.build();
        
    }
    
}
