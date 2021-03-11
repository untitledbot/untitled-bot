package dev.alexisok.untitledbot.modules.dash;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

@Data
public final class OAuthData {
    
    @NotNull
    private final String access_token, scope;
    
    private final long sessionToken;
    
    @NotNull
    private final User user;
    
    //store the user's ip address to prevent session theft.
    @NotNull
    private final String IPAddress;
    
    private boolean acceptedCookies = false;
}
