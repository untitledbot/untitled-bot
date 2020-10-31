package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Inspired by the rnak command on Darling bot by 14ROVI.
 * 
 * https://darling.rovi.me/
 * 
 * @author AlexIsOK
 * @since 1.3.22
 */
public class Rnak extends UBPlugin {

    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        try {
            message.getChannel().sendFile(Objects.requireNonNull(RankImageRender.render(message.getAuthor().getId(), message.getGuild().getId(), message.getIdLong(), true))).queue();
        } catch(Throwable t) {
            t.printStackTrace();
            message.getChannel().sendMessage("bot did an oopsie").queue();
        }
        return null;
    }

    @Override
    public void onRegister() {
        CommandRegistrar.register("rnak", this);
        Manual.setHelpPage("rnak", "`]DI resu | @ resu[` knar :egasU\n" +
                ".knar )s'resu rehtona ro( ruoy teG\n\n\n" +
                "Inspired by the `rnak` command for Darling by 14ROVI.");
        CommandRegistrar.registerAlias("rnak", "rakn", "rnk", "wank");
    }
}
