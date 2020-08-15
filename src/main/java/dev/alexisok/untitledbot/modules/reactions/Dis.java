package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * ಠ_ಠ
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Dis extends UBPlugin {
    
    private static final MessageEmbed FACE = new EmbedBuilder().addField("", "ಠ_ಠ", false).build();
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        return FACE;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("disappointed", this);
        Manual.setHelpPage("disappointed", "display ಠ_ಠ");
        CommandRegistrar.registerAlias("disappointed", "dis");
    }
}
