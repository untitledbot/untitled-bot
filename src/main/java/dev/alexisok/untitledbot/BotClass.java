package dev.alexisok.untitledbot;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.data.UserDataFileCouldNotBeCreatedException;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.basic.vote.OnVoteHook;
import dev.alexisok.untitledbot.modules.music.MusicKernel;
import dev.alexisok.untitledbot.util.hook.VoteHook;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.util.ShutdownHook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
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
    
    public static final List<ShutdownHook> HOOK_REGISTRAR = new ArrayList<>();
    
    private static final int MAX_INFRACTIONS = 3;
    
    //user id, commands
    private static final HashMap<Long, Integer> RATE_LIMIT = new HashMap<>();
    
    //user id, amount of times hit ratelimit
    private static final HashMap<Long, Byte> INFRACTIONS = new HashMap<>();
    
    //user id
    private static final ArrayList<Long> TEMP_BLACKLIST = new ArrayList<>();
    
    //user id
    public static final ArrayList<Long> BLACKLIST = new ArrayList<>();
    
    private static final ArrayList<VoteHook> VOTE_HOOKS = new ArrayList<>();
    
    static {
        TimerTask t = new TimerTask() {
            @Override
            public void run() {
                RATE_LIMIT.clear();
            }
        };
        
        TimerTask t2 = new TimerTask() {
            @Override
            public void run() {
                INFRACTIONS.clear();
            }
        };
        
        new Timer().scheduleAtFixedRate(t, 0, 60000); //1 minute
        new Timer().scheduleAtFixedRate(t2, 0, 3600000); //1 hour
    }
    
    public static void addHook(@NotNull VoteHook hook) {
        VOTE_HOOKS.add(hook);
    }
    
    /**
     * Add a user to the blacklist, also adds them to the blacklist.properties file.
     * @param userID the id of the user.
     * @return true if the user was added, false if they were already added or if there was an error.
     */
    @Contract
    public static boolean addToBlacklist(long userID) {
        if(BLACKLIST.contains(userID))
            return false;
        
        BLACKLIST.add(userID);
        
        Properties p = new Properties();
        try(FileInputStream fis = new FileInputStream("./blacklist.properties");
            FileOutputStream fos = new FileOutputStream("./blacklist.properties")) {
            p.load(fis);
            p.put(userID, 0);
            p.store(fos, "blacklist file");
            fos.flush();
            return true;
        } catch(IOException ignored) {
            return false;
        }
    }
    
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
                    "For help with the bot, use `>help`\n" +
                    "To change the prefix, use `>prefix <new prefix>`\n" +
                    "The website for the bot is [here](https://untitled-bot.xyz), and the " +
                    "official support server is [here](https://discord.gg/vSWgQ9a).", false).build();
    
    //cache for server prefixes
    private static final HashMap<String, String> PREFIX_CACHE = new HashMap<>();
    
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
     * Gets a nice prefix, so `>` as a prefix will return `>` but `ub` returns `ub `
     * @param id the id of the guild
     * @return the new prefix
     */
    @NotNull
    @Contract(pure = true)
    public static String getPrefixNice(String id) {
        String prefix = getPrefix(id, null);
        if((prefix.charAt(prefix.length() - 1) + "").matches("[A-Za-z0-9]"))
            prefix += " ";
        return prefix;
    }

    public static void registerVoteHook(OnVoteHook onVoteHook) {
        VOTE_HOOKS.add(onVoteHook);
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
        
        if(BLACKLIST.contains(event.getAuthor().getIdLong()) || TEMP_BLACKLIST.contains(event.getAuthor().getIdLong()))
            return;
        
        if(!event.getChannel().canTalk())
            return;
        
        //get the prefix of the guild
        String prefix = getPrefix(event.getGuild().getId(), event.getAuthor().getId());
        
        String message = event.getMessage().getContentRaw();
        
        if(message.startsWith(prefix + " "))
            message = message.replaceFirst(prefix + " ", prefix);
        
        try {
            //if the message starts with @untitled-bot
            if(message.split(" ")[0].equalsIgnoreCase("<@730135989863055472>") || message.split(" ")[0].equalsIgnoreCase("<@!730135989863055472>")) {
                
                if(prefix.equals(""))
                    event.getChannel().sendMessage("You seem to be in the NoPrefix:TM: mode, to exit, simply say `exit`.").queue();
                else
                    event.getChannel()
                            .sendMessage(String.format("Hello!  My prefix for this server is `%s`.%n" +
                                    "For a full list of commands, use `%shelp` or `%s help`.%n" +
                                    "The default prefix is `>` and can be set by an administrator " +
                                    "on this server by using the `prefix` command.", prefix, prefix, prefix))
                            .queue(r -> DELETE_THIS_CACHE.put(event.getMessageId(), r));
                return;
            }
        } catch(IndexOutOfBoundsException ignored) {}
        
        if(!event.getMessage().getContentRaw().startsWith(prefix) || event.getMessage().getContentRaw().equals(prefix))
            return;
        
        message = message.substring(prefix.length());
        
        message = message.replace("\n", " ");
        
        //replace all "  " with " "
        if(message.contains("  ")) {
            do message = message.replaceAll(" {2}", " ");
            while(message.contains("  "));
        }
        
        //args...
        String[] args = message.split(" ");
        
        //execute a command and return the message it provides
        try {
            args[0] = args[0].toLowerCase();
            Logger.debug("Exec command " + args[0] + " by " + event.getAuthor().getId());
            CommandRegistrar.runCommand(args[0], args, event.getMessage(), (embed) -> {
                if(embed == null)
                    return;
                event.getChannel()
                    .sendMessage((Objects.requireNonNull(embed)))
                    .queue(r -> DELETE_THIS_CACHE.put(event.getMessageId(), r)); 
            });
        } catch(NullPointerException e) { //this returns null if the command does not exist.
        } catch(InsufficientPermissionException ignored) { //if the bot can't send messages (filled up logs before).
            Logger.debug("Could not send a message to a channel.");
        } catch(Throwable t) {
            t.printStackTrace();
        }
        //this needs to be outside of everything else to avoid not being called.
        updateRL(event);
        
    }
    
    private static synchronized void updateRL(GuildMessageReceivedEvent event) {

        long authorID = event.getAuthor().getIdLong();

        if(RATE_LIMIT.containsKey(authorID)) {
            int amount = RATE_LIMIT.get(authorID);
            amount++;
            RATE_LIMIT.put(authorID, amount);
            if(INFRACTIONS.containsKey(authorID)) {
                byte i = INFRACTIONS.get(authorID);
                if(i >= MAX_INFRACTIONS) {
                    event.getChannel().sendMessage("\\* \\* \\* BANNED \\* \\* \\*\n" +
                            "<@" + authorID + ">, you have been temporarily banned from using the bot.\n" +
                            "").queue();
                    RATE_LIMIT.remove(authorID);
                    INFRACTIONS.remove(authorID);
                    TEMP_BLACKLIST.add(authorID);                            //12h
                    event.getAuthor().openPrivateChannel().queueAfter(43200000L, TimeUnit.MILLISECONDS, e -> {
                        try {
                            TEMP_BLACKLIST.remove(authorID);
                            e.sendMessage("You have been unbanned from using the bot.\n" +
                                    "Note: continue to break this rule and you will be banned from the bot forever.").queue();
                        } catch(Throwable ignored) {}
                    });
                    return;
                }
            }
            if(amount >= 13 - (INFRACTIONS.getOrDefault(authorID, (byte) 0) * 2)) {
                event.getChannel().sendMessage("Hey <@" + authorID + ">, slow down on the commands.\n" +
                        "You will be temporarily banned from the bot if you continue to spam.").queue();
                RATE_LIMIT.remove(authorID);
                INFRACTIONS.put(authorID, (byte) (INFRACTIONS.getOrDefault(authorID, (byte) 0) + 1));
            }
        } else {
            RATE_LIMIT.put(event.getAuthor().getIdLong(), 1);
        }
    }
    
    /**
     * when a guild message is deleted.  what else could it possibly be?
     * @param event the delete event
     */
    @Override
    public synchronized void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
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
                " [official server](https://alexisok.dev/ub/discord.html).\n\n" +
                "NOTE: any conversation with this bot is logged.  Don't send me anything too embarrassing!");
        onPrivateMessage = eb.build();
    }
    
    @Override
    public final synchronized void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent e) {
        if(!e.getAuthor().getId().equals("730135989863055472") && !e.getAuthor().getId().equals("716035864123408404")) {
            e.getAuthor().openPrivateChannel().queue(a -> a.sendMessage(onPrivateMessage).queue(
                    r -> BotClass.addToDeleteCache(e.getMessage().getId(), r)
            ));
            Logger.log("DM " + e.getAuthor().getId() + ": " + e.getMessage().getContentRaw().replace("\n", "\\n"));
        }
    }
    
    @Override
    public synchronized void onGenericEvent(@Nonnull GenericEvent event) {
        CommandRegistrar.runGenericListeners(event);
    }

    /**
     * It was pointed out to me that this could be useful.
     * Sometimes there are bad guilds that wouldn't be reported
     * otherwise and that they could be sort of filtered here.
     * 
     * Existing guilds won't be looked at, but this will be.
     * @param g the guild
     */
    private static synchronized void onJoin(@NotNull Guild g) {
        User owner;
        String ownerTag = "ERROR";
        String ownerID = "ERROR";
        try {
            owner = Objects.requireNonNull(g.getOwner()).getUser();
            ownerID = owner.getId();
            ownerTag = owner.getAsTag();
        } catch(NullPointerException ignored) {}
        Main.getTextChannelById("774205271282810911").sendMessage(
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
                        ownerID), false)
                        .setThumbnail(g.getIconUrl()).build()
        ).queue();
    }
    
    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent e) {
        onJoin(e.getGuild());
        List<TextChannel> ch = e.getGuild().getTextChannels();
        
        boolean found = false;
        voidPrefixCache(); //because broken bot stuffs
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
    
    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent e) {
        Logger.debug("Guild left");
    }
    
    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        MusicKernel.INSTANCE.onUserLeaveVC(event.getChannelLeft());
    }

    @Override
    public void onGuildVoiceMove(@NotNull GuildVoiceMoveEvent event) {
        MusicKernel.INSTANCE.onUserLeaveVC(event.getChannelLeft());
    }

    /**
     * Run when the bot is about to shutdown.
     * 
     * Note: this calls {@link Runtime#exit(int)} with a status code of {@code 0}.
     */
    public static void onShutdown() {
        HOOK_REGISTRAR.forEach(ShutdownHook::onShutdown);
        Runtime.getRuntime().exit(0);
    }
    
    public static void registerShutdownHook(@NotNull ShutdownHook hook) {
        HOOK_REGISTRAR.add(hook);
    }
    
    /**
     * Run when a user votes for the bot.
     * @param userID the ID of the user.
     */
    @SuppressWarnings("unused")
    public static void onVote(long userID) {
        VOTE_HOOKS.forEach(h -> h.onVote(userID));
    }
}
