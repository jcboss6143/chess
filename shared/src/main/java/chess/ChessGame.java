package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceAtPosition = board.getPiece(startPosition);
        if (pieceAtPosition == null) { return null; }
        Collection<ChessMove> possibleMoves = pieceAtPosition.pieceMoves(board, startPosition);
        Collection<ChessMove> allValidMoves = new HashSet<ChessMove>();

        for (ChessMove moveToTest: possibleMoves) {
            ChessBoard boardCopy = board.clone(); // copy so we have original board after testing the move
            makeTrustedMove(moveToTest, pieceAtPosition);
            if (!isInCheck(pieceAtPosition.getTeamColor())) { allValidMoves.add(moveToTest); } // adds move if it doesn't put player in check
            setBoard(boardCopy); // resets board back to before the move was made
        }
        return allValidMoves;
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> potentialMoves = validMoves(move.getStartPosition());
        if (potentialMoves == null || !potentialMoves.contains(move)) {
            throw new InvalidMoveException("Please provide a valid move"); }
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        makeTrustedMove(move, pieceToMove);

    }

    private void makeTrustedMove(ChessMove move, ChessPiece pieceToMove) {
        board.removePiece(move.getEndPosition());
        board.removePiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), pieceToMove);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> teamPieces = getPieceLocations(teamColor);
        ChessPosition kingPosition = findPiece(teamColor, ChessPiece.PieceType.KING);
        for (ChessPosition currentPiecePosition: teamPieces) {
            ChessPiece currentPiece = board.getPiece(currentPiecePosition);
            Collection<ChessMove> possibleMoves = currentPiece.pieceMoves(board, currentPiecePosition);
            //continuing working
        }
        return true;
    }


    private Collection<ChessPosition> getPieceLocations (TeamColor teamColor) {
        HashSet<ChessPosition> teamPieces = new HashSet<>();
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition currentPosition = new ChessPosition(x,y);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                    teamPieces.add(currentPosition);
                }
            }
        }
        return teamPieces;
    }


    private ChessPosition findPiece(TeamColor teamColor, ChessPiece.PieceType pieceType) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                ChessPosition currentPosition = new ChessPosition(x,y);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor && currentPiece.getPieceType() == pieceType) {
                    return currentPosition;
                }
            }
        }
        return null;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
