package dev.alexisok.untitledbot.modules.rpg;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.rpg.exception.RPGDataFileHasAlreadyBeenInitializedException;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.io.File;

/**
 * @author AlexIsOK
 * @since 1.4.0
 */
public final class RPGCommand extends UBPlugin {
    
    /**
     * 
     * @param args arguments for the command.
     *             The first argument is always the name of
     *             the command.  Arguments are the discord
     *             message separated by spaces.
     * @param message the {@link Message} that can be used
     *                to get information from the user
     * @return the embed.
     */
    @Nullable
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("RPG", "Welcome to the untitled-bot RPG!\n\n" +
                                       "The help is pretty extensive, so please use the command `help rpg` to list the specific " +
                                       "help pages.\n" +
                                       "\n" +
                                       "More information and better help guides are available on the " +
                                       "[official website](https://untitled-bot.xyz/rpg).", false);
            return eb.build();
        }
        
        switch(args[1]) {
            //stats command
            case "stats": case "profile": case "rank": case "level": case "balance": {
                try {
                    File rendered = RPGProfileDrawer.render(message.getAuthor().getId(), message.getGuild().getId(), message.getId());
                    message.getChannel().sendFile(rendered).queue(done -> {
                        if(!rendered.delete()) {
                            Logger.log("WARNING: the rendered image for the RPG command stats was not deleted!");
                        }
                    });
                    return null;
                } catch(Throwable ignored) {
                    eb.addField("RPG", "It seems I couldn't send the profile image!  Do I have the 'Attach Files' permission?", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
            }
            case "init": {
                try {
                    Init.init(message.getAuthor().getId(), message.getGuild().getId());
                    eb.addField("RPG", "Your data file for this guild has been updated to support the RPG.", false);
                    eb.setColor(Color.GREEN);
                } catch(RPGDataFileHasAlreadyBeenInitializedException ignored) {
                    eb.addField("RPG", "You already started the RPG!", false);
                    eb.setColor(Color.RED);
                }
                return eb.build();
            }
            
            default: {
                eb.addField("RPG", String.format("Unknown sub-command `%s`.%nPlease check the spelling and try again, or " +
                                                         "use the command `help rpg` for help.", args[1]), false);
                eb.setColor(Color.RED);
                return eb.build();
            }
        }
    }
    
    @Override
    public void onRegister() {
        //here we go!!
        CommandRegistrar.register("rpg", this);
        Manual.setHelpPage("rpg", "Because the RPG command handles so much, you must use the following commands for help.\n" +
                                          "`help rpg-`"); //TODO
    }
}
