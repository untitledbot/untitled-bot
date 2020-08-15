package dev.alexisok.untitledbot.modules.basic.ayaya;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * AYAYA AYAYA AYAYA!!!!!11!11
 * 
 * please don't hate me
 * 
 * not a reaction but it is under that category
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class AYAYA extends UBPlugin {
    
    private static final MessageEmbed AYAYA;
    private static final MessageEmbed JOJO;
    
    static {
        AYAYA = new EmbedBuilder().setImage("https://media.discordapp.net/attachments/741171918719418419/744055357613015080/iu.png").build();
        JOJO = new EmbedBuilder().setImage("https://media.discordapp.net/attachments/696529468247769151/744061564843327558/iu.png").build();
    }
    
    @NotNull
    @Override
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        //about one in ten chance that the image sent is column fellows
        if(message.getId().endsWith("9"))
            return JOJO;
        return AYAYA;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("ayaya", this);
        Manual.setHelpPage("ayaya", "if you need help with this command, you probably don't need help.");
    }
}
