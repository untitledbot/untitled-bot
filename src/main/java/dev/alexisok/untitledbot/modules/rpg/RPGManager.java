package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.command.Command;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

/**
 * RPG (Role Playing Game) plugin.  I made an RPG a while ago for Java and plan to import
 * it into this bot, should be interesting!
 * 
 * @author AlexIsOK
 * @since 1.0.0
 */
public class RPGManager extends UBPlugin implements Command {
    
    public void onRegister() {
        Vault.addDefault("rpg-xp", "0");
        Vault.addDefault("rpg-level", "0");
        
        //skeleton permission nodes...
        CommandRegistrar.register("rpg", "alexisok.rpg", this);
        CommandRegistrar.setDefaultPermissionForNode("alexisok.rpg", true);
    }
    
    public String onCommand(String @NotNull [] args, Message message) {
        if(args.length == 1)
            return "For help with the RPG, please see https://alexisok.dev/untitled-bot/rpg.html";
        return null;
    }
    
}
