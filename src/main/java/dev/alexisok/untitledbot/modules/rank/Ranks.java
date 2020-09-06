package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.MessageHook;
import dev.alexisok.untitledbot.data.UserDataCouldNotBeObtainedException;
import dev.alexisok.untitledbot.data.UserDataFileCouldNotBeCreatedException;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Daily;
import dev.alexisok.untitledbot.modules.rank.xpcommands.Shop;
import dev.alexisok.untitledbot.modules.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This plugin implements ranks.  it handles most things ranks.
 * 
 * @author AlexIsOK
 * @since 0.0.1
 */
public final class Ranks extends UBPlugin implements MessageHook {
    
    //0th element is level one
    private static final long[] XP_REQUIRED_FOR_LEVEL_UP = new long[100];
//                                                                   {50, 100, 125, 150, 200, 250, 400, 500, 700, 900, 1000,
//                                                                   1250, 1500, 2000, 2500, 3000, 3500, 4000, 5000, 6000,
//                                                                   7000, 8000, 9000, 10000, 11000, 12000, 13000, 14000,
//                                                                   15000, 16000, 17000, 18000, 19000, 20000, 25000, 30000,
//                                                                   35000, 40000, 45000, 50000, 55000, 60000, 65000, 70000,
//                                                                   80000, 90000, 100000, 150000, 200000, 400000, 800000,
//                                                                   1000000, 2000000, 3000000, 5000000, 8000000, 10000000};
    
    @Override
    public void onStartup() {
        super.onStartup();
    }
    
    @Override
    public void onRegister() {
        setupXPRequired();
        CommandRegistrar.registerHook(this);
        CommandRegistrar.register("rank", "core.ranks", this);
        CommandRegistrar.register("rank-total", "core.ranks", new Total());
        CommandRegistrar.register("rank-top", "core.ranks", new Top());
        CommandRegistrar.register("rank-settings", "admin", new RankSettings());
        new Daily().onRegister();
        new Shop().onRegister();
        Manual.setHelpPage("rank-top", "Get the top user ranks for the guild.\n" +
                                               "Usage: `top [amnt]`");
        Manual.setHelpPage("rank", "Get your (or another user's) rank.\nUsage: `rank [user @ | user ID]`");
        Manual.setHelpPage("rank-total", "Get the total amount of experience of yourself or another user.\n" +
                                                 "Usage: rank-total [user @]");
        Manual.setHelpPage("rank-settings", "Set the rank settings.\n" +
                                                    "Usage: `rank-settings <setting> <value>`\n" +
                                                    "Current settings:\n" +
                                                    "\tannounce-xp-boost <true | false>\n" +
                                                    "\tannounce-level-up <current | channel <channel #> | none>\n");
        CommandRegistrar.registerAlias("rank-top", "ranktop", "leaderboard", "top", "ranklist");
        CommandRegistrar.registerAlias("rank", "level");
        Vault.addDefault("ranks-xp", "0");
        Vault.addDefault("ranks-level", "1");
        
        DoubleXPTime.installer();
    }
    
