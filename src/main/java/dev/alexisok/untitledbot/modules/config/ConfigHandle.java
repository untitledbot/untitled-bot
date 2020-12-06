package dev.alexisok.untitledbot.modules.config;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.modules.starboard.Starboard;
import dev.alexisok.untitledbot.util.vault.Vault;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;

/**
 * Uses the `config` command.
 * 
 * @author AlexIsOK
 * @since 1.3.21
 */
public final class ConfigHandle extends UBPlugin {
    
    private static final String CONFIG_GENERATOR_URL = "https://untitled-bot.xyz/config.html";
    
    @NotNull
    @Override
    @Contract(pure = true)
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        if(args.length == 1) {
            eb.addField("Easy Config", "Hello!  The config command allows for easy bot setup.\n" +
                                               "You can generate a config command [here](" + CONFIG_GENERATOR_URL + ").\n" +
                                               "", false);
            eb.setColor(Color.GREEN);
            return eb.build();
        }
        
        String check = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        String[] splitCheck = check.split(";");
        
        
        //check legal expressions, store after the command is 100% correct.
        for(String s : splitCheck) {
            try {
                String token = s.split(":")[0];
                String value = s.split(":")[1];
                if(!ConfigTokens.isLegal(token, value))
                    throw new ConfigTokenNotPresentException();
            } catch(ConfigTokenNotPresentException ignored){
                eb.addField("Error",
                        String.format("Hey, I had an error checking this key.\n" +
                                              "It seems as if the value %s is not acceptable for %s.\n" +
                                              "If you modified the config command, please revert it," +
                                              " if you didn't, please report this as a bug.",
                                s.split(":")[1],
                                s.split(":")[0]),
                        false);
                eb.setColor(Color.RED);
                return eb.build();
            } catch(Throwable t) {
                eb.addField("Error", "Looks like there is an error with the config code, and I couldn't" +
                                                           " seem to find where it is.  Please report this by either joining the " +
                                                           "support server or by using the `bug-report` command.", false);
                eb.addField("Error", t.getMessage(), false);
                eb.setColor(Color.RED);
                return eb.build();
            }
            
        } //ef
        
        //store
        for(String s : splitCheck) {
            
            String token = ConfigTokens.getVaultKey(s.split(":")[0]);
            String val = s.split(":")[1];
            if(ConfigTokens.getTransform().containsKey(token)) {
                String[] get = ConfigTokens.getTransform().get(token);
                for(int i = 0; i < get.length; i += 2) {
                    val = val.replace(get[i], get[i + 1]);
                }
            }
            
            Vault.storeUserDataLocal(
                    null,
                    message.getGuild().getId(),
                    token,
                    val
            );
        }
        
        eb.addField("Config", "Congrats!  Your server has been easily configured with the `config` command.\n" +
                                      "You can run this command again if you want to change the configuration.\n", false);
        eb.setColor(Color.GREEN);
        BotClass.nullifyPrefixCacheSpecific(message.getGuild().getId());
        Starboard.voidCacheForGuild(message.getGuild().getId());
        return eb.build();
    }
    
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("config", UBPerm.ADMIN, this);
        Manual.setHelpPage("config", "The config command allows for easy server setup.\n" +
                                             "You can get a config command generated for you here:" +
                                             "\n" +
                                             CONFIG_GENERATOR_URL +
                                             "\n" +
                                             "This command requires administrator permissions on the server," +
                                             " as the command can make arbitrary bot configuration changes.");
    }
}
