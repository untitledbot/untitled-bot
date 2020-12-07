package dev.alexisok.untitledbot.hook;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.enums.UBPerm;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class WebhookPlugin extends UBPlugin {

    /**
     * When the `webhook` command is run by me.
     * @param strings the arguments
     * @param message the message
     * @return the embed that will be displayed to the user.
     */
    public @NotNull MessageEmbed onCommand(String[] strings, @NotNull Message message) {
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);

        eb.addField("Webhook stats", "Total votes: " + Webhook.getTotalVotes(), false);
        return eb.build();
    }

    /**
     * When the plugin is registered (after all the shards are loaded).
     */
    @Override
    public void onRegister() {
        CommandRegistrar.register("webhook", UBPerm.OWNER, this);
        Logger.log("Starting the webhook server.");
        Webhook.startServer();
        Logger.log("Done!");
    }
}
