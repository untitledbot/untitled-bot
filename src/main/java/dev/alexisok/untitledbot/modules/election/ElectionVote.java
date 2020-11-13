package dev.alexisok.untitledbot.modules.election;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public class ElectionVote extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!ElectionKernel.INSTANCE.hasElection(message.getGuild()))
            return eb.addField("Vote", "There doesn't seem to be an ongoing election...", false).build();
        
        if(ElectionKernel.INSTANCE.isStopped(message.getGuild().getId()))
            return eb.addField("Vote", "The election has stopped!  New votes cannot be cast.", false).build();
        
        int currentVoteChange = Integer.parseInt(ElectionKernel.INSTANCE.getElectionData(String.format("voter.%s.voteCount", message.getAuthor().getId()), message.getGuild().getId(), "0"));
        int maxVoteChange     = Integer.parseInt(ElectionKernel.INSTANCE.getElectionData("vote.change", message.getGuild().getId(), "0"));
        
        //more votes than allowed
        if(currentVoteChange > maxVoteChange)
            return eb.addField("Vote", "It seems you've changed your vote too many times in this election!", false).build();

        Member target = null;
        
        if(message.getMentionedMembers().size() == 1) {
            target = message.getGuild().getMemberById(message.getMentionedMembers().get(0).getId());
        }
        
        if(target == null && args[1].matches("[0-9]+")) {
            target = message.getGuild().getMemberById(args[1]);
        }
        
        if(target == null) {
            eb.addField("Vote", "I couldn't find a member to vote for!", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        
        
//        ElectionKernel.INSTANCE.saveElectionData("", String.format("voter.%s.voteFor", message.getAuthor().getId()))
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("vote", this);
        Manual.setHelpPage("vote", "Vote for a candidate in an election.");
    }
}
