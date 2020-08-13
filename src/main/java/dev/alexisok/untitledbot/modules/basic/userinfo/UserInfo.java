package dev.alexisok.untitledbot.modules.basic.userinfo;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Get information on a user
 * 
 * @author AlexIsOK
 * @since 1.3
 */
public class UserInfo extends UBPlugin {
    
    @Override
    public @Nullable MessageEmbed onCommand(@NotNull String[] args, @NotNull Message message) {
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
                    veStr = "i can't see it i need the permission";
                    break;
                default:
                    veStr = "pls report this a bug happened user info verstr was default branch or something gdnhjsbhjaf";
            }
            
            Guild g = message.getGuild();
            
            String sysChannel;
            try {
                sysChannel = String.format("<#%s>", g.getSystemChannel().getId());
            } catch(NullPointerException ignored) {
                sysChannel = "none that i can see";
            }
            
            
            eb.addField("Basic info",
                    String.format("Name: %s%n" +
                                          "User count: %d%n" +
                                          "Role count: %d%n" +
                                          "Emoji count: %d%n" +
                                          "Security level: %s%n" +
                                          "Server region: %s%n" +
                                          "System message channel: %s%n",
                            g.getName(), g.getMemberCount(), g.getRoles().size(), g.getEmotes().size(),
                            veStr, g.getRegionRaw().toLowerCase(), sysChannel),
                    true);
            eb.addField("Other information",
                    String.format("ID: %s%n" +
                                          "Creation date: %s%n" + //TODO test this
                                          "MFA Level: %s%n" +
                                          "AFK timeout: %d seconds%n" +
                                          "Default notification level: %s%n",
                            g.getId(), g.getTimeCreated().toInstant().toString(), g.getRequiredMFALevel().toString(),
                            g.getAfkTimeout().getSeconds(), g.getDefaultNotificationLevel().toString()), true);
            eb.addField("Nitro stuffs",
                    String.format("Boosters: %d%n" +
                                          "Boosts: %d%n" +
                                          "Tier: %d%n" +
                                          ""), true);
            
            return eb.build();
        } else if(message.getMentionedMembers().size() == 1) {
            //member info
            User u = message.getMentionedMembers().get(0).getUser();
            
            return null; //TODO
            
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
