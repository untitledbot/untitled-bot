package dev.alexisok.untitledbot.dashboard.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.User;

@Data
@AllArgsConstructor
public final class Session {
    
    //the id of the session
    private final long sessionID;
    
    //the user this session belongs to
    private final User user;
    
    //time in ms when this session expires
    private final long validUntil;
    
}
