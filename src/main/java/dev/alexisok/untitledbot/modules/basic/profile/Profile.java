package dev.alexisok.untitledbot.modules.basic.profile;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.rank.RankImageRender;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * Profile command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class Profile extends UBPlugin {
    
    @Nullable
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        
        User u;
        
        try {
            u = args[1].matches("[0-9]+")
                             ? Objects.requireNonNull(message.getJDA().getUserById(args[1]))
                             : message.getMentionedMembers().get(0).getUser();
        } catch(Throwable t) {
            u = message.getAuthor();
        }
        
        try {
            File f = Objects.requireNonNull(RankImageRender.render(u.getId(), message.getGuild().getId(), message.getIdLong(), message));
            message.getChannel().sendFile(f).queue(done -> Logger.log("Deleting file: " + f.delete()));
        } catch(Throwable ignored){}
        
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("profile", this);
        Manual.setHelpPage("profile", "Get your user profile or another user's profile.\n" +
                                              "Usage: `profile [user @ | user ID]`");
        CommandRegistrar.registerAlias("profile", "prof");
    }
}
