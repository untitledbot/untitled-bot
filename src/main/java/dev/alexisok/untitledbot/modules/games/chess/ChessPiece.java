package dev.alexisok.untitledbot.modules.games.chess;

/**
 * @author AlexIsOK
 * @since 1.3.24
 */
public enum ChessPiece {
    W_PAWN(1, "pawn"), W_ROOK(1, "rook"), W_KNIGHT(1, "knight"), W_BISHOP(1, "bishop"), W_QUEEN(1, "queen"), W_KING(1, "king"),
    B_PAWN(2, "pawn"), B_ROOK(2, "rook"), B_KNIGHT(2, "knight"), B_BISHOP(2, "bishop"), B_QUEEN(2, "queen"), B_KING(2, "king"),
    AIR(0, "empty");
    
    public final int side;
    public final String name;
    
    ChessPiece(int i, String name) {
        this.side = i;
        this.name = name;
    }
}
