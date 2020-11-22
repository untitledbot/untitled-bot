package dev.alexisok.untitledbot.modules.images.apiflipnote;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apache.commons.lang3.ArrayUtils;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
public final class DoImageThingFlip {
    
    private DoImageThingFlip(){}
    
    private static final String AUTH;
    
    static {
        //load the authorization
        String s;
        try(FileInputStream fis = new FileInputStream("./secrets.properties")) {
            Properties p = new Properties();
            p.load(fis);
            s = p.getProperty("flipnote-key");
        } catch(IOException ignored) {
            s = "";
        }
        AUTH = s;
    }
    
    @RegExp
    private static final String CDN_REGEX = "(cdn|media).(discordapp.com/)";
    
    /**
     * Generate a {@link net.dv8tion.jda.api.entities.MessageEmbed} with the provided information.
     *
     * @param relativePath the path as it appears relative to https://api.alexflipnote.dev/
     *                     Example: {@code bad?image=%s}
     * @param eb the base embed builder
     * @param message the {@link Message} that was sent.
     * @return the message embed ready to send
     */
    public static synchronized MessageEmbed generateImage(@NotNull String relativePath,
                                                          @NotNull EmbedBuilder eb,
                                                          @NotNull Message message,
                                                          @NotNull String[] args) {
        
        //jda handles stopping typing
        message.getChannel().sendTyping().queue();
        
        if(message.getAttachments().size() == 1) {
            if (!message.getAttachments().get(0).isImage()) {
                eb.setTitle("Error");
                eb.setDescription("Please attach an *image* for the file to use.");
                return eb.build();
            }
            
//            message.getChannel().sendFile();
            
            String returnString = download(
                    String.format(
                            "https://api.alexflipnote.dev/" + relativePath,
                            message.getAttachments().get(0).getUrl()),
                    message.getId()
            );
            
            message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
            
            return null;
        } else if(message.getMentionedMembers().size() == 1) {
            Member m = message.getGuild().getMemberById(message.getMentionedMembers().get(0).getId());
            if (m != null) {
                String returnString = download(
                        String.format(
                                "https://api.alexflipnote.dev/" + relativePath,
                                m.getUser().getEffectiveAvatarUrl()),
                        message.getId()
                );
                message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                return null;
            }
        
        } else if(args.length == 2 && args[1].matches(Message.JUMP_URL_PATTERN.pattern())) { //message link
            String[] split = args[1].split("/");
            TextChannel tc = message.getGuild().getTextChannelById(split[split.length - 2]);
            a: if (tc != null) {
                Message m = tc.getHistory().getMessageById(split[split.length - 1]);
                List<Message.Attachment> attachments = m.getAttachments();
                if (attachments.size() == 0) {
                    String returnString = download(
                            String.format(
                                    "https://useless-api--vierofernando.repl.co/" + relativePath,
                                    deAnimate(m.getAuthor().getEffectiveAvatarUrl())),
                            message.getId()
                    );

                    message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                    break a;
                }
                
                String returnString = download(
                        String.format(
                                "https://useless-api--vierofernando.repl.co/" + relativePath,
                                m.getAttachments().get(0).getUrl()),
                        message.getId()
                );
                
                message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                return null;
            }
        } else if(args.length == 2 && args[1].matches(CDN_REGEX)) {
            String returnString = download(
                    String.format(
                            "https://useless-api--vierofernando.repl.co/" + relativePath,
                            args[1]),
                    message.getId()
            );
            
            message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
            return null;
        } else if(args.length == 2 && args[1].matches("^[\\^]{1,20}")) {
            message.getChannel().getHistoryBefore(message, args[1].length()).queue(complete -> {
                Message m = complete.getRetrievedHistory().get(args[1].length() - 1);
                List<Message.Attachment> attachments = m.getAttachments();
                if(attachments.size() == 0) {
                    String returnString = download(
                            String.format(
                                    "https://api.alexflipnote.dev/" + relativePath,
                                    deAnimate(m.getAuthor().getEffectiveAvatarUrl())),
                            message.getId()
                    );
                    
                    message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                    return;
                }
                
                String returnString = download(
                        String.format(
                                "https://api.alexflipnote.dev/" + relativePath,
                                m.getAttachments().get(0).getUrl()),
                        message.getId()
                );
                
                message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
            });
            return null;
        } else if(args.length >= 2) { //other matches (id, nickname, username)
            Member m = null;
            if(args[1].matches("^[0-9]")) {
                m = message.getGuild().getMemberCache().getElementById(args[1]);
            }
            if(m == null) {
                try {
                    m = message.getGuild().getMemberCache().getElementsByNickname(args[1], true).get(0);
                } catch(IndexOutOfBoundsException ignored) {}
            }
            if(m != null) {
                String returnString = download(
                        String.format(
                                "https://api.alexflipnote.dev/" + relativePath,
                                m.getUser().getEffectiveAvatarUrl()),
                        message.getId()
                );
                message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
                return null;
            }
        }
        String returnString = download(
                String.format(
                        "https://api.alexflipnote.dev/" + relativePath,
                        deAnimate(message.getAuthor().getEffectiveAvatarUrl())
                ),
                message.getId()
        );
        message.getChannel().sendFile(new File(returnString)).queue(r -> new File(returnString).delete());
        return null;
    }
    
    @NotNull
    @Contract(pure = true)
    private static String deProxy(@NotNull String proxiedURL) {
        String[] args = proxiedURL.split("/");
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("https"))
                ArrayUtils.shift(args, i);
        }
        return String.join("/", args);
    }
    
    /**
     * Removes an animated avatar
     * @param animatedURL the URL that is animated
     * @return the non-animated URL
     */
    private static String deAnimate(@NotNull String animatedURL) {
        return animatedURL.replace("a_", "").replace("gif", "png");
    }

    @Nullable
    @Contract(pure = true)
    protected static synchronized String download(@NotNull String urlStr, @NotNull String uniqueID) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch(MalformedURLException ignored) {
            return null;
        }
        
        try {
            URLConnection con = url.openConnection();
            con.setRequestProperty("Authorization", AUTH);
            InputStream in = con.getInputStream();
            Files.copy(in, Paths.get("./tmp/" + uniqueID + ".png"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "./tmp/" + uniqueID + ".png";
    }
}
