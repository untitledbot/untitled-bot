package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Set the color of the rank background
 * @since 1.4.0
 */
public final class RankColor extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length != 2 || !args[1].toLowerCase().matches("^[#]?[0-9a-f]{6}")) {
            eb.setColor(Color.RED);
            eb.setTitle("Rank Color");
            eb.setDescription("Please input a valid color to use (ex. #7F8E58)\n" +
                    "To find a color, use [this site](https://colorpicker.me) and copy the `Hex Code` field when you pick your color.");
            return eb.build();
        }
        
        String code = args[1].toLowerCase();
        
        //add # to code if it is not present
        if(!code.startsWith("#"))
            code = "#" + code;
        
        Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "rank-bg.color", code);
        
        eb.setColor(Color.decode(code));
        eb.setTitle("Rank Color");
        eb.setDescription("Your rank card color has been set.  Use `rank` to check it out!");
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-color", this);
        Manual.setHelpPage("rank-color", "Set the color of your rank card.\n" +
                "Usage: `rank-color <hex>`\n\n" +
                "To get the <hex> code, [pick a color from here](https://colorpicker.me)");
        
        //who in their right mind thought that adding a `u` to color was a good idea
        CommandRegistrar.registerAlias("rank-color", "rankcolor", "rank-colour", "rankcolour", "rank-col", "rankcol");
    }
}
