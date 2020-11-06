package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.data.UserDataFileCouldNotBeCreatedException;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class handles communicating with Discord.
 * This class is not final, as you are allowed to override
 * the methods in this class (besides the message received
 * methods).
 *
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class BotClass extends ListenerAdapter {
    
    /**
     * Only allow package-private instances of this class.
     */
    BotClass() {}
    
    {
        Logger.log("new instance of BotClass created:  " + this.toString());
    }
    
    private static long messagesSentTotal = 0L;
    
    public static long getMessagesSentTotal() {
        return messagesSentTotal;
    }
    
    public static final ArrayList<String> BLACKLIST = new ArrayList<>();
    
    private static final ArrayList<String> NO_PREFIX = new ArrayList<>();
    
    //message id, bot msg id reply
    private static final HashMap<String, Message> DELETE_THIS_CACHE = new HashMap<>();

    /**
     * For methods that do not send messages through traditional means.
     * @param messageID the ID of the user's message.
     * @param reply the bots reply to the message.
     */
    public static void addToDeleteCache(@NotNull String messageID, @NotNull Message reply) {
        DELETE_THIS_CACHE.put(messageID, reply);
    }
    
    private static final MessageEmbed JOIN_MESSAGE = new EmbedBuilder().addField("untitled-bot",
            "\n```" +
                    "      ╔╗ ╔╗╔╗     ╔╗  ╔╗    ╔╗\n" +
                    "     ╔╝╚╦╝╚╣║     ║║  ║║   ╔╝╚╗\n" +
                    "╔╗╔╦═╬╗╔╬╗╔╣║╔══╦═╝║  ║╚═╦═╩╗╔╝\n" +
                    "║║║║╔╗╣║╠╣║║║║║═╣╔╗╠══╣╔╗║╔╗║║\n" +
                    "║╚╝║║║║╚╣║╚╣╚╣║═╣╚╝╠══╣╚╝║╚╝║╚╗\n" +
                    "╚══╩╝╚╩═╩╩═╩═╩══╩══╝  ╚══╩══╩═╝\n" +
                    "```" +
                    "\n\n\n" +
                    "Thank you for inviting untitled-bot!\n" +
                    "For help with the bot, use `" + Main.PREFIX + "help`\n" +
                    "The website for the bot is [here](https://untitled-bot.xyz), and the " +
                    "official support server is [here](https://discord.gg/vSWgQ9a).", false).build();
    
    //cache for server prefixes
    private static final HashMap<String, String> PREFIX_CACHE = new HashMap<>();
    
    private static final Timer TIMER = new Timer(true);
    
    /**
     * Nullify the prefix cache for a specific guild.
     * @param guildID the ID of the guild.
     */
    public static void nullifyPrefixCacheSpecific(@NotNull String guildID) {
        PREFIX_CACHE.remove(guildID);
    }
    
    /**
     * Update the cached prefix for a guild.
     * @param guildID the ID of the guild as a String
     * @param prefix the prefix of that guild
     */
    public static void updateGuildPrefix(@NotNull String guildID, @NotNull String prefix) {
        Logger.debug(String.format("Updating prefix cache to include %s for %s", prefix, guildID));
        PREFIX_CACHE.put(guildID, prefix);
        Logger.debug("Prefix cache updated.");
    }
    
    /**
     * Get the prefix of a guild if it is cached.
     *
     * Attempts to do the following:
     *
     * 1. get the prefix from the cache
     *
     * 2. if the prefix is not in the cache, load it
     *
     * 3. if the prefix does not exist, use ">"
     *
     * @param guildID the ID of the guild.
     * @param userID the ID of the user.  (Make {@code null} to skip user prefix).
     * @return the prefix.
     */
    @NotNull
    @Contract(pure = true)
    public static String getPrefix(@NotNull String guildID, String userID) {
        if(userID != null && NO_PREFIX.contains(userID))
            return "";
        String prefix;
        if(!PREFIX_CACHE.containsKey(guildID)) {
            prefix = Vault.getUserDataLocalOrDefault(null, guildID, "guild.prefix", ">");
            updateGuildPrefix(guildID, prefix);
        } else {
            prefix = PREFIX_CACHE.get(guildID);
        }
        
        return prefix == null ? ">" : prefix;
    }
    
    /**
     * Add a user to the no-prefix list.
     *
     * @param userID the ID of the user as a String.
     * @return {@code true} if the user was added, {@code false} if they were already added.
     */
    public static synchronized boolean addToNoPrefix(@NotNull String userID) {
        if(NO_PREFIX.contains(userID))
            return false;
        return NO_PREFIX.add(userID);
    }
    
    /**
     * Removes a user from the no-prefix list.
     * @param userID the ID of the user as a String.
     * @return {@code true} if they were removed, {@code false} if they were not removed.
     */
    public static synchronized boolean removeFromNoPrefix(@NotNull String userID) {
        return NO_PREFIX.remove(userID);
    }
    
    public static synchronized void voidPrefixCache() {
        PREFIX_CACHE.clear();
    }
    
    /**
     * This is messy...
     * @param event the mre
     */
    @Override
    public final synchronized void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        
        messagesSentTotal++;
        
        CommandRegistrar.runMessageHooks(event);
        
        if(event.getAuthor().isBot() || event.isWebhookMessage())
            return;
        
        if(BLACKLIST.contains(event.getAuthor().getId()))
            return;
        
        if(!event.getChannel().canTalk())
            return;
        
        //get the prefix of the guild
        String prefix = getPrefix(event.getGuild().getId(), event.getAuthor().getId());
        
        String message = event.getMessage().getContentRaw();
        
        if(message.startsWith(prefix + " "))
            message = message.replaceFirst(prefix + " ", prefix);
        
        try {
            //if the message is @untitled-bot
            if(event.getMessage().getMentionedMembers().get(0).getId().equals("730135989863055472")
                    && message.split(" ").length == 1) {
                
                if(prefix.equals(""))
                    event.getChannel().sendMessage("You seem to be in the NoPrefix:TM: mode, to exit, simply say `exit`.").queue();
                else
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            event.getChannel()
                                    .sendMessage(String.format("Hello!  My prefix for this guild is `%s`.%n" +
                                            "For a full list of commands, use `%shelp` or `%s help`.%n" +
                                            "The default prefix is `>` and can be set by an administrator " +
                                            "on this server by using the `prefix` command.", prefix, prefix, prefix))
                                    .queue(r -> {
                                        DELETE_THIS_CACHE.put(event.getMessageId(), r);
                                    });
                        }
                    }, 0);
                return;
            }
        } catch(IndexOutOfBoundsException ignored) {}
        
        if(!event.getMessage().getContentRaw().startsWith(prefix) || event.getMessage().getContentRaw().equals(prefix))
            return;
        
        message = message.substring(prefix.length());
        
        //replace all "  " with " "
        if(message.contains("  ")) {
            do message = message.replaceAll(" {2}", " ");
            while(message.contains("  "));
        }
    
        //args...
        String[] args = message.split(" ");
    
        //execute a command and return the message it provides
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    event.getChannel()
                            .sendMessage((Objects.requireNonNull(CommandRegistrar.runCommand(args[0], args, event.getMessage()))))
                            .queue(r -> DELETE_THIS_CACHE.put(event.getMessageId(), r));
                } catch(NullPointerException ignored) { //this returns null if the command does not exist.
                } catch(InsufficientPermissionException ignored) { //if the bot can't send messages (filled up logs before).
                    Logger.debug("Could not send a message to a channel.");
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }, 0);
        
    }

    /**
     * 
     * @param event
     */
    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        if(DELETE_THIS_CACHE.containsKey(event.getMessageId())) {
            Logger.debug("Deleting message");
            DELETE_THIS_CACHE.get(event.getMessageId()).delete().queue();
        }
    }

    private static final MessageEmbed onPrivateMessage;
    
    static {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setDescription("Hello!  To use untitled-bot, type `>help` in a server I'm in to get started.\n" +
                "If you changed the prefix, simply type @untitled-bot in the server to get my prefix.\n\nFor help with the bot, feel free to join the" +
                " [official server](https://alexisok.dev/ub/discord.html).");
        onPrivateMessage = eb.build();
    }
    
    @Override
    public final void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent e) {
        if(!e.getAuthor().getId().equals("730135989863055472")) {
            
            e.getAuthor().openPrivateChannel().queue(a -> a.sendMessage(onPrivateMessage).queue(
                    r -> BotClass.addToDeleteCache(e.getMessage().getId(), r)
            ));
        }
    }
    
    @Override
    public void onGenericEvent(@Nonnull GenericEvent event) {
        CommandRegistrar.runGenericListeners(event);
    }
    
    private static void onJoin(@NotNull Guild g) {
        User owner;
        String ownerTag = "ERROR";
        String ownerID = "ERROR";
        try {
            owner = Objects.requireNonNull(g.getOwner()).getUser();
            ownerID = owner.getId();
            ownerTag = owner.getAsTag();
        } catch(NullPointerException ignored) {}
        Main.jda.getTextChannelById(774205271282810911L).sendMessage(
                new EmbedBuilder().addField("Guild joined!", String.format("" +
                        "Name: %s%n" +
                        "Creation time: %s%n" +
                        "ID: %s%n" +
                        "Members: %s%n" +
                        "Owner tag: %s%n" +
                        "Owner ID: %s%n",
                        g.getName(), g.getTimeCreated().toString(),
                        g.getId(), g.getMembers().size(),
                        ownerTag,
                        ownerID), false).build()
        ).queue();
    }
    
    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent e) {
        onJoin(e.getGuild());
        List<TextChannel> ch = e.getGuild().getTextChannels();
        
        boolean found = false;
        
        try {
            for(TextChannel tc : ch) {
                if(tc.getName().contains("bot")) {
                    found = true;
                    tc.sendMessage(JOIN_MESSAGE).queueAfter(3000, TimeUnit.MILLISECONDS);
                    break;
                }
            }
            if(!found)
                throw new UserDataFileCouldNotBeCreatedException();
        } catch(InsufficientPermissionException | UserDataFileCouldNotBeCreatedException ignored) {
            try {
                Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(JOIN_MESSAGE).queueAfter(3000, TimeUnit.MILLISECONDS);
            } catch(InsufficientPermissionException | NullPointerException ignored2) {
                Logger.debug(String.format("Could not send a welcome message to %s.", e.getGuild().getId()));
            }
        }
    }
    
}
