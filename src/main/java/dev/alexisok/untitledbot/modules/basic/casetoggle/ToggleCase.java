package dev.alexisok.untitledbot.modules.basic.casetoggle;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class ToggleCase extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        
        try {
            if (args.length == 1) {
                message.getChannel().getHistoryBefore(message.getId(), 1)
                        .queue(t -> {
                            try {
                                message.getChannel().sendMessage(alternate(t.getRetrievedHistory().get(0).getContentRaw().replaceAll("<@[0-9]{5,64}>", "<user>").toLowerCase())).queue();
                            } catch(Throwable ignored){}
                        });
            } else {
                message.getChannel().sendMessage(alternate(String.join(" ", ArrayUtils.remove(args, 0)).toLowerCase())).queue();
            }
        } catch(Throwable ignored) {
            message.getChannel().sendMessage("Cannot mock the previous message as it either had no text or the text is in an embed.").queue();
        }
        return null;
    }
    
    @NotNull
    private static String alternate(@NotNull String normal) {
        boolean isUpperCase = false;
        char[] charArray = normal.toCharArray();
        for(int i = 0; i < charArray.length; i++) {
            if(isUpperCase)
                charArray[i] = Character.toUpperCase(charArray[i]);
            isUpperCase = !isUpperCase;
        }
        return String.valueOf(charArray);
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("toggle-case", this);
        Manual.setHelpPage("toggle-case", "MaKe A mEsSaGe ChAnGe EaCh LeTtEr CaPiTaLiZaTiOn.");
        CommandRegistrar.registerAlias("toggle-case", "alternate-case", "ac", "tc", "mock");
    }
}
