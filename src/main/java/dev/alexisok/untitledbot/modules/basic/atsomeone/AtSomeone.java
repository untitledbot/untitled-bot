package dev.alexisok.untitledbot.modules.basic.atsomeone;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A module that listens for the `someone` keyword.<br>
 * Adapted from the April 1st joke https://youtu.be/BeG5FqTpl9U
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public class AtSomeone extends UBPlugin {
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("someone", "module.example.someone", new AtSomeone());
    }
    
    @Override
    public String onCommand(String[] args, @NotNull Message message) {
        String lennyA = "\uFFE1( \u0361\u00B0 \u035C\u0296 \u0361\u00B0)/\u2606*\u3002;+\uFF0CAT SOMEONE HAS BEEN CAST\nWHO SHALL RECEIVE THY INCANTATION?\nWHY, YES, IT IS <@";
        Member a = message.getGuild().getMembers().get(ThreadLocalRandom.current().nextInt(0, message.getGuild().getMembers().size()));
        lennyA += String.format("%s> OF COURSE!", a.getId());
        return lennyA;
    }
}
