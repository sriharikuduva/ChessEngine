package com.chess.engine.player;

import com.chess.engine.Alliance;
import com.chess.engine.board.Board;
import com.chess.engine.board.Move;
import com.chess.engine.pieces.King;
import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected final Board board;
    protected final King king;
    protected final Collection<Move> legalMoves;
    private final boolean isInCheck;

    public Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.king = establishKing();
        this.isInCheck = !Player.calculateAttackOnTile(this.king.getPiecePosition(), opponentMoves).isEmpty();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
    }

    protected static Collection<Move> calculateAttackOnTile(int piecePosition, Collection<Move> opponentMoves) {
        List<Move> attacks = new ArrayList<>();
        for (Move m: opponentMoves) {
            if (piecePosition == m.getDestination())
                attacks.add(m);
        }
        return ImmutableList.copyOf(attacks);
    }

    private King establishKing() {
        for (final Piece p : getActivePieces()) {
            if (p.getPieceType() == Piece.PieceType.KING) {
                return (King) p;
            }
        }
        throw new RuntimeException("Should not reach here, invalid board!!");
    }

    public boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return false;
    }

    public King getKing() {
        return this.king;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }


    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> attacksOnKing =
                Player.calculateAttackOnTile(transitionBoard.getCurrentPlayer().getOpponent().getKing().getPiecePosition(),
                transitionBoard.getCurrentPlayer().getLegalMoves());
        if (!attacksOnKing.isEmpty()) return new MoveTransition(board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);


        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    private boolean hasEscapeMoves() {
        for (Move m : legalMoves) {
            MoveTransition transition = makeMove(m);
            if (transition.getMoveStatus().isDone())
                return true;
        }
        return false;
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentLegals);
}
