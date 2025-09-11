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
        int x;
        if (this.pieceColor == ChessGame.TeamColor.WHITE){ x = 1; } else { x = -1; } // determining direction
        if (((this.pieceColor == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) || (this.pieceColor == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7))
                && (board.getPiece(new ChessPosition(myPosition.getRow() + x,myPosition.getColumn())) == null)){
            addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + (x * 2), myPosition.getColumn(), false);
        } // ^
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn(), false);
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn() + 1, true);
        addPawnMoveIfValid(board, myPosition, moves, myPosition.getRow() + x, myPosition.getColumn() - 1, true);
        return moves;
    }



    // =============== Get lines of Moves =============== //
    private void getHorizontals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        findMoveLines(board, myPosition, moves, 1, 0);
        findMoveLines(board, myPosition, moves, 2, 0);
        findMoveLines(board, myPosition, moves, 0, 1);
        findMoveLines(board, myPosition, moves, 0, 2);
    }

    private void getDiagonals(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves) {
        findMoveLines(board, myPosition, moves, 1, 1);
        findMoveLines(board, myPosition, moves, 1, 2);
        findMoveLines(board, myPosition, moves, 2, 2);
        findMoveLines(board, myPosition, moves, 2, 1);
    }

    private void findMoveLines(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int xDir, int yDir) {
        int x = myPosition.getRow();
        int y = myPosition.getColumn();
        if (xDir == 1) { x++; } else if (xDir == 2) { x--; }
        if (yDir == 1) { y++; } else if (yDir == 2) { y--; }
        while (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possible_move = new ChessPosition(x,y);
            //System.out.println(possible_move);
            ChessPiece piece_at_move = board.getPiece(possible_move);
            if (piece_at_move == null) {
                moves.add(new ChessMove(myPosition, possible_move, null));
            }
            else {
                if (piece_at_move.pieceColor != this.pieceColor) { moves.add(new ChessMove(myPosition, possible_move, null)); }
                break;
            }
            if (xDir == 1) { x++; } else if (xDir == 2) { x--; }
            if (yDir == 1) { y++; } else if (yDir == 2) { y--; }
        }
    }


    // =============== Other Move Checks =============== //
    private void addMoveIfValid(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int x, int y){
        if (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possible_move = new ChessPosition(x,y);
            ChessPiece piece_at_move = board.getPiece(possible_move);
            if (piece_at_move == null || piece_at_move.pieceColor != this.pieceColor) {
                moves.add(new ChessMove(myPosition, possible_move, null));
            }
        }
    }

    private void addPawnMoveIfValid(ChessBoard board, ChessPosition myPosition, HashSet<ChessMove> moves, int x, int y, boolean diagonal){
        if (x <= 8 && y <= 8 && x >= 1 && y >= 1 ) {
            ChessPosition possible_move = new ChessPosition(x,y);
            ChessPiece piece_at_move = board.getPiece(possible_move);
            if ((piece_at_move != null && piece_at_move.pieceColor != this.pieceColor && diagonal) || (piece_at_move == null && !diagonal)) {
                if (x == 8 || x == 1) { promotePawn(myPosition, possible_move, moves); }
                else { moves.add(new ChessMove(myPosition, possible_move, null)); }
            }
        }
    }

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
}
