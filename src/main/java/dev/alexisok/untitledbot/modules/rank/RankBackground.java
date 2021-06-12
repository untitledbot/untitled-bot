package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.BotClass;
import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Set the background of a rank card
 * @since 1.4.0
 */
public final class RankBackground extends UBPlugin {
    
    @Override
    public MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        boolean allowed = Boolean.parseBoolean(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(), null, "rank-bg.allowed", "true"));
        
        if(!allowed) {
            eb.addField("Disallowed.", "You are not allowed to set a custom background, as you may have uploaded NSFW or illegal content prior.", false);
            return eb.build();
        }
        
        boolean agreed = Boolean.parseBoolean(Vault.getUserDataLocalOrDefault(message.getAuthor().getId(),
                null,
                "rank-bg.agreed",
                "false"));
        
        if(agreed) {
            if(message.getAttachments().size() != 1) {
                return eb.setTitle("Rank Background")
                        .setColor(Color.RED)
                        .setDescription("Couldn't find an image, did you upload one along with the command?")
                        .build();
            }
            if(!message.getAttachments().get(0).isImage()) {
                return eb.setTitle("Rank Background")
                        .setColor(Color.RED)
                        .setDescription("Hmm... that attachment doesn't look like an image to me.")
                        .build();
            }
            
            if(message.getAttachments().get(0).getSize() > 10485760) { //10 mb
                return eb.setTitle("Rank Background")
                        .setColor(Color.RED)
                        .setDescription("Rank Background images must be under 10MB.")
                        .build();
            }
            
            message.getAttachments().get(0)
                    .downloadToFile(String.format("./tmpbg/%s-%s.%s", message.getAuthor().getId(), message.getGuild().getId(), message.getAttachments().get(0).getFileExtension()))
                    .whenComplete((file, throwable) -> {
                        
                        try {
                            BufferedImage image = ImageIO.read(file);
                            
                            image = resize(image);
                            
                            new File(String.format("./backgrounds/%s/", message.getGuild().getId())).mkdirs();
                            
                            ImageIO.write(image, "png", new File(String.format("./backgrounds/%s/%s.png",
                                    message.getGuild().getId(),
                                    message.getAuthor().getId())));
                            
                            message.reply(RankImageRender.render(message.getAuthor().getId(), message.getGuild().getId(), message.getIdLong(), message))
                                    .mentionRepliedUser(false)
                                    .queue();
                            
                            Main.getTextChannelById("788643002280968192")
                                    .sendFile(new File(String.format("./backgrounds/%s/%s.png",
                                            message.getGuild().getId(),
                                            message.getAuthor().getId())), String.format("U-%s_G-%s.png", message.getAuthor().getId(), message.getGuild().getId()))
                                    .queue();
                            
                        } catch (IOException e) {
                            e.printStackTrace();
                            message.reply("There seems to be an error with the rank background, please report this if it happens again.")
                                    .mentionRepliedUser(false)
                                    .queue();
                        }
                        
                    });
            
            return eb.setTitle("Rank Background")
                    .setColor(Color.GREEN)
                    .setDescription("Your background is processing, please wait a few seconds for it to complete.\n\n" +
                            "You can also use the `rank-color` command to change the color of the text on your rank card.")
                    .build();
            
        } else {
            if(args.length == 2 && args[1].equalsIgnoreCase("agree")) {
                Vault.storeUserDataLocal(message.getAuthor().getId(), null, "rank-bg.agreed", "true");
                return eb.setTitle("Rank Background")
                        .setDescription("Thank you for agreeing.  You may now upload a custom background.")
                        .setColor(Color.GREEN)
                        .build();
            }
            return eb.setTitle("Rank Background")
                    .setDescription("You must agree to the following rules to use this command:\n" +
                            "1. You will not upload NSFW or illegal content\n" +
                            "2. You will not upload content that could cause harm\n" +
                            "3. The background is only available in the server you upload it to\n" +
                            "\nNote that content may be forwarded to Discord if it is illegal.\n\n" +
                            "If you agree, type `" + BotClass.getPrefixNice(message.getGuild().getId()) + "rank-bg agree`")
                    .setColor(Color.YELLOW)
                    .build();
        }
        
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-bg", this);
        Manual.setHelpPage("rank-bg", "Set the background of the rank command.\n" +
                "Usage:\n" +
                "`%srank-bg` with PNG attached.\n\n" +
                "The recommended size for backgrounds are 800w and 300h, and images will be scaled accordingly.");
        CommandRegistrar.registerAlias("rank-bg", "rankbg", "rank-background", "rank-image");
    }
    
    /**
     * Resize an image to the appropriate dimensions.
     * @param image the image to scale down.
     * @return the scaled down image.
     */
    @NotNull
    @Contract(pure = true)
    private static BufferedImage resize(@NotNull BufferedImage image) {
        BufferedImage newImage = new BufferedImage(800, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D gd = newImage.createGraphics();
        gd.drawImage(image, 0, 0, 800, 300, null);
        gd.dispose();
        return newImage;
    }
}
