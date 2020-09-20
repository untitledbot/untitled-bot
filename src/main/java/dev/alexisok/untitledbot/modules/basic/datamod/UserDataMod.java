package dev.alexisok.untitledbot.modules.basic.datamod;

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

/**
 * Modifies user data (owner only)
 * 
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class UserDataMod extends UBPlugin {
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(!message.getAuthor().getId().equals("541763812676861952")) {
            eb.addField("Data mod", "This command is reserved for AlexIsOK.  Become him and try again.", false);
            return eb.build();
        }
        
        try {
            String guildID   = args[1];
            String userID    = args[2];
            String dataKey   = args[3];
            String dataValue = args[4];
            
            if(userID.equals("null")) userID = null;
            if(guildID.equals("null")) guildID = null;
            
            
            if(userID == null && guildID == null)
                throw new NullPointerException();
            
            if(userID.equals("this"))
                userID = "541763812676861952";
            
            Vault.storeUserDataLocal(userID, guildID, dataKey, dataValue);
            eb.addField("ok", "done i guess", false);
            return eb.build();
            
        } catch(Throwable t) {
            eb.addField("Come on, this is easy!", "usage: `usrdatamod <guildID> <userID> <dataKey> <dataValue>`", false);
            return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("usrdatamod", "owner", this);
        Manual.setHelpPage("usrdatamod", "modifies user data");
        CommandRegistrar.registerAlias("usrdatamod", "usrmod", "moddata");
    }
}
