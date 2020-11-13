package dev.alexisok.untitledbot.modules.election;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public final class ElectionKernel {
    
    //instance of this class.
    public static final ElectionKernel INSTANCE;
    
    //guild id, elections
    private final HashMap<String, ElectionObject> ELECTIONS;
    
    static {
        INSTANCE = new ElectionKernel();
    }
    
    private ElectionKernel() {
        this.ELECTIONS = new HashMap<>();
    }
    
    
    /**
     * Save data relating to the election.
     *
     * Saves as a map
     *
     * @param data the data to save
     * @param key the key to save the data under
     * @param guildID the id of the guild
     * @return true if it was saved, false otherwise.
     */
    @Contract
    protected synchronized boolean saveElectionData(@NotNull String data, @NotNull String key, @NotNull String guildID) {
        try {
            FileOutputStream fos = new FileOutputStream(String.format("./elections/%s.properties", guildID));
            
            Properties p = new Properties();
            
            if (new File(String.format("./elections/%s.properties", guildID)).exists())
                p.load(new FileInputStream(String.format("./elections/%s.properties", guildID)));
            
            p.setProperty(key, data);
            p.store(fos, "election data");
            fos.close();
            return true;
        } catch(Throwable ignored) {
            return false;
        }
    }

    /**
     * Get the data from an election.
     * 
     * @param key the data key
     * @param guildID the guild id
     * @return the data
     */
    @Nullable
    @Contract(pure = true)
    protected synchronized String getElectionData(@NotNull String key, @NotNull String guildID) {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(String.format("./elections/%s.properties", guildID)));
            return p.getProperty(key);
        } catch(Throwable ignored) {
            return null;
        }
    }
    
    /**
     * Get the data from an election, or fallback if it is null.
     * 
     * @param key the data key
     * @param guildID the guild id
     * @param fallback the fallback if data is null
     * @return the data or the fallback if data is null
     */
    @NotNull
    @Contract(pure = true)
    protected synchronized String getElectionData(@NotNull String key, @NotNull String guildID, @NotNull String fallback) {
        String s = this.getElectionData(key, guildID);
        return s == null ? fallback : s;
    }
    
    /**
     * If the guild has an ongoing election.
     * @param g the guild to check.
     * @return {@code true} if there is an ongoing election, {@code false} otherwise.  Will also return {@code false} if the guild is not found.
     */
    @Contract(pure = true)
    public boolean hasElection(Guild g) {
        return this.hasElection(g.getId());
    }
    
    /**
     * If the guild has an ongoing election.
     * @param guildID the guild to check by id.
     * @return {@code true} if there is an ongoing election, {@code false} otherwise.  Will also return {@code false} if the guild is not found.
     */
    @Contract(pure = true)
    public boolean hasElection(@NotNull String guildID) {
        if(this.ELECTIONS.containsKey(guildID)) {
            return this.ELECTIONS.get(guildID).isElection();
        }
        return false;
    }
    
    /**
     * Reset an election by the guild
     * @param guild the guild
     */
    @Contract
    public boolean resetElection(@NotNull Guild guild) {
        return this.resetElection(guild.getId());
    }
    
    /**
     * Reset an election by the guild ID
     * @param guildID the ID of the guild.
     */
    @Contract
    public boolean resetElection(@NotNull String guildID) {
        this.ELECTIONS.remove(guildID);
        return this.eraseData(guildID);
    }
    
    /**
     * Delete the election data for a guild
     * @param guildID the id of the guild
     * @return true if it was deleted, false otherwise.
     * @see File#delete()
     */
    @Contract
    private boolean eraseData(@NotNull String guildID) {
        this.ELECTIONS.remove(guildID);
        return new File(String.format("./elections/%s.properties", guildID)).delete();
    }
    
    /**
     * Set the amount of times users can change their vote in the election.
     * @param guild the guild.
     * @param amount the amount of times they can change.
     */
    @Contract
    public boolean setChangeCount(@NotNull Guild guild, int amount) {
        return this.setChangeCount(guild.getId(), amount);
    }
    
    /**
     * Set the amount of times users can change their vote in the election.
     * @param guildID the guild.
     * @param amount the amount of times they can change.
     */
    @Contract
    public boolean setChangeCount(@NotNull String guildID, int amount) {
        return this.saveElectionData("" + amount, "vote.change", guildID);
    }

    /**
     * Close an election so no more people can join as candidates.
     * @param guild the guild to close.
     * @param state true if it is closed, false otherwise.
     * @return true if it was closed, false if there was an error.
     */
    @Contract
    public boolean close(@NotNull Guild guild, boolean state) {
        return this.close(guild.getId(), state);
    }

    /**
     * Closes an election so no more people can join as candidates.
     * @param id the ID of the guild to close.
     * @param state true if ti is closed, false otherwise.
     * @return true if it was closed, false if there was an error.
     */
    @Contract
    private boolean close(@NotNull String id, boolean state) {
        return this.saveElectionData("closed", "" + state, id);
    }
    
    /**
     * End an election so people can no longer vote.
     * @param guild the guild to end.
     * @param state true if it is ended, false otherwise.
     * @return true if it was closed, false if there was an error.
     */
    @Contract
    public boolean end(@NotNull Guild guild, boolean state) {
        return this.end(guild.getId(), state);
    }
    
    /**
     * End an election so people can no longer vote.
     * @param id the guild to end by id.
     * @param state true if it is ended, false otherwise.
     * @return true if it was closed, false if there was an error.
     */
    @Contract
    private boolean end(@NotNull String id, boolean state) {
        return this.saveElectionData("ended", "" + state, id);
    }

    /**
     * Stops an election.
     * @param guild the guild ID
     * @return true if it was stopped false if there was an error.
     */
    @Contract
    public boolean stop(Guild guild) {
        return this.saveElectionData("stopped", "true", guild.getId());
    }

    /**
     * Is the election stopped?
     * @return true if it is stopped false if it is not
     */
    @Contract
    public boolean isStopped(@NotNull String guildID) {
        return this.getElectionData("stopped", guildID, "false").equalsIgnoreCase("true");
    }
}
