package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() { return pieceColor; }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    // calls functions that dictate the movesets for each piece
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (this.type) {
            case BISHOP -> { return bishopMoves(board, myPosition); }
            case ROOK -> { return rookMoves(board, myPosition); }
            case QUEEN -> { return queenMoves(board, myPosition); }
            case KNIGHT -> { return knightMoves(board, myPosition); }
            case KING -> { return kingMoves(board, myPosition); }
            case PAWN -> { return pawnMoves(board, myPosition); }
            case null, default -> { return null; }
        }
    }





    // =============== Piece Rules =============== //


    // Bishop, Rook, and Queen utilize functions designed to get all valid moves that are in a row
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        getDiagonals(board, myPosition, moves);
        return moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        getHorizontals(board, myPosition, moves);
        return moves;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        getHorizontals(board, myPosition, moves);
        getDiagonals(board, myPosition, moves);
        return moves;
    }

    // Knight function check all potential locations it could move to and adds valid moves to the set
    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() + 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() + 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() - 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() - 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 2);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 2);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 2);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 2);
        return moves;
    }

    // King function check all potential locations it could move to and adds valid moves to the set
    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn());
        addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn());
        addMoveIfValid(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() + 1);
        addMoveIfValid(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() - 1);
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        int x; // x determines the pawns direction (up for White and down for black)
        if (this.pieceColor == ChessGame.TeamColor.WHITE){ x = 1; } else { x = -1; }
        // the if statement below deals with the special rule pawns have when they're moved for the first time
        if (((this.pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7))
                && (board.getPiece(new ChessPosition(myPosition.getRow() + x,myPosition.getColumn())) == null)){
            addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + (x * 2), myPosition.getColumn(), false); }
        // Default Possible moves for a Pawn
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn(), false);
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn() + 1, true);
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn() - 1, true);
        return moves;
    }





    // =============== Gets lines of Valid Moves =============== //


    // Adds lines of moves in the shape of a +
    private void getHorizontals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        findMoveLines(board, myPosition, moves, 1, 0);
        findMoveLines(board, myPosition, moves, 2, 0);
        findMoveLines(board, myPosition, moves, 0, 1);
        findMoveLines(board, myPosition, moves, 0, 2);
    }

    // Adds diagonal lines of moves in the shape of an X
    private void getDiagonals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        findMoveLines(board, myPosition, moves, 1, 1);
        findMoveLines(board, myPosition, moves, 1, 2);
        findMoveLines(board, myPosition, moves, 2, 2);
        findMoveLines(board, myPosition, moves, 2, 1);
    }

    // This function will add moves in a straight line away from the position of the current piece.
    // xDir and yDir dictate the direction of this line, and will increase the respective axis when set to 1 and
    // decrease it when set to 2 (all other values will cause the respective axis to stay the same).
    // This line of moves will stop when it reaches the end of the board or runs into another piece.
    // When the line reaches another piece, it will check if the pieces are the same color and will add a valid move
    // if they don't match.
    private void findMoveLines(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int xDir, int yDir) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        // next 2 lines are the initial shift of the x and y axis, which is performed so the line doesn't start on the current piece
        if (xDir == 1) { x++; } else if (xDir == 2) { x--; }
        if (yDir == 1) { y++; } else if (yDir == 2) { y--; }
        while (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possibleMove = new ChessPosition(x,y);
            ChessPiece pieceAtMove = board.getPiece(possibleMove);
            if (pieceAtMove == null) {
                moves.add(new ChessMove(myPosition, possibleMove, null)); } // adds move if position is empty
            else {
                if (pieceAtMove.pieceColor != this.pieceColor) { moves.add(new ChessMove(myPosition, possibleMove, null)); }
                break; }
            if (xDir == 1) { x++; } else if (xDir == 2) { x--; } // x-axis shift
            if (yDir == 1) { y++; } else if (yDir == 2) { y--; } // y-axis shift
        }
    }





    // =============== Checks if provided position is valid move =============== //


    // Function is given a possible move location (x and y) and adds it if the move is in-bounds and doesn't contain a piece of the same color
    private void addMoveIfValid(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int x, int y){
        if (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possibleMove = new ChessPosition(x,y);
            ChessPiece pieceAtMove = board.getPiece(possibleMove);
            if (pieceAtMove == null || pieceAtMove.pieceColor != this.pieceColor) {
                moves.add(new ChessMove(myPosition, possibleMove, null)); } }
    }

    // modified version of AddMoveIfValid, but it has additional checks specifically for pawns
    // these checks include diagonal captures and piece promotions
    private void addPawnMoveIfValid(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int x, int y, boolean diagonal){
        if (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possibleMove = new ChessPosition(x,y);
            ChessPiece pieceAtMove = board.getPiece(possibleMove);
            if ((pieceAtMove != null && pieceAtMove.pieceColor != this.pieceColor && diagonal) || (pieceAtMove == null && !diagonal)) {
                if (x == 8 || x == 1) { promotePawn(myPosition, possibleMove, moves); }
                else { moves.add(new ChessMove(myPosition, possibleMove, null)); } } }
    }





    // =============== Pawn Promotion =============== //


    // called by addPawnMoveIfValid when a pawn has reached either side of the board.
    private void promotePawn(ChessPosition myPosition, ChessPosition newPosition, HashSet<ChessMove> moves) {
        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
    }





    // =============== Overrides  =============== //


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return  pieceColor + "." + type;
    }
}
