package dev.alexisok.untitledbot.modules.election;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlexIsOK
 */
class ElectionObject {
    
    @Getter@Setter
    private boolean election;
    
    protected ElectionObject(boolean election) {
        this.election = election;
    }
    
}
