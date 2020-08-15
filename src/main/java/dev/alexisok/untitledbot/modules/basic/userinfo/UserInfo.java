package dev.alexisok.untitledbot.modules.basic.userinfo;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
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
        
        if(args.length == 1) {
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
                    veStr = "pls report this a bug happened user info verstr was default branch or something gdnhjsbhjaf";
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
                    String.format("Name: `%s`%n" +
                                          "User count: `%d`%n" +
                                          "Role count: `%d`%n" +
                                          "Emoji count: `%d`%n" +
                                          "Security level: `%s`%n" +
                                          "Server region: `%s`%n" +
                                          "System message channel: %s%n",
                            g.getName(), g.getMemberCount(), g.getRoles().size(), g.getEmotes().size(),
                            veStr, g.getRegionRaw().toLowerCase(), sysChannel),
                    true);
            eb.addField("Other information",
                    String.format("ID: `%s`%n" +
                                          "Creation date: `%s`%n" +
                                          "MFA Level: `%s`%n" +
                                          "AFK timeout: `%d` minutes%n" +
                                          "Default notification level: `%s`%n" +
                                          "Explicit content level: `%s`%n" +
                                          "Max bitrate: `%dkbps`%n" +
                                          "Max emotes: `%d`%n" +
                                          "Max file size: `%dMiB`%n" +
                                          "Owner: <@%s>%n" +
                                          "Channels: `%d`%n" +
                                          "Voice channels: `%d`%n" +
                                          "Categories: `%d`%n",
                            g.getId(), g.getTimeCreated().toInstant().toString(), g.getRequiredMFALevel().toString().toLowerCase(),
                            g.getAfkTimeout().getSeconds() / 60, g.getDefaultNotificationLevel().toString().toLowerCase(),
                            g.getExplicitContentLevel().getDescription(), g.getBoostTier().getMaxBitrate() / 1000,
                            g.getBoostTier().getMaxEmotes(), g.getMaxFileSize() / 1024 / 1024, g.getOwnerId(),
                            g.getTextChannels().size(), g.getVoiceChannels().size(), g.getCategories().size()), true);
            eb.addField("Nitro stuffs",
                    String.format("Boosters: `%d`%n" +
                                          "Boosts: `%d`%n" +
                                          "Tier: `%d`%n" +
                                          "",
                            g.getBoosters().size(), g.getBoostCount(), g.getBoostTier().getKey()), true);
            return eb.build();
        } else if(message.getMentionedMembers().size() == 1) {
            //member info
            User u = message.getMentionedMembers().get(0).getUser();
            
            String userFlags = "";
            
            for(User.UserFlag uf : u.getFlags()) {
                userFlags += String.format("`%s`%n", uf.getName());
            }
            
            eb.setThumbnail(u.getAvatarUrl());
            eb.addField("Basic info", String.format("Username: `%s`%n" +
                                                            "Discriminator: `%s`%n" +
                                                            "Account creation time: `%s`%n",
                    u.getName(), u.getDiscriminator(), 
                    new Date((Long.parseLong(u.getId()) >> 22) + 1420070400000L).toString()), true);
            eb.addField("User flags", userFlags, true);
            eb.addField("Other info", String.format("" +
                                                            "ID: `%s`%n" +
                                                            "Raw flags: `%d`%n" +
                                                            "",
                    u.getId(), u.getFlagsRaw()), true);
            return eb.build();
        } else {
            eb.addField("Info", "Usage: `info [user @]`, leave the argument blank for server info.", false);
            eb.setColor(Color.RED);
            return eb.build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("userinfo", this);
        CommandRegistrar.registerAlias("userinfo", "info");
    }
}
