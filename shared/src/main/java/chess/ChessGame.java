package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {
        board.resetBoard();
    }



    // =============== Turns =============== //


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



    // =============== Moves =============== //


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // finding all possible moves
        ChessPiece pieceAtPosition = board.getPiece(startPosition);
        if (pieceAtPosition == null) { return null; }
        Collection<ChessMove> possibleMoves = pieceAtPosition.pieceMoves(board, startPosition);
        Collection<ChessMove> nonValidMoves = new HashSet<>();

        // goes through all possible moves and adds them to nonValidMoves if it puts the king in check
        for (ChessMove moveToTest: possibleMoves) {
            ChessBoard boardCopy = board.clone(); // copy so we have original board after testing the move
            makeTrustedMove(moveToTest, pieceAtPosition);
            if (isInCheck(pieceAtPosition.getTeamColor())) {
                nonValidMoves.add(moveToTest); } // adds move if it puts player in check
            setBoard(boardCopy); // resets board back to before the move was made
        }

        // removing all invalid moves from possibleMoves
        for (ChessMove invalidMove: nonValidMoves) {
            possibleMoves.remove(invalidMove); }
        return possibleMoves;
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> potentialMoves = validMoves(move.getStartPosition()); // gets all possible valid moves

        // verifies that the move provided is a valid move
        if (potentialMoves == null || !potentialMoves.contains(move)) {
            throw new InvalidMoveException("Please provide a valid move"); }

        // verifies the piece they tried to move was their own
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("That's not your piece silly"); }

        // moving the piece and switching whose turn it is
        makeTrustedMove(move, pieceToMove);
        if (teamTurn == TeamColor.WHITE) { setTeamTurn(TeamColor.BLACK); }
        else { setTeamTurn(TeamColor.WHITE); }
    }


    /**
     * Makes a move that is known to be possible (this move may not be a valid move)
     * =========================================================================================================
     *  NOTICE: this should only be called if you got the 'move' parameter from the hashset returned by calling
     *  piecemoves() on your 'pieceToMove' parameter!
     * =========================================================================================================
     *
     * @param move chess move to perform. This move should have
     *             originated from the hashset described above
     * @param pieceToMove the piece we want to move
     */
    private void makeTrustedMove(ChessMove move, ChessPiece pieceToMove) {
        board.removePiece(move.getStartPosition()); // removes the piece
        if (move.getPromotionPiece() == null) { // adds the piece
            board.addPiece(move.getEndPosition(), pieceToMove); }
        else { // adds new promotion piece
            board.addPiece(move.getEndPosition(), new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece())); }
    }


    /**
     * Determines if there are moves that can be made
     *
     * @param teamColor which team to check for possible moves
     * @return True if there is a valid move a piece can make for that team
     */
    private boolean isMovePossible(TeamColor teamColor) {
        Collection<ChessPosition> teamPieces = getPieceLocations(teamColor);
        for (ChessPosition piecePosition:teamPieces){   // loops through all pieces and checks if they return any vaid moves
            Collection<ChessMove> possibleMoves = validMoves(piecePosition);
            if (!possibleMoves.isEmpty()){ return true; }
        }
        return false;
    }



    // =============== Finding Pieces on Board =============== //


    /**
     * Returns a hashset of all black or white pieces currently on the board
     *
     * @param teamColor finds all locations of pieces that are of this color.
     */
    private Collection<ChessPosition> getPieceLocations(TeamColor teamColor) {
        HashSet<ChessPosition> teamPieces = new HashSet<>();
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {  // looping through the board
                ChessPosition currentPosition = new ChessPosition(x,y);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor) { // adds piece location if color matches
                    teamPieces.add(currentPosition); } }
        }
        return teamPieces;
    }


    /**
     * Returns the position of a desired chess piece. If the piece doesn't exist, it returns null.
     * Notice: Will only return the position of the first piece found.
     * Primarily used for finding a players king.
     *
     * @param teamColor color of the piece we want.
     * @param pieceType the type of piece we want.
     */
    private ChessPosition findMatchingPiece(TeamColor teamColor, ChessPiece.PieceType pieceType) {
        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {  // looping through the board
                ChessPosition currentPosition = new ChessPosition(x,y);
                ChessPiece currentPiece = board.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() == teamColor && currentPiece.getPieceType() == pieceType) {
                    return currentPosition; } }     // if piece has been found, returns the position
        }
        return null;    // returns nothing if the piece wasn't found
    }





    // =============== Check, Checkmate, and Stalemate =============== //


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // getting our king position
        ChessPosition kingPosition = findMatchingPiece(teamColor, ChessPiece.PieceType.KING);

        // getting the positions of all enemy pieces
        TeamColor otherTeamColor;
        if (teamColor == TeamColor.WHITE) { otherTeamColor = TeamColor.BLACK; } else { otherTeamColor = TeamColor.WHITE; }
        Collection<ChessPosition> enemyTeamPieces = getPieceLocations(otherTeamColor);

        // loops through each of the enemies pieces and checks if it has a move that can take the king
        for (ChessPosition currentEnemyPosition : enemyTeamPieces) {
            ChessPiece currentPiece = board.getPiece(currentEnemyPosition);
            Collection<ChessMove> possibleEnemyMoves = currentPiece.pieceMoves(board, currentEnemyPosition);
            for (ChessMove possibleEnemyMove: possibleEnemyMoves) {
                if (possibleEnemyMove.getEndPosition().equals(kingPosition)){ return true; } } // player is in check
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) & !isMovePossible(teamColor);
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) & !isMovePossible(teamColor);
    }





    // =============== Set and Get Board =============== //


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





    // =============== Overrides  =============== //


    @Override
    public String toString() {
        return "{" + "Turn=" + teamTurn + ", Board=" + board + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }

}
