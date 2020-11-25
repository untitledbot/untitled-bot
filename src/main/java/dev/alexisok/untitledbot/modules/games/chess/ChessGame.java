package dev.alexisok.untitledbot.modules.games.chess;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import static dev.alexisok.untitledbot.modules.games.chess.ChessPiece.*;

/**
 * Defines a game of chess.
 * 
 * @author AlexIsOK
 * @since 1.3.24
 */
public final class ChessGame {
    
    private static final ChessPiece[][] DEFAULT_BOARD;
    
    static {
        //default chess board when players start a new game
        DEFAULT_BOARD = new ChessPiece[][] {
                {B_ROOK, B_KNIGHT, B_BISHOP, B_KING, B_QUEEN, B_BISHOP, B_KNIGHT, B_ROOK},
                {B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN, B_PAWN},
                {AIR, AIR, AIR, AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR, AIR, AIR, AIR},
                {AIR, AIR, AIR, AIR, AIR, AIR, AIR, AIR},
                {W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN, W_PAWN},
                {W_ROOK, W_KNIGHT, W_BISHOP, W_KING, W_QUEEN, W_BISHOP, W_KNIGHT, W_ROOK}
        };
    }
    
    @Getter
    private ChessPiece[][] board;
    
    @Getter
    private final String whiteID;
    
    @Getter
    private final String blackID;
    
    /**
     * Create a new game of chess.
     * @param white the ID of the white player
     * @param black the ID of the black player
     */
    protected ChessGame(@NotNull String white, @NotNull String black) {
        this.board = DEFAULT_BOARD.clone();
        this.whiteID = white;
        this.blackID = black;
    }

    /**
     * Move a piece on the board
     * 
     * @param from the current position
     * @param to the next position
     * @throws IllegalArgumentException if the piece cannot be moved that way.
     */
    protected void movePiece(@NotNull String from, @NotNull String to, @NotNull String playerID) throws IllegalArgumentException {
        if(!from.matches("^[A-Ha-h][1-8]$") || !to.matches("^[A-Ha-h][1-8]$"))
            throw new IllegalArgumentException("Please input valid coordinates, such as A1 to A5");
//        int colTo = switch(to.charAt(0)) {
//            case 'A' -> 1;
//            case 'B' -> 2;
//            case 'C' -> 3;
//            case 'D' -> 4;
//            case 'E' -> 5;
//            case 'F' -> 6;
//            case 'G' -> 7;
//            case 'H' -> 8;
//            default -> -1;
//        };
//        
//        int colFrom = switch(from.charAt(0)) {
//            case 'A' -> 1;
//            case 'B' -> 2;
//            case 'C' -> 3;
//            case 'D' -> 4;
//            case 'E' -> 5;
//            case 'F' -> 6;
//            case 'G' -> 7;
//            case 'H' -> 8;
//            default -> -1;
//        };
//        
//        int rowTo = Character.getNumericValue(to.charAt(1));
//        int rowFrom = Character.getNumericValue(from.charAt(1));
//        
//        colTo--;
//        colFrom--;
//        
//        rowTo--;
//        rowFrom--;
//        
//        ChessPiece current = this.board[rowFrom][colFrom];
//        
//        if(current == null || current.equals(AIR))
//            throw new IllegalArgumentException("You cannot move that piece!");
//        
//        boolean is1 = this.whiteID.equalsIgnoreCase(playerID);
//        
//        if((is1 && current.side != 1) || (!is1 && current.side != 2))
//            throw new IllegalArgumentException("You cannot move that piece!");
//        
//        
//        if(current.name.equals("pawn")) {
//            if(colFrom != colTo) {
//                if(colFrom - 1 != colTo && colFrom + 1 != colTo)
//                    throw new IllegalArgumentException("Pawn cannot move more than one to the left or to the right.");
//                if(rowFrom + 1 != rowTo)
//                    throw new IllegalArgumentException("Pawn must move forward one to take a piece!");
//                
//            }
//            
//            if(rowFrom != 1) {
//                
//            }
//        }
    }
    
}
