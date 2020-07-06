package dev.alexisok.untitledbot.modules.basic.atsomeone;

import dev.alexisok.untitledbot.annotation.Finished;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A module that listens for the `someone` keyword.<br>
 * Adapted from the April 1st joke https://youtu.be/BeG5FqTpl9U
 * <br>
 * This is meant to be an example for a plugin, and is simple
 * enough for people even with little programming experience
 * to be able to understand.  It does require that you read
 * the documentation and have some knowledge of programming.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
@Finished(since="0.0.1")
public class AtSomeone extends UBPlugin {
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("someone", "module.example.someone", new AtSomeone());
    }

    /**
     * Run when someone uses the someone incantation.<br>
     * The face was obtained from https://www.lennyfaces.net/magic/ since I couldn't
     * find the original face that was used for the @someone command in Discord.  If
     * you happen to find the original face or have a copy of it, please let me know
     * by emailing me or opening an issue or pull request.
     * 
     * @param args arguments for the command.
     *             The first argument is always the name of
     *             the command.  Arguments are the discord
     *             message separated by spaces.
     * @param message the {@link Message} that can be used
     *                to get information from the user
     * @return the @someone message plus tags the name of the user who will be someone'd
     */
    @Override
    public String onCommand(String[] args, @NotNull Message message) {
        //obtained from https://www.lennyfaces.net/magic/
        String lennyA = "￡( ͡° ͜ʖ ͡°)/☆*。;+，AT SOMEONE HAS BEEN CAST\nWHO SHALL RECEIVE THY INCANTATION?\nWHY, YES, IT IS <@";
        Member randomMember = message.getGuild().getMembers().get(ThreadLocalRandom.current().nextInt(0, message.getGuild().getMembers().size()));
        lennyA += String.format("%s> OF COURSE!", randomMember.getId());
        return lennyA;
    }
}
