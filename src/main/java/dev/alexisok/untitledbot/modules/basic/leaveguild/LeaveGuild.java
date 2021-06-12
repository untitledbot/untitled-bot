package dev.alexisok.untitledbot.modules.basic.leaveguild;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class LeaveGuild extends UBPlugin {
    
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        if(!message.getAuthor().getId().equals("541763812676861952"))
            return null;
        
        if(args.length != 2 || !args[1].matches("^[0-9]+")) {
            message.reply("need guild id").queue();
            return null;
        }
        Guild g = Main.getGuildById(message.getGuild().getId());
        
        if(g == null) {
            message.reply("guild null").queue();
            return null;
        }
        
        g.leave().queue((r) -> message.reply("ok").queue());
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("leave-guild", UBPerm.OWNER, this);
        CommandRegistrar.registerAlias("leave-guild", "leaveguild");
    }
}
