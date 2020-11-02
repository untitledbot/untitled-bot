package dev.alexisok.untitledbot.modules.games.ttt;

import net.dv8tion.jda.api.entities.Message;

/**
 * @author AlexIsOK
 * @since 1.3.23
 */
final class TTTBoard {
    
    protected final String player1, player2;
    
    protected Message m;
    
    protected boolean isPlayer1Turn = true;
    
    private final byte[][] board = new byte[3][3];
    
    protected TTTBoard(String player1, String player2) {
        this.player1 = player1;
        this.player2 = player2;
    }
    
    /**
     * Set an element on the board
     * @param x the x
     * @param y the y
     * @param value the value (0 = none, 1 = player 1 (x), 2 = player 2 (o))
     */
    protected void setElement(int x, int y, byte value) {
        this.board[x][y] = value;
    }
    
    protected byte getElement(int x, int y) {
        return this.board[x][y];
    }
    
}
