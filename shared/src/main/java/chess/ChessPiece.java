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
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

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


    // =============== Piece moves functions =============== //
    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        return getDiagonals(board, myPosition, moves);
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        return getHorizontals(board, myPosition, moves);
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        moves = getHorizontals(board, myPosition, moves);
        return getDiagonals(board, myPosition, moves);
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() + 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() + 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 2, myPosition.getColumn() - 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 2, myPosition.getColumn() - 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 2);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 2);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 2);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 2);
        return moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() + 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() + 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn() - 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn() - 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() + 1, myPosition.getColumn());
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow() - 1, myPosition.getColumn());
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() + 1);
        moves = addMoveIfValid(board, myPosition, moves, myPosition.getRow(), myPosition.getColumn() - 1);
        return moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        var moves = new HashSet<ChessMove>();
        // add pawn moves here
        return moves;
    }



    // =============== Get lines of Moves =============== //
    private HashSet<ChessMove> getHorizontals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        moves = findMoveLines(board, myPosition, moves, 1, 0);
        moves = findMoveLines(board, myPosition, moves, 2, 0);
        moves = findMoveLines(board, myPosition, moves, 0, 1);
        return findMoveLines(board, myPosition, moves, 0, 2);
    }

    private HashSet<ChessMove> getDiagonals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        moves = findMoveLines(board, myPosition, moves, 1, 1);
        moves = findMoveLines(board, myPosition, moves, 1, 2);
        moves = findMoveLines(board, myPosition, moves, 2, 2);
        return findMoveLines(board, myPosition, moves, 2, 1);
    }

    private HashSet<ChessMove> findMoveLines(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int xDir, int yDir) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        while (x < 8 && y < 8 && x > 1 && y > 1 ) {
            if (xDir == 1) { x++; } else if (xDir == 2) { x--; }
            if (yDir == 1) { y++; } else if (yDir == 2) { y--; }
            ChessPosition possible_move = new ChessPosition(x,y);
            //System.out.println(possible_move);
            ChessPiece piece_at_move = board.getPiece(possible_move);
            if (piece_at_move == null) {
                moves.add(new ChessMove(myPosition, possible_move, this.type));
            }
            else {
                if (piece_at_move.pieceColor != this.pieceColor) { moves.add(new ChessMove(myPosition, possible_move, this.type)); }
                break;
            }
        }
        return moves;
    }


    // =============== Other Move Checks =============== //
    private HashSet<ChessMove> addMoveIfValid(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int x, int y){
        if (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possible_move = new ChessPosition(x,y);
            if (board.getPiece(possible_move).pieceColor == this.pieceColor ) { return moves; }
            moves.add(new ChessMove(myPosition, possible_move, this.type));
        }
        return moves;
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
}
