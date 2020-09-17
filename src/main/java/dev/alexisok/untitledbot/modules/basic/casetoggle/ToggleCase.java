package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class ToggleCase extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        boolean isUpperCase = true;
        
        String msg = String.join(" ", ArrayUtils.remove(args, 0)).toLowerCase();
        
        char[] charArray = msg.toCharArray();
        for(int i = 0; i < charArray.length; i++) {
            char s = charArray[i];
            if(isUpperCase)
                charArray[i] = Character.toUpperCase(charArray[i]);
            isUpperCase = !isUpperCase;
        }
        
        msg = String.valueOf(charArray);
    
        EmbedBuilder eb = new EmbedBuilder();
        eb.addField("Toggle Case", msg, false);
        EmbedDefaults.setEmbedDefaults(eb, message);
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("toggle-case", this);
        Manual.setHelpPage("toggle-case", "MaKe A mEsSaGe ChAnGe EaCh LeTtEr CaPiTaLiZaTiOn.");
        CommandRegistrar.registerAlias("toggle-case", "alternate-case", "ac", "tc");
    }
}
