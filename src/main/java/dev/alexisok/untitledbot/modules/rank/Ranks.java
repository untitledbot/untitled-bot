package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.*;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import dev.alexisok.untitledbot.data.UserDataFileCouldNotBeCreatedException;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Daily;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.CheckForSigned;
import javax.annotation.CheckReturnValue;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This plugin implements ranks.  it handles most things ranks.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Ranks extends UBPlugin implements MessageHook {
    
    //0th element is level one
    //1664510 is max value because using this formula level 1024 goes over ((2^63) - 1)
    private static final long[] XP_REQUIRED_FOR_LEVEL_UP = new long[65535];
//                                                                   {50, 100, 125, 150, 200, 250, 400, 500, 700, 900, 1000,
//                                                                   1250, 1500, 2000, 2500, 3000, 3500, 4000, 5000, 6000,
//                                                                   7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000,
//                                                                   15000, 16000, 17000, 18000, 19000, 20000, 25000, 30000,
//                                                                   35000, 40000, 45000, 50000, 55000, 60000, 65000, 70000,
//                                                                   80000, 90000, 100000, 150000, 200000, 400000, 800000,
//                                                                   1000000, 2000000, 3000000, 5000000, 8000000, 10000000};
    
    static {
        for(int i = 0; i < XP_REQUIRED_FOR_LEVEL_UP.length; i++) {
            XP_REQUIRED_FOR_LEVEL_UP[i] = (long) ((Math.pow(i, 3) * 2L) + 250L);
        }
    }
    
    //since xp always starts at 0 and ends at 1 through 100, just store the max value here.
    private static final HashMap<Long, Integer> XP_PER_MESSAGE_CACHE = new HashMap<>();
    
    @Override
    public void onStartup() {
        super.onStartup();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.registerHook(this);
        CommandRegistrar.register("rank", this);
        CommandRegistrar.register("rank-total", new Total());
        CommandRegistrar.register("rank-top", new Top());
        CommandRegistrar.register("rank-settings", UBPerm.MANAGE_MESSAGES, new RankSettings());
        new Daily().onRegister();
//        new Shop().onRegister();
        Manual.setHelpPage("rank-top", "Get the top user ranks for the server.\n" +
                                               "Usage: `top`");
        Manual.setHelpPage("rank", "Get your (or another user's) rank.\nUsage: `rank [user @ | user ID]`");
        Manual.setHelpPage("rank-total", "Get the total amount of experience of yourself or another user.\n" +
                                                 "Usage: rank-total [user @]");
        Manual.setHelpPage("rank-settings", "Set the rank settings.\n" +
                                                    "Usage: `%srank-settings <setting> <value>`\n" +
                                                    "Current settings:\n" +
                                                    "\t`%sannounce-level-up <current | channel <channel #> | none>`\n" +
                                                    "\t`%smax-xp <number>`");
        CommandRegistrar.registerAlias("rank-top", "ranktop", "leaderboard", "top", "ranklist", "bottom");
        CommandRegistrar.registerAlias("rank", "level");
        Vault.addDefault("ranks-xp", "0");
        Vault.addDefault("ranks-level", "1");
        
    }
    
    /**
     * Get the amount of xp needed for this level.
     * @param i the level.
     * @return the xp needed or {@code -1} if you are level 100.
     */
    @CheckForSigned
    @CheckReturnValue
    public static long xpNeededForLevel(int i) {
        if(i == 0 || i > 65535)
            throw new IllegalArgumentException("\"i\" must be between 1 and 65535 (inclusive)");
        if(i == 65535)
            return -1;
        return XP_REQUIRED_FOR_LEVEL_UP[i - 1];
    }
    
    /**
     * Get the level from xp provided
     * @param XP the XP provided
     * @return the level
     */
    public static int getLevelForXP(long XP) {
        long current = XP;
        int i = 1;
        try {
            while (current >= XP_REQUIRED_FOR_LEVEL_UP[i]) {
                current -= XP_REQUIRED_FOR_LEVEL_UP[i];
                i++;
            }
        } catch(ArrayIndexOutOfBoundsException ignored) {
            return 65536;
        }
        return i;
    }
    
    
    /**
     * Get the remainder of {@link #getLevelForXP(long)}
     * @param XP the XP provided
     * @return the remainder
     */
    public static long getLevelForXPRemainder(long XP) {
        long current = XP;
        int i = 1;
        try {
            while (current >= XP_REQUIRED_FOR_LEVEL_UP[i]) {
                current -= XP_REQUIRED_FOR_LEVEL_UP[i];
                i++;
            }
        } catch(ArrayIndexOutOfBoundsException ignored) {
            return Long.MAX_VALUE;
        }
        return current;
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Nullable
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        message.getChannel().sendTyping().queue();
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        String xp;
        String lv;
        
        boolean other = false;
        
        if(!Main.HAS_MEMBERS_INTENT)
            message.getChannel().sendMessage("Due to the bot not having its Members Intent accepted, image generation no longer shows the " +
                    "user avatar or name.  Sorry about that :(\n" +
                    "I should have the members intent back up in a week or so, please be patient :)").queue();
        
        try {
            int s = message.getMentionedMembers().size();
            Member target = s == 1 ? message.getMentionedMembers().get(0) : null;
            if(target == null) {
                target = message.getGuild().getMembersByName(args[1], true).get(0);
            }
            if(target == null) {
                target = message.getGuild().getMemberById(args[1]);
            }
            assert target != null;
            xp = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-level");
            other = true;
        } catch(Throwable ignored) {
            xp = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-level");
        }
        
        if (lv == null || xp == null) {
            eb.setColor(Color.RED);
            eb.addField("Ranking", "Could not get the rank of this user.  Have they talked in this server before?", false);
            return eb.build();
        }
        
        if(lv.equalsIgnoreCase("65535")) lv = "MAX (65535)";
    
        eb.setColor(Color.GREEN);
        if(!other) {
            try {
                File f = Objects.requireNonNull(RankImageRender.render(message.getAuthor().getId(), message.getGuild().getId(), message.getIdLong(), message));
                message.getChannel().sendFile(f).queue(done -> Logger.log("Deleting file: " + f.delete()));
                return null;
            } catch(InsufficientPermissionException | NullPointerException | IOException e) {
                e.printStackTrace();
                try {
                    eb.addField("Ranking",
                            "Could not send image, falling back to text.\n" +
                                    "Level: " + lv + "\n" +
                                    "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                            false);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                    //THIS SHOULD ONLY BE CAUGHT IF THE USER IS THE HIGHEST LEVEL
                    eb.addField("Ranking",
                            "Could not send image, falling back to text.\n" +
                                    "Level: " + lv + "\n" +
                                    "Exp:   " + xp + "\n",
                            false);
                }
            }
        } else {
            try {
                int size = message.getMentionedMembers().size();
                User target = size == 1 ? message.getMentionedMembers().get(0).getUser() : null;
                try {
                    if(target == null)
                        target = message.getGuild().getMembersByEffectiveName(args[1], true).get(0).getUser();
                } catch(Throwable ignored) {}
                if(target == null)
                    target = message.getJDA().getUserById(args[1]);
                File f = Objects.requireNonNull(RankImageRender.render(Objects.requireNonNull(target).getId(), message.getGuild().getId(), message.getIdLong(), message));
                message.getChannel().sendFile(f).queue(done -> Logger.log("Deleting file: " + f.delete()));
                return null;
            } catch(InsufficientPermissionException | NullPointerException ignored2) {
                try {
                    eb.addField("Ranking",
                            "Stats for this user:\n" +
                                    "Level: " + lv + "\n" +
                                    "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                            false);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                    //THIS SHOULD ONLY BE CAUGHT IF THE USER IS THE HIGHEST LEVEL
                    eb.addField("Ranking",
                            "Stats for this user:\n" +
                                    "Level: " + lv + "\n" +
                                    "Exp:   " + xp + "\n",
                            false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return eb.build();
    }

    /**
     * Get the amount of XP per message
     * @param guildID the ID of the guild
     * @return the amount per message.
     */
    private static synchronized int getPerMessage(long guildID) {
        if(XP_PER_MESSAGE_CACHE.containsKey(guildID))
            return XP_PER_MESSAGE_CACHE.get(guildID);
        int v = Integer.parseInt(Vault.getUserDataLocalOrDefault(null, String.valueOf(guildID), "ranks.permsg", "3"));
        XP_PER_MESSAGE_CACHE.put(guildID, v);
        return v;
    }
    
    
    
    @Override
    public void onMessage(GuildMessageReceivedEvent mre) {
        
        //no bots
        if(mre.getAuthor().isBot())
            return;
        
        Message m = mre.getMessage();
        if(!m.isFromGuild())
            return;
        
        //do not do rank stuffs if there is a command
        if(m.getContentRaw().startsWith(BotClass.getPrefix(mre.getGuild().getIdLong(), -1)))
            return;
        
        long randomAmount = 0L;
        try {
            //was 3 to 5
            randomAmount = ThreadLocalRandom.current().nextLong(0,
                    1 + Long.parseLong(Vault.getUserDataLocalOrDefault(m.getAuthor().getId(),
                            m.getGuild().getId(),
                            "ranks-level", "1")) +
                            getPerMessage(m.getGuild().getIdLong())
            );
        } catch(Throwable ignored) {
            Logger.log("Error: there was an error with boost amount for user " + mre.getAuthor().getId() + " in guild " + mre.getGuild().getId());
        }
        if(randomAmount != 0)
            doLevelStuff(m, randomAmount);
    }
    
    public static void doLevelStuff(@NotNull Message m, long randAdd) {
        String xpstr = Vault.getUserDataLocalOrDefault(m.getAuthor().getId(), m.getGuild().getId(), "ranks-xp", "0");
        String lvstr = Vault.getUserDataLocalOrDefault(m.getAuthor().getId(), m.getGuild().getId(), "ranks-level", "1");
        
        //make sure the user is not level 0
        if(lvstr.equals("0"))
            lvstr = "1";
        
        long currentXP = Long.parseLong(xpstr);
        int currentLv = Integer.parseInt(lvstr);
    
        if (currentLv > 65535 || currentXP >= Long.MAX_VALUE - 1000L) return;
        
        currentXP += randAdd;
        
        //check level up
        if(currentLv != 65535 && currentXP >= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1]) {
            currentXP -= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1];
            currentLv++;
            try {
                String rankToReward = Vault.getUserDataLocal(null, m.getGuild().getId(), "role.reward." + currentLv);
                String roleMessage = "";
                if(rankToReward != null && !rankToReward.equals("none")) {
                    try {
                        m.getGuild().addRoleToMember(m.getAuthor().getId(), Objects.requireNonNull(m.getGuild().getRoleById(rankToReward))).queue();
                        roleMessage = String.format("You have also been awarded the role %s!!!", Objects.requireNonNull(m.getGuild().getRoleById(rankToReward)).getName());
                    } catch(Throwable ignored) {
                        try {
                            roleMessage = String.format("I would also assign you role %s, but I don't have access to it!", Objects.requireNonNull(m.getGuild().getRoleById(rankToReward)).getName());
                        } catch(Throwable ignored2) {
                            roleMessage = String.format("I would assign you the role for level %d but there was an error getting the role name!  Was it deleted?", currentLv);
                        }
                    }
                }
                String shouldSendPhase2 = Vault.getUserDataLocal(null, m.getGuild().getId(), "ranks-broadcast.rankup");
                if(shouldSendPhase2 == null) {
                    shouldSendPhase2 = "current";
                }
                
                if(shouldSendPhase2.equalsIgnoreCase("current")) {
                    m
                            .getChannel()
                            .sendMessage(String.format("Nice %s!  You have leveled up to level %d!%n%s", m.getAuthor().getName(), currentLv, roleMessage))
                            .queue(r -> BotClass.addToDeleteCache(m.getId(), r));
                } else if(shouldSendPhase2.equalsIgnoreCase("channel")) {
                    Objects.requireNonNull(Main.getTextChannelById(Objects.requireNonNull(Vault.getUserDataLocal(null, m.getGuild().getId(), "ranks-broadcast.rankup.channel"))))
                            .sendMessage(String.format("%s has leveled up to level %d!%n%s", m.getAuthor().getName(), currentLv, roleMessage))
                            .queue(r -> BotClass.addToDeleteCache(m.getId(), r));
                }
                
                //if all else fails, don't do anything.
                
            } catch(InsufficientPermissionException | UserDataCouldNotBeObtainedException | UserDataFileCouldNotBeCreatedException ignored) {}
        }
        
        Vault.storeUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-xp", String.valueOf(currentXP));
        Vault.storeUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-level", String.valueOf(currentLv));
    }
    
    //unused
    @Override
    public void onAnyEvent(GenericEvent e) {}
    
    /**
     * Get the total XP a user has from all of their levels.
     * @param user the user ID
     * @param guild the guild ID
     * @return the total amount of xp, or {@code 0} if there is none (xp or lv is null).
     */
    protected static long totalXPFromAllLevels(String user, String guild) {
        long returnLong = 0L;
    
        String xpS = Vault.getUserDataLocal(user, guild, "ranks-xp");
        String lvS = Vault.getUserDataLocal(user, guild, "ranks-level");
    
        if (lvS == null || xpS == null) return returnLong;
        
        long xp = Long.parseLong(xpS);
        int lv = Integer.parseInt(lvS);
        
        returnLong += xp;
        
        if(lv == 0) return returnLong;
        
        for(int i = 0; i < lv; i++) {
            try {
                returnLong += XP_REQUIRED_FOR_LEVEL_UP[i];
            } catch(ArrayIndexOutOfBoundsException ignored){}
        }
        
        //250 was being added in the loop, should remove it here.
        returnLong = returnLong - 250;
        
        return returnLong;
    }
}