    private static void setupXPRequired() {
        for(int i = 0; i < XP_REQUIRED_FOR_LEVEL_UP.length; i++) {
            XP_REQUIRED_FOR_LEVEL_UP[i] = (long) ((Math.pow(i, 3) * 2) + 250L);
        }
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(!message.isFromGuild()) {
            eb.setColor(Color.RED);
            eb.addField("Error", "You must run this in a guild!", false);
            return eb.build();
        }
        
        String xp;
        String lv;
        
        boolean other = false;
        
        try {
            int s = message.getMentionedMembers().size();
            User target = s == 1 ? message.getMentionedMembers().get(0).getUser() : Main.jda.getUserById(args[1]);
            assert target != null;
            xp = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-level");
            other = true;
        } catch(Exception ignored) {
            xp = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-xp");
            lv = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-level");
        }
    
        
    
        if(lv == null || xp == null) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                int size = message.getMentionedMembers().size();
                User target = size == 1 ? message.getMentionedMembers().get(0).getUser() : Main.jda.getUserById(args[1]);
                assert target != null;
                xp = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-xp");
                lv = Vault.getUserDataLocal(target.getId(), message.getGuild().getId(), "ranks-level");
                other = true;
            } catch(Exception ignored) {
                xp = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-xp");
                lv = Vault.getUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "ranks-level");
            }
        }
        
        if (lv == null || xp == null) {
            eb.setColor(Color.RED);
            eb.addField("Error", "Looks like the level or XP is null..... this is awkward.....", false);
            return eb.build();
        }
        
        if(lv.equalsIgnoreCase("100")) lv = "MAX (100)";
    
        eb.setColor(Color.GREEN);
        if(!other) {
            try {
                eb.addField("Ranking",
                        "\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                        false);
            } catch(ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                //THIS SHOULD ONLY BE CAUGHT IF THE USER IS THE HIGHEST LEVEL
                eb.addField("Ranking",
                        "\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "\n",
                        false);
            }
        } else {
            try {
                eb.addField("Ranking",
                        "Stats for this user:\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "/" + XP_REQUIRED_FOR_LEVEL_UP[Integer.parseInt(lv) - 1] + "\n",
                        false);
            } catch(ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
                //THIS SHOULD ONLY BE CAUGHT IF THE USER IS THE HIGHEST LEVEL
                eb.addField("Ranking",
                        "Stats for this user:\n" +
                                "Level: " + lv + "\n" +
                                "Exp:   " + xp + "\n",
                        false);
            }
        }
        return eb.build();
    }
    
    @Override
    public void onMessage(@NotNull MessageReceivedEvent mre) {
        
        //no bots
        if(mre.getAuthor().isBot())
            return;
        
        Message m = mre.getMessage();
        if(!m.isFromGuild())
            return;
        
        doLevelStuff(m, ThreadLocalRandom.current().nextLong(3 * DoubleXPTime.boostAmount, 5 * DoubleXPTime.boostAmount));
    }
    
    public static void doLevelStuff(@NotNull Message m, long randAdd) {
        String xpstr = Vault.getUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-xp");
        String lvstr = Vault.getUserDataLocal(m.getAuthor().getId(), m.getGuild().getId(), "ranks-level");
        
        if (lvstr == null || xpstr == null) {
            xpstr = "0";
            lvstr = "1";
        }
        
        //make sure the user is not level 0
        if(lvstr.equals("0"))
            lvstr = "1";
        
        long currentXP = Long.parseLong(xpstr);
        int currentLv = Integer.parseInt(lvstr);
    
        if (currentLv > 99 && currentXP >= Long.MAX_VALUE - 10L) return;
        
        if(DoubleXPTime.boostAmount != 1)
            DoubleXPTime.totalXPFromBoost = DoubleXPTime.totalXPFromBoost.add(new BigInteger(String.valueOf(randAdd)));
        
        currentXP += randAdd;
        
        //check level up
        if(currentLv != 100 && currentXP >= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1]) {
            currentXP -= XP_REQUIRED_FOR_LEVEL_UP[currentLv - 1];
            currentLv++;
            try {
                String rankToReward = Vault.getUserDataLocal(null, m.getGuild().getId(), "role.reward." + currentLv);
                String roleMessage = "";
                if(rankToReward != null && !rankToReward.equals("none")) {
                    try {
                        m.getGuild().addRoleToMember(m.getAuthor().getId(), Objects.requireNonNull(m.getGuild().getRoleById(rankToReward))).queue();
                        roleMessage = String.format("You have also been awarded rank %s!!!", Objects.requireNonNull(m.getGuild().getRoleById(rankToReward)).getName());
                    } catch(Throwable ignored) {
                        try {
                            roleMessage = String.format("I would also assign you rank %s, but I don't have access to it!", Objects.requireNonNull(m.getGuild().getRoleById(rankToReward)).getName());
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
                            .queue();
                } else if(shouldSendPhase2.equalsIgnoreCase("channel")) {
                    Objects.requireNonNull(Main.jda.getTextChannelById(Objects.requireNonNull(Vault.getUserDataLocal(null, m.getGuild().getId(), "ranks-broadcast.rankup.channel"))))
                            .sendMessage(String.format("%s has leveled up to level %d!%n%s", m.getAuthor().getName(), currentLv, roleMessage))
                            .queue();
                } else if(shouldSendPhase2.equalsIgnoreCase("none")) {
                    throw new UserDataFileCouldNotBeCreatedException("nothing 2");
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
