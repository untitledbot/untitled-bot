package dev.alexisok.untitledbot.modules.games.ttt;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 
 * @author AlexIsOK
 * @since 1.3.23
 */
public class TicTacToe extends UBPlugin implements MessageHook {
    
    //message ID (gameID), game
    private static final HashMap<String, TTTBoard> PLAYING = new HashMap<>();
    
    //channel id
    private static final ArrayList<String> CURRENT_GAMES_CHANNEL = new ArrayList<>();
    
    @NotNull
    @Override
    public synchronized MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        return null;
    }
    
    private static synchronized char getFromByte(byte b, int def) {
        if(b == 0)
            return Character.forDigit(def, 10);
        else if(b == 1)
            return 'x';
        else
            return 'o';
    }

    /**
     * Generate the game board provided the {@link TTTBoard}.
     * @param board the board
     * @return the board
     */
    private static synchronized String generateMessage(TTTBoard board) {
        return String.format("```%n" +
                " %s | %s | %s%n" +
                "----+----+----%n" +
                " %s | %s | %s%n" +
                "----+----+----%n" +
                " %s | %s | %s%n" +
                "```",
                getFromByte(board.getElement(0, 0), 1), //this
                getFromByte(board.getElement(1, 0), 2), //is
                getFromByte(board.getElement(2, 0), 3), //the
                getFromByte(board.getElement(0, 1), 4), //best
                getFromByte(board.getElement(1, 1), 5), //burrito
                getFromByte(board.getElement(2, 1), 6), //i've
                getFromByte(board.getElement(0, 2), 7), //ever
                getFromByte(board.getElement(1, 2), 8), //eaten
                getFromByte(board.getElement(2, 2), 9)); //yum, yum, yum!
    }
    
    private static synchronized void update(@NotNull String gameID, int emoji, @NotNull String userID) {
        //emoji was checked previously
        
        TTTBoard board = PLAYING.get(gameID);
        
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("tic-tac-toe", this);
        Manual.setHelpPage("tic-tac-toe", "Play a game of tic-tac-toe.\n" +
                "Usage: `ttt <person to challenge>`");
        CommandRegistrar.registerAlias("tic-tac-toe", "ttt");
    }
    
    @Override
    public void onMessage(GuildMessageReceivedEvent m) {}
    
    private static final HashMap<String, Integer> LIST_OF_EMOJI_ONE_THRU_NINE = new HashMap<>();
    
    static {
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0031", 1);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0032", 2);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0033", 3);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0034", 4);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0035", 5);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0036", 6);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0037", 7);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0038", 8);
        LIST_OF_EMOJI_ONE_THRU_NINE.put("\u0039", 9);
    }
    
    @Override
    public void onAnyEvent(GenericEvent ev) {
        if(ev instanceof GuildMessageReactionAddEvent) {
            try {
                GuildMessageReactionAddEvent e = (GuildMessageReactionAddEvent) ev;
                if (PLAYING.containsKey(e.getMessageId())) {
                    if (!e.getReactionEmote().getEmoji().matches("[\u0031-\u0039]"))
                        return;
                    String memberID = e.getMember().getId();
                    //check if the member is a player
                    if(memberID.equals(PLAYING.get(e.getMessageId()).player1) || memberID.equals(PLAYING.get(e.getMessageId()).player2))
                        update(e.getMessageId(), LIST_OF_EMOJI_ONE_THRU_NINE.get(e.getReactionEmote().getEmoji()), e.getMember().getId());
                }
            } catch(Throwable ignored) {}
        }
    }
}
