package dev.alexisok.untitledbot.modules.basic.avatar;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * Get a user's avatar
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class Avatar extends UBPlugin {
    
    /**
     * 
     * Get the avatar image and send it as an image
     * 
     * @param args arguments for the command.
     *             The first argument is always the name of
     *             the command.  Arguments are the discord
     *             message separated by spaces.
     * @param message the {@link Message} that can be used
     *                to get information from the user
     * @return the embed with the avatar image
     */
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        
        if(message.getMentionedMembers().size() == 1)
            eb.setImage(message.getMentionedMembers().get(0).getUser().getAvatarUrl());
        else
            eb.setImage(message.getAuthor().getAvatarUrl());
        
        try {
            return eb.build();
        } catch(IllegalStateException ignored) {
            //if a user does not have an avatar
            try {
                return eb.setImage(message.getMentionedMembers().get(0).getUser().getDefaultAvatarUrl()).build();
            } catch(IndexOutOfBoundsException ignored2) {
                return eb.setImage(message.getAuthor().getDefaultAvatarUrl()).build();
            }
        }
    }
    
    /**
     * Registers the commands `avatar` and `av`
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("avatar", this);
        CommandRegistrar.registerAlias("avatar", "av");
        Manual.setHelpPage("avatar", "Get the avatar of yourself or another user.\n" +
                                             "Usage: `avatar [user @]`\n" +
                                             "Leave the option blank for your own avatar.");
    }
}
