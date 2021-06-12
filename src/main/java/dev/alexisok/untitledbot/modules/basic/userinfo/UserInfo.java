package dev.alexisok.untitledbot.modules.basic.userinfo;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.DateFormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.Objects;

/**
 * Get information on a user
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public final class UserInfo extends UBPlugin {
    
    @Override
    public @NotNull MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Member u = null;
        
        if(args.length == 1 && !args[0].equals("info") && message.getMentionedMembers().size() != 1)
            u = message.getMember();
        
        if(args.length == 1 && args[0].equals("info")) {
            //server info
            eb.setThumbnail(message.getGuild().getIconUrl());
            Guild.VerificationLevel ve = message.getGuild().getVerificationLevel();
            
            int verificationLevel = ve.getKey();
            String veStr;
            
            switch(verificationLevel) {
                case 0:
                    veStr = "None (unrestricted)";
                    break;
                case 1:
                    veStr = "Low (verified email)";
                    break;
                case 2:
                    veStr = "Medium (Discord account older than 5 minutes)";
                    break;
                case 3:
                    veStr = "(╯°□°）╯︵ ┻━┻ (server member > 10 minutes)";
                    break;
                case 4:
                    veStr = "┻━┻ ﾐヽ(ಠ益ಠ)ノ彡┻━┻ (verified phone)";
                    break;
                case -1:
                    veStr = "i can't see the level i think someone turned off the lights";
                    break;
                default:
                    veStr = "you should never see this text ever and if you do then please report it right away";
            }
            
            Guild g = message.getGuild();
            
            String sysChannel;
            try {
                sysChannel = String.format("<#%s>", Objects.requireNonNull(g.getSystemChannel()).getId());
            } catch(NullPointerException ignored) {
                sysChannel = "none that i can see";
            }
            
            eb.setThumbnail(g.getIconUrl());
            
            eb.addField("Basic info",
                    String.format("Name: %s%n" +
                                          "Users: %d%n" +
                                          "Roles: %d%n" +
                                          "Emojis: %d%n" +
                                          "Security level: %s%n" +
                                          "Server region: %s%n" +
                                          "System message channel: %s%n",
                            g.getName(), g.getMemberCount(), g.getRoles().size(), g.getEmotes().size(),
                            veStr, g.getRegionRaw().toLowerCase(), sysChannel),
                    false);
            eb.addField("Other information",
                    String.format("ID: %s%n" +
                                          "Creation date: %s%n" +
                                          "MFA Level: %s%n" +
                                          "AFK timeout: %d minutes%n" +
                                          "Default notification level: %s%n" +
                                          "Explicit content level: %s%n" +
                                          "Max bitrate: %dkbps%n" +
                                          "Max emotes: %d%n" +
                                          "Max file size: %dMiB%n" +
                                          "Owner: <@%s>%n" +
                                          "Channels: %d%n" +
                                          "Voice channels: %d%n" +
                                          "Categories: `%d`%n",
                            g.getId(), g.getTimeCreated().toInstant().toString(), g.getRequiredMFALevel().toString().toLowerCase(),
                            g.getAfkTimeout().getSeconds() / 60, g.getDefaultNotificationLevel().toString().toLowerCase(),
                            g.getExplicitContentLevel().getDescription(), g.getBoostTier().getMaxBitrate() / 1000,
                            g.getBoostTier().getMaxEmotes(), g.getMaxFileSize() / 1024 / 1024, g.getOwnerId(),
                            g.getTextChannels().size(), g.getVoiceChannels().size(), g.getCategories().size()), false);
            eb.addField("Nitro stuffs",
                    String.format("Boosters: %d%n" +
                                          "Boosts: %d%n" +
                                          "Tier: %d%n",
                            g.getBoosters().size(), g.getBoostCount(), g.getBoostTier().getKey()), false);
            eb.setThumbnail(message.getGuild().getIconUrl());
            return eb.build();
        } else if(message.getMentionedMembers().size() == 1 || args.length == 2) {
            //member info
            
            if(u == null) {
                try {
                    u = message.getGuild().getMembersByName(args[1], true).get(0);
                } catch(Throwable ignored) {}
            }
            
            if(u == null) {
                try {
                    u = message.getGuild().getMemberById(args[1]);
                } catch(Throwable ignored) {}
            }
            
            if(u == null) {
                eb.addField("Info", "Could not find a user by that mention or ID.", false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
            eb.setThumbnail(u.getUser().getAvatarUrl());
            String highestRole = "None";
            try {
                 highestRole = u.getRoles().get(0).getAsMention();
            } catch(Throwable ignored) {}
            
            StringBuilder roles = new StringBuilder();
            
            int i = 0;
            for(Role r : u.getRoles()) {
                roles.append(r.getAsMention()).append("\n");
                i++;
                if(i > 10)
                    break;
            }
            
            eb.addField("**Member**", String.format("Name: %s%nJoin date: %s%nHighest role: %s%n" +
                                                            "Top 10 roles: %n%s%nFor permissions, do `permissions <user>`",
                    u.getEffectiveName(), u.getTimeJoined(), highestRole, roles
            ), false);
            
            eb.addField("**User**", String.format("Name: %s%nCreation date: %s%nBot: %s%nID: %s%n",
                    u.getUser().getName() + "#" + u.getUser().getDiscriminator(), u.getUser().getTimeCreated(), u.getUser().isBot(),
                    u.getId()), false);
    
            String boostingSince = "Not boosting";
            try {
                boostingSince = Objects.requireNonNull(u.getTimeBoosted()).toString();
            } catch(Throwable ignored){}
            eb.addField("**Nitro Stuffs**", String.format("Boosting since: %s%n",
                    boostingSince), false);
            
            eb.setColor(u.getColor());
            return eb.build();
        } else {
            eb.addField("**Info**", "Usage: `info [user @]`, leave the argument blank for server info.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("userinfo", this);
        CommandRegistrar.registerAlias("userinfo", "info", "ui");
    }
}
