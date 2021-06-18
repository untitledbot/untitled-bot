package dev.alexisok.untitledbot.modules.rank;

import dev.alexisok.untitledbot.command.CommandRegistrar;
import dev.alexisok.untitledbot.command.EmbedDefaults;
import dev.alexisok.untitledbot.command.Manual;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.plugin.UBPlugin;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Set the color of the rank background
 * @since 1.4.0
 */
public final class RankColor extends UBPlugin {
    
    private static final Map<String, Color> COLORS = new HashMap<>();
    
    static {
        COLORS.put("RED", Color.RED);
        COLORS.put("ORANGE", Color.ORANGE);
        COLORS.put("YELLOW", Color.YELLOW);
        COLORS.put("GREEN", Color.GREEN);
        COLORS.put("BLUE", Color.BLUE);
        COLORS.put("PURPLE", new Color(0x800080));
        COLORS.put("WHITE", Color.WHITE);
        COLORS.put("BLACK", Color.BLACK);
        COLORS.put("BLURPLE", new Color(0x7289DA));
        
        //ugly new branding colors
        COLORS.put("FUCHSIA", new Color(0xEB459E));
        COLORS.put("VOMIT", new Color(0x5865F2));
    }
    
    /**
     * Get a color by its name.
     * @param name the name of the color.
     * @return the {@link Color}, or {@code null} if there was no corresponding color.
     */
    @Nullable
    @Contract(pure = true)
    private static Color getColorByName(@NotNull String name) {
        return COLORS.get(name.toUpperCase());
    }
    
    @Override
    public @NotNull MessageEmbed onCommand(String[] args, @NotNull Message message) {
        
        EmbedBuilder eb = new EmbedBuilder();
        EmbedDefaults.setEmbedDefaults(eb, message);
        
        Color picked = null;
        
        if(args.length == 2)
            picked = getColorByName(args[1]);
        
        if(picked == null && (args.length != 2 || !args[1].toLowerCase().matches("^[#]?[0-9a-f]{6}"))) {
            eb.setColor(Color.RED);
            eb.setTitle("Rank Color");
            eb.setDescription("Please input a valid color to use (ex. #7F8E58) or a color name (white, green, orange, etc.)\n" +
                    "To find a color, use [this site](https://colorpicker.me) and copy the `Hex Code` field when you pick your color.");
            return eb.build();
        }
        if(picked == null) {
            String code = args[1].toLowerCase();
            
            //add # to code if it is not present
            if (!code.startsWith("#"))
                code = "#" + code;
            
            Vault.storeUserDataLocal(message.getAuthor().getId(), message.getGuild().getId(), "rank-bg.color", code);
            
            eb.setColor(Color.decode(code));
        } else {
            Vault.storeUserDataLocal(message.getAuthor().getId(),
                    message.getGuild().getId(),
                    "rank-bg.color",
                    String.format("#%06x", picked.getRGB() & 0x00FFFFFF));
            eb.setColor(picked);
        }
        
        eb.setTitle("Rank Color");
        eb.setDescription("Your rank card color has been set.  Use `rank` to check it out!");
        return eb.build();
    }
    
    @Override
    public void onRegister() {
        CommandRegistrar.register("rank-color", this);
        Manual.setHelpPage("rank-color", "Set the color of your rank card.\n" +
                "Usage: `rank-color <hex>`\n\n" +
                "To get the <hex> code, [pick a color from here](https://colorpicker.me)");
        
        //who in their right mind thought that adding a `u` to color was a good idea
        CommandRegistrar.registerAlias("rank-color", "rankcolor", "rank-colour", "rankcolour", "rank-col", "rankcol");
    }
}
