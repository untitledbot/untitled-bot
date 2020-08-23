package dev.alexisok.untitledbot.modules.reactions;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * ┬┴┬┴┤/╲/( ͡° ͡° ͜ʖ ͡° ͡°)/\╱\
 * (∩ ͡° ͜ʖ ͡°)⊃━炎炎炎炎炎炎炎炎
 * @author AlexIsOK
 * @since 1.3
 */
public final class AttackOnLenny extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
//        dsffdgfdg
//        TODO
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("attack-on-lenny", this);
        CommandRegistrar.registerAlias("attack-on-lenny", "aol");
        Manual.setHelpPage("attack-on-lenny", "┬┴┬┴┤/╲/( ͡° ͡° ͜ʖ ͡° ͡°)/\\╱\\");
    }
}
