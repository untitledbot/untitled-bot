package dev.alexisok.untitledbot.modules.images.apiflipnote;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class Achievement extends UBPlugin {
    
    @SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length < 3) {
            return eb.addField("", "Usage: `achievement <icon number> <text>`", false).build();
        }
        
        try {
            int item = Integer.parseInt(args[1]);
            String text = String.join("%20", Arrays.copyOfRange(args, 2, args.length))
                    .replace("\n", "")
                    .replace("&", "");
            try {
                message.getChannel().sendTyping().queue();
                String uri = DoImageThingFlip.download("https://api.alexflipnote.dev/achievement?text=" + text + "&icon=" + item, message.getId());
                message.getChannel().sendFile(new File(uri)).queue(a -> {
                    new File(uri).delete();
                });
                return null;
            } catch(IllegalArgumentException ignored) {
                eb.addField("Error", String.format("You literally broke the bot into %d pieces.", Long.MAX_VALUE), false);
            }
        } catch(Throwable ignored) {
            eb.addField("", "Usage: `achievement <icon number> <text>`", false);
        }
        
        return eb.build();
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("achievement", this);
        Manual.setHelpPage("achievement", "Generate a Minecraft achievement image.\n" +
                "API: https://api.alexflipnote.dev/\n" +
                "Usage: `achievement <icon number 1-50> <text>`");
        CommandRegistrar.registerAlias("achievement", "advancement", "ach");
    }
}
