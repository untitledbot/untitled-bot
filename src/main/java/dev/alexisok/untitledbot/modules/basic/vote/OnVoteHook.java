package dev.alexisok.untitledbot.modules.basic.vote;

import dev.alexisok.untitledbot.Main;
import dev.alexisok.untitledbot.logging.Logger;
import dev.alexisok.untitledbot.util.hook.VoteHook;
import dev.alexisok.untitledbot.util.vault.Vault;
import net.dv8tion.jda.api.entities.User;

/**
 * @author AlexIsOK
 * @since 1.3.25
 */
public final class OnVoteHook implements VoteHook {
    
    @Override
    public void onVote(long userID) {
        Logger.log("Incoming vote from " + userID);
        User u = Main.getUserById(userID);
        
        if(u == null) {
            Logger.log("User " + userID + " isn't getting their vote reward :(");
            return;
        }
        
        if(u.isBot()) {
            Logger.log("Somehow, a bot (" + userID + ") voted for the bot.");
            return;
        }
        
        String hasVoted = Vault.getUserDataLocalOrDefault(String.valueOf(userID), null, "vote.lecture", "false");
        
        if(hasVoted.equals("false"))
            return;
        
        int currentKeys = Integer.parseInt(Vault.getUserDataLocalOrDefault(String.valueOf(userID), null, "shop.item", "0"));
        Vault.storeUserDataLocal(String.valueOf(userID), null, "vote.keys", String.valueOf(currentKeys + 1));
        
        u.openPrivateChannel().queue(r -> {
            r.sendMessage("Thank you for voting for untitled-bot!  You have received one voting key.\n" +
                    "\nYou can see your inventory at any time using the `inv` command" +
                    "NOTE: this message will not appear again for you.").queue();
            Vault.storeUserDataLocal(String.valueOf(userID), null, "vote.lecture", "true");
        });
    }
}
