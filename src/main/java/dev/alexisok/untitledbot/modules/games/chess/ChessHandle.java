package dev.alexisok.untitledbot.modules.games.chess;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class ChessHandle extends UBPlugin {
    
    private static final HashMap<String, Long> ONGOING_GAMES_BY_ID = new HashMap<>();
    private static final HashMap<Long, ChessGame> GAMES = new HashMap<>();
    
    private static final HashMap<String, String> INVITES = new HashMap<>();
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        if(ONGOING_GAMES_BY_ID.containsKey(message.getAuthor().getId())) {
            eb.setColor(Color.RED);
            eb.addField("Chess", String.format("You already have an ongoing game!\n" +
                    "To move a piece, say something in chat such as `B1 to A3`.\n" +
                    "To stop the chess game, say `quit`.\n" +
                    "To see your stats, do `%schess stats`",
                BotClass.getPrefixNice(message.getGuild().getId())),
                    false);
            return eb.build();
        }
        
        if(args.length == 1) {
            eb.addField("Chess", "Usage: `chess <stats|play|accept>`", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
        switch(args[1]) {
            case "stats": {
                String userID;

                if(message.getMentionedUsers().size() == 1) userID = message.getMentionedUsers().get(0).getId();
                else userID = message.getAuthor().getId();

                String wins = Vault.getUserDataLocalOrDefault(userID, message.getGuild().getId(), "chess.stats.wins", "0");
                String losses = Vault.getUserDataLocalOrDefault(userID, message.getGuild().getId(), "chess.stats.losses", "0");
                String forfeits = Vault.getUserDataLocalOrDefault(userID, message.getGuild().getId(), "chess.stats.forfeits", "0");

                eb.addField("Chess", String.format("Stats for <@%s>:\n" +
                        "Wins: %s%n" +
                        "Losses: %s%n" +
                        "Forfeits: %s%n", userID, wins, losses, forfeits), false);
                eb.setColor(Color.GREEN);
                return eb.build();
            }
            
            case "play": {
                if(message.getMentionedMembers(message.getGuild()).size() != 1) {
                    eb.addField("Chess", "Usage: `chess play <user>`", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                
                Member opponent = message.getMentionedMembers(message.getGuild()).get(0);
                
                if(ONGOING_GAMES_BY_ID.containsKey(opponent.getId())) {
                    eb.addField("Chess", "Your opponent is already in a game!", false);
                    eb.setColor(Color.RED);
                    return eb.build();
                }
                
            }
            
            case "accept": {
                
            }
        }
        return null;
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("chess", this);
        Manual.setHelpPage("chess", "Start a game of chess.\n" +
                "Usage: `chess [@opponent]`\n" +
                "AI is not in this yet, but feel free to contribute to the bot to add it!");
    }
}
